package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.mg.MgLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;

public class MgMusicInfoReq {
    private static MgMusicInfoReq instance;

    private MgMusicInfoReq() {
    }

    public static MgMusicInfoReq getInstance() {
        if (instance == null) instance = new MgMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (咪咕) (下面那个接口能获取无版权音乐的信息)
//    private final String SONG_DETAIL_MG_API = "https://music.migu.cn/v3/api/music/audioPlayer/songs?copyrightId=%s";
    private final String SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";

    /**
     * 补充 NetMusicInfo 歌曲时长
     */
    public void fillDuration(NetMusicInfo musicInfo) {
        String songId = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_MG_API, songId))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
        if (!musicInfo.hasDuration()) musicInfo.setDuration(DurationUtil.toSeconds(data.getString("length")));
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);

        if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
        if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumId"));
        if (!musicInfo.hasDuration()) musicInfo.setDuration(DurationUtil.toSeconds(data.getString("length")));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                JSONArray imgArray = data.getJSONArray("albumImgs");
                BufferedImage albumImage = SdkUtil.getImageFromUrl(JsonUtil.isEmpty(imgArray) ? "" : imgArray.getJSONObject(imgArray.size() - 1).getString("img"));
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
        MgLyricReq.getInstance().fillLyric(musicInfo);
    }
}
