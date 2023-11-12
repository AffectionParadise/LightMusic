//package net.doge.sdk.entity.music.info.tracker;
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
//public class KgTracker {
//    private final String pidversec = "57ae12eb6890223e355ccfcb74edf70d";
//    private final String clientver = "12029";
//    private final String appid = "1005";
//    private final String mid = "114514";
//    private final String userid = "0";
//    private final String token = "";
//    private final String signKey = "OIlwieks28dk2k092lksi2UIkp";
//
//    /**
//     * 获取酷狗歌曲 url
//     *
//     * @param sid     歌曲 Hash
//     * @param quality 音质(128k 320k flac flac24bit)
//     * @return
//     */
//    public String getTrackUrl(String sid, String quality) {
//        Map<String, String> qualityHashMap = new HashMap<>();
//        qualityHashMap.put("128k", "128hash");
//        qualityHashMap.put("320k", "320hash");
//        qualityHashMap.put("flac", "sqhash");
//        qualityHashMap.put("flac24bit", "highhash");
//
//        Map<String, String> qualityMap = new HashMap<>();
//        qualityMap.put("128k", "128");
//        qualityMap.put("320k", "320");
//        qualityMap.put("flac", "flac");
//        qualityMap.put("flac24bit", "high");
//
//        String body = HttpRequest.get("https://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + sid).executeAsync().body();
//        JSONObject bodyJson = JSONObject.parseObject(body);
//        String thash = bodyJson.getJSONObject("extra").getString(qualityHashMap.get(quality));
//        String albumId = bodyJson.getString("albumid");
//        String albumAudioId = bodyJson.getString("album_audio_id");
//        if (StringUtil.isEmpty(albumId)) albumId = "0";
//        if (StringUtil.isEmpty(albumAudioId)) albumAudioId = "0";
//
//        Map<String, Object> paramsMap = new TreeMap<>();
//        paramsMap.put("album_id", albumId);
//        paramsMap.put("userid", userid);
//        paramsMap.put("area_code", 1);
//        paramsMap.put("hash", thash.toLowerCase());
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
//        paramsMap.put("key", CryptoUtil.hashMD5(thash.toLowerCase() + pidversec + appid + mid + userid));
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
//    public static void main(String[] args) {
//        new KgTracker().getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", "128k");
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
//        return CryptoUtil.hashMD5(signKey + sign + signKey);
//    }
//}
