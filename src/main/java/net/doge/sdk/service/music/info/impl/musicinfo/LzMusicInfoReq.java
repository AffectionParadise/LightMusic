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

public class LzMusicInfoReq {
    private static LzMusicInfoReq instance;

    private LzMusicInfoReq() {
    }

    public static LzMusicInfoReq getInstance() {
        if (instance == null) instance = new LzMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (李志)
    private final String SINGLE_SONG_DETAIL_LZ_API = "https://www.lizhinb.com/?audioigniter_playlist_id=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String[] sp = id.split("_");
        String albumSongBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_LZ_API, sp[0]))
                .executeAsStr();
        JSONArray songArray = JSONArray.parseArray(albumSongBody);
        JSONObject songJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("cover"));
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
        String albumSongBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_LZ_API, sp[0]))
                .executeAsStr();
        JSONArray songArray = JSONArray.parseArray(albumSongBody);
        JSONObject lyricJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
        String lyric = lyricJson.getString("lyrics").replace("\r\n", "\n");
        musicInfo.setLyric(lyric);
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
