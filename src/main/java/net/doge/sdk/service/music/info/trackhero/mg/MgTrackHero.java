package net.doge.sdk.service.music.info.trackhero.mg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class MgTrackHero {
    private static MgTrackHero instance;

    private MgTrackHero() {
        initMap();
    }

    public static MgTrackHero getInstance() {
        if (instance == null) instance = new MgTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API (咪咕)
    private final String SONG_URL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/strategy/listen-url/v2.4?songId=%s&toneFlag=%s&resourceType=2";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "PQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "HQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "SQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "ZQ");
    }

    /**
     * 获取咪咕音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String urlBody = HttpRequest.get(String.format(SONG_URL_MG_API, id, quality))
                .header("aversionid", "")
                .header("token", "")
                .header("channel", "0146832")
                .header("language", "Chinese")
                .header("ua", "Android_migu")
                .header("mode", "android")
                .header("os", "Android 10")
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        return data.getString("url");
    }
}
