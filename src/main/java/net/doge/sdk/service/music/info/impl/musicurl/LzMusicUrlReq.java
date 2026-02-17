package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

public class LzMusicUrlReq {
    private static LzMusicUrlReq instance;

    private LzMusicUrlReq() {
    }

    public static LzMusicUrlReq getInstance() {
        if (instance == null) instance = new LzMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (李志)
    private final String SONG_URL_LZ_API = "https://www.lizhinb.com/?audioigniter_playlist_id=%s";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String[] sp = id.split("_");
        String albumSongBody = HttpRequest.get(String.format(SONG_URL_LZ_API, sp[0]))
                .executeAsStr();
        JSONArray songArray = JSONArray.parseArray(albumSongBody);
        JSONObject urlJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
        return UrlUtil.encodeBlank(urlJson.getString("audio"));
    }
}
