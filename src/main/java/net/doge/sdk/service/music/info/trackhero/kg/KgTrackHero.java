//package net.doge.sdk.entity.music.info.trackhero;
//
//import cn.hutool.http.Header;
//import cn.hutool.http.HttpRequest;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.util.common.CryptoUtil;
//import net.doge.util.common.JsonUtil;
//import net.doge.util.common.StringUtil;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class KgTrackHero {
//    private static KgTrackHero instance;
//
//    private KgTrackHero() {
//        initMap();
//    }
//
//    public static KgTrackHero getInstance() {
//        if (instance == null) instance = new KgTrackHero();
//        return instance;
//    }
//
//    private final String appid = "1005";
//    private final String clientver = "12029";
//    private final String pidversec = "57ae12eb6890223e355ccfcb74edf70d";
//    private final String signKey = "OIlwieks28dk2k092lksi2UIkp";
//    // 会员信息
//    private final String token = "";
//    private final String userid = "0";
//    private final String mid = "114514";
//
//    private Map<String, String> qualityHashMap = new HashMap<>();
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
/// /        qualityHashMap.put(AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.STANDARD], "128hash");
/// /        qualityHashMap.put(AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.HIGH], "320hash");
/// /        qualityHashMap.put(AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.LOSSLESS], "sqhash");
/// /        qualityHashMap.put(AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.HI_RES], "highhash");
//        qualityHashMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "hash_128");
//        qualityHashMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "hash_320");
//        qualityHashMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "hash_flac");
//        qualityHashMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hash_high");
//
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "high");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "viper_atmos");
//    }
//
//    /**
//     * 获取酷狗歌曲 url
//     *
//     * @param hash    歌曲 Hash
//     * @param quality 音质(128k 320k flac hires)
//     * @return
//     */
//    public String getTrackUrl(String hash, String quality) {
//        // 封 IP
/// /        String body = HttpRequest.get("https://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + hash).executeAsync().body();
/// /        JSONObject bodyJson = JSONObject.parseObject(body);
/// /        String thash = bodyJson.getJSONObject("extra").getString(qualityHashMap.get(quality)).toLowerCase();
/// /        String albumId = bodyJson.getString("albumid");
/// /        String albumAudioId = bodyJson.getString("album_audio_id");
//
//        String body = HttpRequest.post("http://gateway.kugou.com/v3/album_audio/audio")
//                .header(Header.USER_AGENT, "Android712-AndroidPhone-11451-376-0-FeeCacheUpdate-wifi")
//                .header("KG-THash", "13a3164")
//                .header("KG-RC", "1")
//                .header("KG-Fake", "0")
//                .header("KG-RF", "00869891")
//                .header("x-router", "kmr.service.kugou.com")
//                .body(String.format("{\"area_code\":\"1\",\"show_privilege\":\"1\",\"show_album_info\":\"1\",\"is_publish\":\"\"," +
//                                "\"appid\":%s,\"clientver\":%s,\"mid\":\"%s\",\"dfid\":\"-\",\"clienttime\":\"%s\",\"key\":\"%s\"," +
//                                "\"fields\":\"audio_info,album_info,album_audio_id\",\"data\":[{\"hash\":\"%s\"}]}",
//                        appid, clientver, mid, signKey, System.currentTimeMillis() / 1000, hash))
//                .executeAsync()
//                .body();
//        JSONObject data = JSONObject.parseObject(body).getJSONArray("data").getJSONArray(0).getJSONObject(0);
//        String thash = data.getJSONObject("audio_info").getString(qualityHashMap.get(quality));
//        String albumId = data.getJSONObject("album_info").getString("album_id");
//        String albumAudioId = data.getString("album_audio_id");
//        if (StringUtil.isEmpty(albumId)) albumId = "";
//        if (StringUtil.isEmpty(albumAudioId)) albumAudioId = "";
//
//        Map<String, Object> params = new TreeMap<>();
//        params.put("album_id", albumId);
//        params.put("userid", userid);
//        params.put("area_code", 1);
//        params.put("hash", thash);
//        params.put("module", "");
//        params.put("mid", mid);
//        params.put("appid", appid);
//        params.put("ssa_flag", "is_fromtrack");
//        params.put("clientver", clientver);
//        params.put("open_time", new SimpleDateFormat("yyyyMMdd").format(new Date()));
//        params.put("vipType", 6);
//        params.put("ptype", 0);
//        params.put("token", token);
//        params.put("auth", "");
//        params.put("mtype", 0);
//        params.put("album_audio_id", albumAudioId);
//        params.put("behavior", "play");
//        params.put("clienttime", System.currentTimeMillis() / 1000);
//        params.put("pid", 2);
//        params.put("key", CryptoUtil.md5(thash + pidversec + appid + mid + userid));
//        params.put("dfid", "-");
//        params.put("pidversion", 3001);
//
//        params.put("quality", qualityMap.get(quality));
//
//        String url = "https://gateway.kugou.com/v5/url?" + requestParams(params) + "&signature=" + sign(params);
//        String respBody = HttpRequest.get(url)
//                .header(Header.USER_AGENT, "Android712-AndroidPhone-8983-18-0-NetMusic-wifi")
//                .header("KG-THash", "3e5ec6b")
//                .header("KG-Rec", "1")
//                .header("KG-RC", "1")
//                .header("x-router", "tracker.kugou.com")
//                .executeAsync()
//                .body();
//        JSONObject urlJson = JSONObject.parseObject(respBody);
//        JSONArray urlArray = urlJson.getJSONArray("url");
//        if (JsonUtil.notEmpty(urlArray)) return urlArray.getString(0);
//        return "";
//    }
//
//    private String requestParams(Map<String, Object> paramsMap) {
//        StringJoiner sj = new StringJoiner("&");
//        for (String k : paramsMap.keySet()) sj.add(k + "=" + paramsMap.get(k));
//        return sj.toString();
//    }
//
//    private String signParams(Map<String, Object> paramsMap) {
//        StringBuilder sb = new StringBuilder();
//        for (String k : paramsMap.keySet()) sb.append(k).append("=").append(paramsMap.get(k));
//        return sb.toString();
//    }
//
//    private String sign(Map<String, Object> paramsMap) {
////        Map<String, Object> paramsTreeMap = new TreeMap<>(paramsMap);
//        String content = signParams(paramsMap);
//        return CryptoUtil.md5(signKey + content + signKey);
//    }
//
//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//    }
//}
