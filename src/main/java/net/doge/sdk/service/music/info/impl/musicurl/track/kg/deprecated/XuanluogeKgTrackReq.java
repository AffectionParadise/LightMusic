//package net.doge.sdk.service.music.info.impl.musicurl.track.kg;
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
//public class XuanluogeKgTrackReq {
//    private static XuanluogeKgTrackReq instance;
//
//    private XuanluogeKgTrackReq() {
//        initMap();
//    }
//
//    public static XuanluogeKgTrackReq getInstance() {
//        if (instance == null) instance = new XuanluogeKgTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (酷狗)
//    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/kugou.py
//    private final String SONG_URL_KG_API = "http://118.24.104.108:3456/api.php?miss=getMusicUrl&source=kugou&id=%s&level=%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "high");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "viper_atmos");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "viper_clear");
//    }
//
//    /**
//     * 获取酷狗音乐歌曲链接
//     *
//     * @param hash    歌曲 hash
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String hash, String quality) {
//        try {
//            String songBody = HttpRequest.get(String.format(SONG_URL_KG_API, hash, qualityMap.get(quality)))
//                    .executeAsStr();
//            JSONObject urlJson = JSONObject.parseObject(songBody);
//            if (!"200".equals(urlJson.getString("message"))) return "";
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
//        XuanluogeKgTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
