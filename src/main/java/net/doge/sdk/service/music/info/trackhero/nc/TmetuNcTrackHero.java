package net.doge.sdk.service.music.info.trackhero.nc;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.util.core.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class TmetuNcTrackHero {
    private static TmetuNcTrackHero instance;

    private TmetuNcTrackHero() {
        initMap();
    }

    public static TmetuNcTrackHero getInstance() {
        if (instance == null) instance = new TmetuNcTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API
    private final String SONG_URL_API = "https://www.tmetu.cn/api/music/api.php?miss=songAll&id=%s&level=%s&withLyric=true";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
        qualityMap.put("jyeffect", "jyeffect");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "sky");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "jymaster");
        qualityMap.put("dolby", "dolby");
    }

    /**
     * 获取网易云音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String songBody = HttpRequest.get(String.format(SONG_URL_API, id, qualityMap.get(quality)))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String trackUrl = data.getString("audioUrl");
        return trackUrl;
    }

//    public static void main(String[] args) {
//        TmetuNcTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
