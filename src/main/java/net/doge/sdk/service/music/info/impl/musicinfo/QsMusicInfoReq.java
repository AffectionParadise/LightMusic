package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.qs.QsLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class QsMusicInfoReq {
    private static QsMusicInfoReq instance;

    private QsMusicInfoReq() {
    }

    public static QsMusicInfoReq getInstance() {
        if (instance == null) instance = new QsMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (汽水)
    private final String SONG_DETAIL_QS_API = "https://api.qishui.com/luna/h5/track?track_id=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_QS_API, id))
                .executeAsStr();
        JSONObject songJson = JSONObject.parseObject(songBody).getJSONObject("track");
        JSONObject albumJson = songJson.getJSONObject("album");

        if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("duration") / 1000);
        if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(songJson));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(songJson));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumJson.getString("name"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumJson.getString("id"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                JSONObject coverJson = albumJson.getJSONObject("url_cover");
                String uri = coverJson.getString("uri");
                String url = coverJson.getJSONArray("urls").getString(0);
                String coverImgUrl = url + uri + "~c5_500x500.jpg";
                BufferedImage albumImage = SdkUtil.getImageFromUrl(coverImgUrl);
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
        QsLyricReq.getInstance().fillLyric(musicInfo);
    }
}
