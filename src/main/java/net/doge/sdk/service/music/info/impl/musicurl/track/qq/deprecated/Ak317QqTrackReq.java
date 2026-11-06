//package net.doge.sdk.service.music.info.impl.musicurl.track.qq;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.RandomUtil;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.crypto.CryptoUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.log.LogUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Ak317QqTrackReq {
//    private static Ak317QqTrackReq instance;
//
//    private Ak317QqTrackReq() {
//        initMap();
//    }
//
//    public static Ak317QqTrackReq getInstance() {
//        if (instance == null) instance = new Ak317QqTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (QQ)
//    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/qq.py
//    private final String SONG_URL_QQ_API = "https://api.317ak.cn/api/yinyue/qqyinyue?ckey=%s&i=%s&br=%s&type=json&lrc=1";
//
//    private final String[] REQUEST_KEYS = {
//            "Wk83NlFKQ0lINVBQSUNKT09YVUg=",
//            "UUkyNzZXTTgwNzFaT1JKS0JFMTQ="
//    };
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        // 标准品质
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "2");
//        // HQ
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "6");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "6");
//        // 无损
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "8");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "10");
//        // 至臻全景声
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "9");
//        // 至臻母带
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "7");
//    }
//
//    private String decodeRequestKey(String requestKey) {
//        return CryptoUtil.base64DecodeStr(requestKey);
//    }
//
//    /**
//     * 获取 QQ 音乐歌曲链接
//     *
//     * @param mid     歌曲 mid
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        try {
//            String ckey = decodeRequestKey(RandomUtil.randomChoose(REQUEST_KEYS));
//            String songBody = HttpRequest.get(String.format(SONG_URL_QQ_API, ckey, mid, qualityMap.get(quality)))
//                    .executeAsStr();
//            JSONObject songJson = JSONObject.parseObject(songBody);
//            if (songJson.getIntValue("code") != 1) return "";
//            String trackUrl = songJson.getString("url");
//            if (StringUtil.isEmpty(trackUrl)) return "";
//            return trackUrl;
//        } catch (Exception e) {
//            LogUtil.error(e);
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        Ak317QqTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
