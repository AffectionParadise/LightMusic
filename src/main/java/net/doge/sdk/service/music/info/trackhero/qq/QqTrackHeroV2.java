package net.doge.sdk.service.music.info.trackhero.qq;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.service.music.info.trackhero.qq.entity.QQualityEntry;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class QqTrackHeroV2 {
    private static QqTrackHeroV2 instance;

    private QqTrackHeroV2() {
        initMap();
    }

    public static QqTrackHeroV2 getInstance() {
        if (instance == null) instance = new QqTrackHeroV2();
        return instance;
    }

    private final String guid = "0";
    private final String uin = "0";
    // 从 Cookie 中/客户端的请求体中(comm.authst)获取
    private final String qqmusic_key = "";
    // QQ 号
    private final String loginuin = "";

    private Map<String, QQualityEntry> fnMap = new HashMap<>();

    private void initMap() {
        fnMap.put("standard", new QQualityEntry("M500", ".mp3"));
        fnMap.put("hq", new QQualityEntry("M800", ".mp3"));
        fnMap.put("super", new QQualityEntry("M800", ".mp3"));
        fnMap.put("lossless", new QQualityEntry("F000", ".flac"));
        fnMap.put("hires", new QQualityEntry("RS01", ".flac"));
        fnMap.put("atmosphere", new QQualityEntry("Q000", ".flac"));
        fnMap.put("atmosphere_plus", new QQualityEntry("Q001", ".flac"));
        fnMap.put("master", new QQualityEntry("AI00", ".flac"));
        fnMap.put("nac", new QQualityEntry("TL01", ".nac"));
        fnMap.put("dts", new QQualityEntry("DT03", ".mp4"));
    }

    // 用于需要签名的 QQ 接口，musicu.fcg 不需要签名
//    private HttpRequest signRequest(String body) {
//        String s = QSignHelper.getInstance().sign(body);
//        return HttpRequest.post("https://u.y.qq.com/cgi-bin/musics.fcg?format=json&sign=" + s).body(body);
//    }

    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        // 获取 mediaMid
        String infoReqBody = String.format("{\"comm\":{\"ct\":\"19\",\"cv\":\"1859\",\"uin\":\"0\"},\"req\":{\"module\":\"music.pf_song_detail_svr\"," +
                "\"method\":\"get_song_detail_yqq\",\"param\":{\"song_type\":0,\"song_mid\":\"%s\"}}}", mid);
        String infoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .body(infoReqBody)
                .executeAsync()
                .body();
        JSONObject infoBodyJson = JSONObject.parseObject(infoBody);
        if (infoBodyJson.getIntValue("code") != 0 || infoBodyJson.getJSONObject("req").getIntValue("code") != 0)
            return "";
        String mediaMid = infoBodyJson.getJSONObject("req").getJSONObject("data")
                .getJSONObject("track_info").getJSONObject("file").getString("media_mid");
        // 获取 url
        QQualityEntry qualityEntry = fnMap.get(quality);
        String reqBody = String.format("{\"req\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"filename\":[\"%s\"]," +
                        "\"guid\":\"%s\",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"%s\",\"loginflag\":1,\"platform\":\"20\"}}," +
                        "\"comm\":{\"qq\":\"%s\",\"authst\":\"%s\",\"ct\":\"26\",\"cv\":\"2010101\",\"v\":\"2010101\"}}",
                qualityEntry.getPrefix() + mediaMid + qualityEntry.getSuffix(), guid, mid, uin, loginuin, qqmusic_key);
        String urlBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .body(reqBody)
                .executeAsync()
                .body();
        JSONObject urlJson = JSONObject.parseObject(urlBody);
        JSONObject data = urlJson.getJSONObject("req").getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        JSONArray sipArray = data.getJSONArray("sip");
        if (JsonUtil.isEmpty(sipArray)) return "";
        String sip = sipArray.getString(0);
        String url = data.getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
        String trackUrl = sip + url;
        return StringUtil.isEmpty(url) ? "" : trackUrl;
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("001CnSwn2xF1ee", "standard"));
//        System.out.println(getInstance().getTrackUrl("001CnSwn2xF1ee", "hq"));
//        System.out.println(getInstance().getTrackUrl("0039MnYb0qxYhV", "lossless"));
//    }
}
