package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.kw.KwLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class KwMusicInfoReq {
    private static KwMusicInfoReq instance;

    private KwMusicInfoReq() {
    }

    public static KwMusicInfoReq getInstance() {
        if (instance == null) instance = new KwMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (酷我)
    private final String SONG_DETAIL_KW_API = "https://kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        HttpResponse resp = SdkCommon.kwRequest(String.format(SONG_DETAIL_KW_API, id)).execute();
        if (!resp.isSuccessful()) return;
        String songBody = resp.body();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");

        if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
        if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("artist").replace("&", "、"));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("artistid"));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumid"));
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
        KwLyricReq.getInstance().fillLyric(musicInfo);
    }
}
