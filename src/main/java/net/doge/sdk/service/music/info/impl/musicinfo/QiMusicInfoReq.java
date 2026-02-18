package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class QiMusicInfoReq {
    private static QiMusicInfoReq instance;

    private QiMusicInfoReq() {
    }

    public static QiMusicInfoReq getInstance() {
        if (instance == null) instance = new QiMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (千千)
    private final String SONG_DETAIL_QI_API = "https://music.91q.com/v1/song/info?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲 URL 获取 API (千千)
    private final String GET_SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = SdkCommon.qiRequest(String.format(SONG_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);

        if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("albumTitle"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumAssetCode"));
        if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("pic"));
                FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                musicInfo.callback();
            });
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        String id = musicInfo.getId();
        String playUrlBody = SdkCommon.qiRequest(String.format(GET_SONG_URL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(playUrlBody);
        String lyricUrl = urlJson.getJSONObject("data").getString("lyric");
        musicInfo.setLyric(StringUtil.notEmpty(lyricUrl) ? HttpRequest.get(lyricUrl).executeAsStr() : "");
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
