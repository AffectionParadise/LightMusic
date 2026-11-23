package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class FsMusicInfoReq {
    private static FsMusicInfoReq instance;

    private FsMusicInfoReq() {
    }

    public static FsMusicInfoReq getInstance() {
        if (instance == null) instance = new FsMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (5sing)
    private final String SONG_DETAIL_FS_API = "http://service.5sing.kugou.com/song/find";
    // 歌词 API (5sing)
    private final String LYRIC_FS_API = "http://5sing.kugou.com/fm/m/json/lrc";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("songinfo", id.replace("_", "$"));
        String songBody = HttpRequest.get(SONG_DETAIL_FS_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject data = JSONArray.parseArray(songBody).getJSONObject(0);

        if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("nickname"));
        if (!musicInfo.hasArtistId()) musicInfo.setArtist(data.getString("userid"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("avatar"));
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
        String[] sp = id.split("_");
        Map<String, Object> params = new HashMap<>();
        params.put("songType", sp[0]);
        params.put("songId", sp[1]);
        String lyricBody = HttpRequest.get(LYRIC_FS_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject lyricJson = JSONObject.parseObject(lyricBody);
        musicInfo.setLyric(lyricJson.getString("txt"));
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
