//package net.doge.sdk.service.music.info.trackhero.nc;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.json.JsonUtil;
//import net.doge.util.core.StringUtil;
//import net.doge.util.os.DesktopUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class BugpkNcTrackHero {
//    private static BugpkNcTrackHero instance;
//
//    private BugpkNcTrackHero() {
//        initMap();
//    }
//
//    public static BugpkNcTrackHero getInstance() {
//        if (instance == null) instance = new BugpkNcTrackHero();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API
//    private final String SONG_URL_API = "https://api.bugpk.com/api/163_music?ids=%s&level=%s&type=json";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
//        qualityMap.put("jyeffect", "jyeffect");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "sky");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "jymaster");
//        qualityMap.put("dolby", "dolby");
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
//        String songBody = DesktopUtil.getRequestImpersonate(String.format(SONG_URL_API, id, qualityMap.get(quality)));
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
//        BugpkNcTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
