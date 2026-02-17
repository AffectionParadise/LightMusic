package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.qq.QqLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class QqMusicInfoReq {
    private static QqMusicInfoReq instance;

    private QqMusicInfoReq() {
    }

    public static QqMusicInfoReq getInstance() {
        if (instance == null) instance = new QqMusicInfoReq();
        return instance;
    }

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";
    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("songinfo").getJSONObject("data");
        JSONObject trackInfo = data.getJSONObject("track_info");
        JSONObject album = trackInfo.getJSONObject("album");

        if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("interval"));
        if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(trackInfo));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(trackInfo));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(album.getString("name"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(album.getString("mid"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                // QQ 的歌曲专辑图片需要额外请求接口获得！
                BufferedImage albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("mid")));
                // 有的歌曲没有专辑，先找备份专辑图片，如果还没有就将歌手的图片作为封面
                if (albumImage == ImageConstants.DEFAULT_IMG)
                    albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("pmid")));
                if (albumImage == ImageConstants.DEFAULT_IMG)
                    albumImage = SdkUtil.getImageFromUrl(String.format(ARTIST_IMG_QQ_API, SdkUtil.parseArtistId(trackInfo)));
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
        QqLyricReq.getInstance().fillLyric(musicInfo);
    }
}
