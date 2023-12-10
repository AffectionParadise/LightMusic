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
////        qualityHashMap.put("128k", "128hash");
////        qualityHashMap.put("320k", "320hash");
////        qualityHashMap.put("flac", "sqhash");
////        qualityHashMap.put("flac24bit", "highhash");
//        qualityHashMap.put("128k", "hash_128");
//        qualityHashMap.put("320k", "hash_320");
//        qualityHashMap.put("flac", "hash_flac");
//        qualityHashMap.put("flac24bit", "hash_high");
//        qualityHashMap.put("master", "hash_128");
//
//        qualityMap.put("128k", "128");
//        qualityMap.put("320k", "320");
//        qualityMap.put("flac", "flac");
//        qualityMap.put("flac24bit", "high");
//        qualityMap.put("master", "viper_atmos");
//    }
//
//    /**
//     * 获取酷狗歌曲 url
//     *
//     * @param hash    歌曲 Hash
//     * @param quality 音质(128k 320k flac flac24bit)
//     * @return
//     */
//    public String getTrackUrl(String hash, String quality) {
//        // 封 IP
////        String body = HttpRequest.get("https://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + hash).executeAsync().body();
////        JSONObject bodyJson = JSONObject.parseObject(body);
////        String thash = bodyJson.getJSONObject("extra").getString(qualityHashMap.get(quality)).toLowerCase();
////        String albumId = bodyJson.getString("albumid");
////        String albumAudioId = bodyJson.getString("album_audio_id");
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
//        Map<String, Object> paramsMap = new TreeMap<>();
//        paramsMap.put("album_id", albumId);
//        paramsMap.put("userid", userid);
//        paramsMap.put("area_code", 1);
//        paramsMap.put("hash", thash);
//        paramsMap.put("module", "");
//        paramsMap.put("mid", mid);
//        paramsMap.put("appid", appid);
//        paramsMap.put("ssa_flag", "is_fromtrack");
//        paramsMap.put("clientver", clientver);
//        paramsMap.put("open_time", new SimpleDateFormat("yyyyMMdd").format(new Date()));
//        paramsMap.put("vipType", 6);
//        paramsMap.put("ptype", 0);
//        paramsMap.put("token", token);
//        paramsMap.put("auth", "");
//        paramsMap.put("mtype", 0);
//        paramsMap.put("album_audio_id", albumAudioId);
//        paramsMap.put("behavior", "play");
//        paramsMap.put("clienttime", System.currentTimeMillis() / 1000);
//        paramsMap.put("pid", 2);
//        paramsMap.put("key", CryptoUtil.md5(thash + pidversec + appid + mid + userid));
//        paramsMap.put("dfid", "-");
//        paramsMap.put("pidversion", 3001);
//
//        paramsMap.put("quality", qualityMap.get(quality));
//
//        String url = "https://gateway.kugou.com/v5/url?" + buildRequestParams(paramsMap) + "&signature=" + buildSign(paramsMap);
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
//    private String buildRequestParams(Map<String, Object> paramsMap) {
//        StringJoiner sj = new StringJoiner("&");
//        for (String k : paramsMap.keySet()) sj.add(k + "=" + paramsMap.get(k));
//        return sj.toString();
//    }
//
//    private String buildSignParams(Map<String, Object> paramsMap) {
//        StringBuilder sb = new StringBuilder();
//        for (String k : paramsMap.keySet()) sb.append(k).append("=").append(paramsMap.get(k));
//        return sb.toString();
//    }
//
//    private String buildSign(Map<String, Object> paramsMap) {
////        Map<String, Object> paramsTreeMap = new TreeMap<>(paramsMap);
//        String sign = buildSignParams(paramsMap);
//        return CryptoUtil.md5(signKey + sign + signKey);
//    }
//
//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", "128k"));
//    }
//}
