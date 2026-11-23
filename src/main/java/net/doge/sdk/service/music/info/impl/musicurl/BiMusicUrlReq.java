package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class BiMusicUrlReq {
    private static BiMusicUrlReq instance;

    private BiMusicUrlReq() {
    }

    public static BiMusicUrlReq getInstance() {
        if (instance == null) instance = new BiMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (哔哩哔哩)
    private final String SONG_URL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/url";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("sid", id);
        String playUrlBody = HttpRequest.get(SONG_URL_BI_API)
                .queryParams(params)
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(playUrlBody);
        return urlJson.getJSONObject("data").getJSONArray("cdns").getString(0);
    }
}
