//package net.doge.sdk.service.music.info.impl.musicurl.track.nc;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.log.LogUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Vincentzyu233NcTrackReq {
//    private static Vincentzyu233NcTrackReq instance;
//
//    private Vincentzyu233NcTrackReq() {
//        initMap();
//    }
//
//    public static Vincentzyu233NcTrackReq getInstance() {
//        if (instance == null) instance = new Vincentzyu233NcTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (网易云)
//    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/netease.py
//    private final String SONG_URL_NC_API = "http://xwl.vincentzyu233.cn:51217/v2/music/netease?id=%s&quality=%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
//        qualityMap.put("64k", "1");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "2");
//        qualityMap.put("192k", "3");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "4");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "5");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "6");
//        qualityMap.put("jyeffect", "7");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "8");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "9");
/// /        qualityMap.put("dolby", "dolby");
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
//        try {
//            String songBody = HttpRequest.get(String.format(SONG_URL_NC_API, id, qualityMap.get(quality)))
//                    .executeAsStr();
//            JSONObject urlJson = JSONObject.parseObject(songBody);
//            if (urlJson.getIntValue("code") != 200) return "";
//            JSONObject data = urlJson.getJSONArray("data").getJSONObject(0);
//            String trackUrl = data.getString("url");
//            if (StringUtil.isEmpty(trackUrl)) return "";
//            return trackUrl;
//        } catch (Exception e) {
//            LogUtil.error(e);
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        Vincentzyu233NcTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
