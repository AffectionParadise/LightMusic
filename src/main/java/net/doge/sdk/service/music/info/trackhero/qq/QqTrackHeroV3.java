//package net.doge.sdk.entity.music.info.trackhero.qq;
//
//import cn.hutool.http.HttpRequest;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.sdk.common.SdkCommon;
//import net.doge.sdk.entity.music.info.trackhero.qq.model.QQualityEntry;
//import net.doge.util.common.JsonUtil;
//import net.doge.util.common.StringUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class QqTrackHeroV3 {
//    private static QqTrackHeroV3 instance;
//
//    private QqTrackHeroV3() {
//        initMap();
//    }
//
//    public static QqTrackHeroV3 getInstance() {
//        if (instance == null) instance = new QqTrackHeroV3();
//        return instance;
//    }
//
//    private final String guid = "0";
//    private final String uin = "0";
//    // 从 Cookie 中/客户端的请求体中(comm.authst)获取
//    private final String qqmusic_key = "";
//    // QQ 号
//    private final String loginuin = "";
//
//    private Map<String, QQualityEntry> fnMap = new HashMap<>();
//
//    private void initMap() {
//        fnMap.put("128k", new QQualityEntry("M500", ".mp3"));
//        fnMap.put("320k", new QQualityEntry("M800", ".mp3"));
//        fnMap.put("flac", new QQualityEntry("F000", ".flac"));
//        fnMap.put("hires", new QQualityEntry("RS01", ".flac"));
//        fnMap.put("atmos", new QQualityEntry("Q000", ".flac"));
//        fnMap.put("atmos_plus", new QQualityEntry("Q001", ".flac"));
//        fnMap.put("master", new QQualityEntry("AI00", ".flac"));
//        fnMap.put("nac", new QQualityEntry("TL01", ".nac"));
//        fnMap.put("dts", new QQualityEntry("DT03", ".mp4"));
//    }
//
//    // 用于需要签名的 QQ 接口，musicu.fcg 不需要签名
/// /    private HttpRequest signRequest(String body) {
/// /        String s = QSignHelper.getInstance().sign(body);
/// /        return HttpRequest.post("https://u.y.qq.com/cgi-bin/musics.fcg?format=json&sign=" + s).body(body);
/// /    }
//
//    /**
//     * 获取 QQ 音乐歌曲链接
//     *
//     * @param mid     歌曲 id
//     * @param quality 品质(128k 320k flac hires atmos atmos_plus master nac dts)
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        // 获取 mediaMid
//        String infoReqBody = String.format("{\"comm\":{\"ct\":\"19\",\"cv\":\"1859\",\"uin\":\"0\"},\"req\":{\"module\":\"music.pf_song_detail_svr\"," +
//                "\"method\":\"get_song_detail_yqq\",\"param\":{\"song_type\":0,\"song_mid\":\"%s\"}}}", mid);
//        String infoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
//                .body(infoReqBody)
//                .executeAsync()
//                .body();
//        JSONObject infoBodyJson = JSONObject.parseObject(infoBody);
//        if (infoBodyJson.getIntValue("code") != 0 || infoBodyJson.getJSONObject("req").getIntValue("code") != 0)
//            return "";
//        String mediaMid = infoBodyJson.getJSONObject("req").getJSONObject("data")
//                .getJSONObject("track_info").getJSONObject("file").getString("media_mid");
//        // 获取 url
//        QQualityEntry qualityEntry = fnMap.get(quality);
//        String reqBody = String.format("{\"req\":{\"module\":\"music.vkey.GetVkey\",\"method\":\"UrlGetVkey\",\"param\":{\"filename\":[\"%s\"]," +
//                        "\"guid\":\"%s\",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"%s\",\"downloadfrom\":1,\"ctx\":1,\"referer\":\"y.qq.com\",\"scene\":0}}," +
//                        "\"comm\":{\"qq\":\"%s\",\"authst\":\"%s\",\"ct\":\"26\",\"cv\":\"2010101\",\"v\":\"2010101\"}}",
//                qualityEntry.getPrefix() + mediaMid + qualityEntry.getSuffix(), guid, mid, uin, loginuin, qqmusic_key);
//        String urlBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
//                .body(reqBody)
//                .executeAsync()
//                .body();
//        JSONObject urlJson = JSONObject.parseObject(urlBody);
//        JSONObject data = urlJson.getJSONObject("req").getJSONObject("data");
//        if (JsonUtil.isEmpty(data)) return "";
//        String url = data.getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
//        if (StringUtil.isEmpty(url)) return "";
//        // api 不返回 cdn，需要手动指定，不同音频的 cdn 不一样，弃用
//        String trackUrl = "http://ws.stream.music.qq.com/" + url;
//        return trackUrl;
//    }
//
//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("001CnSwn2xF1ee", "128k"));
//        System.out.println(getInstance().getTrackUrl("001CnSwn2xF1ee", "320k"));
//        System.out.println(getInstance().getTrackUrl("0039MnYb0qxYhV", "flac"));
//    }
//}
