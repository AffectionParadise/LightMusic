package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class BiMusicInfoReq {
    private static BiMusicInfoReq instance;

    private BiMusicInfoReq() {
    }

    public static BiMusicInfoReq getInstance() {
        if (instance == null) instance = new BiMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (哔哩哔哩)
    private final String SINGLE_SONG_DETAIL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/info?sid=%s";
    // 歌词 API (哔哩哔哩)
    private final String LYRIC_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/lyric?sid=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_BI_API, id))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        // 时长是毫秒，转为秒
        if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
        if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("uname"));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("uid"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("cover"));
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
        String lyricBody = HttpRequest.get(String.format(LYRIC_BI_API, id))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject lyricJson = JSONObject.parseObject(lyricBody);
        musicInfo.setLyric(lyricJson.getString("data"));
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
