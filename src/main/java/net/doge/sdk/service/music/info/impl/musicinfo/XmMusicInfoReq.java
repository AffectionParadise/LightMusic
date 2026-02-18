package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;

public class XmMusicInfoReq {
    private static XmMusicInfoReq instance;

    private XmMusicInfoReq() {
    }

    public static XmMusicInfoReq getInstance() {
        if (instance == null) instance = new XmMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (喜马拉雅)
    private final String SONG_DETAIL_XM_API = "https://www.ximalaya.com/revision/track/simple?trackId=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_XM_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        JSONObject trackInfo = data.getJSONObject("trackInfo");
        JSONObject albumInfo = data.getJSONObject("albumInfo");

        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(trackInfo.getString("anchorUid"));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumInfo.getString("title"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumInfo.getString("albumId"));
        if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("duration"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl("https:" + trackInfo.getString("coverPath"));
                FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                musicInfo.callback();
            });
        }
        musicInfo.setLyric("");
    }
}
