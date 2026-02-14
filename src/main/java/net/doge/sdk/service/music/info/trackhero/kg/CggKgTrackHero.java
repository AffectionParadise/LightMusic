//package net.doge.sdk.service.music.info.trackhero.kg;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.sdk.util.http.HttpRequest;
//import net.doge.util.core.JsonUtil;
//import net.doge.util.core.StringUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class CggKgTrackHero {
//    private static CggKgTrackHero instance;
//
//    private CggKgTrackHero() {
//        initMap();
//    }
//
//    public static CggKgTrackHero getInstance() {
//        if (instance == null) instance = new CggKgTrackHero();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API
//    private final String SONG_URL_API = "https://music-api2.cenguigui.cn/?kg=&id=%s&type=song&format=json&level=%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        qualityMap.put("ogg", "ogg");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
//    }
//
//    /**
//     * 获取网易云音乐歌曲链接
//     *
//     * @param id      歌曲 id
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String id, String quality) {
//        String songBody = HttpRequest.get(String.format(SONG_URL_API, id, qualityMap.get(quality)))
//                .executeAsStr();
//        JSONArray data = JSONObject.parseObject(songBody).getJSONArray("data");
//        if (JsonUtil.isEmpty(data)) return "";
//        JSONObject urlJson = data.getJSONObject(0);
//        // 排除试听部分，直接换源
//        if (JsonUtil.isEmpty(urlJson.getJSONObject("freeTrialInfo"))) {
//            String url = urlJson.getString("url");
//            if (StringUtil.notEmpty(url)) return url;
//        }
//        return "";
//    }
//
//    public static void main(String[] args) {
//        CggKgTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
