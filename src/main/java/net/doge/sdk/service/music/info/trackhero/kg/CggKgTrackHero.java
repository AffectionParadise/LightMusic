package net.doge.sdk.service.music.info.trackhero.kg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.os.DesktopUtil;

import java.util.HashMap;
import java.util.Map;

public class CggKgTrackHero {
    private static CggKgTrackHero instance;

    private CggKgTrackHero() {
        initMap();
    }

    public static CggKgTrackHero getInstance() {
        if (instance == null) instance = new CggKgTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API (酷狗)
    private final String SONG_URL_KG_API = "https://music-api2.cenguigui.cn/?kg=&id=%s&type=song&format=json&level=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put("ogg", "ogg");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "hires");
    }

    /**
     * 获取酷狗音乐歌曲链接
     *
     * @param hash    歌曲 hash
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String hash, String quality) {
        String urlBody = DesktopUtil.impersonateGet(String.format(SONG_URL_KG_API, hash, qualityMap.get(quality)));
        JSONObject data = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String trackUrl = data.getString("url");
        if (StringUtil.isEmpty(trackUrl)) return "";
        return trackUrl;
    }

//    public static void main(String[] args) {
//        CggKgTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
