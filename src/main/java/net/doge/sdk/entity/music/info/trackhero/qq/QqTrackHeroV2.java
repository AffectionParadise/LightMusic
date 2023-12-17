package net.doge.sdk.entity.music.info.trackhero.qq;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.entity.music.info.trackhero.qq.helper.QSignHelper;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;

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

    private Map<String, String> hMap = new HashMap<>();
    private Map<String, String> eMap = new HashMap<>();

    private void initMap() {
        hMap.put("128k", "M500");
        hMap.put("320k", "M800");
        hMap.put("flac", "F000");
        hMap.put("hires", "RS01");
        hMap.put("dolby", "Q000");
        hMap.put("master", "AI00");

        eMap.put("128k", ".mp3");
        eMap.put("320k", ".mp3");
        eMap.put("flac", ".flac");
        eMap.put("flac24bit", ".flac");
        eMap.put("dolby", ".flac");
        eMap.put("master", ".flac");
    }

    private HttpRequest signRequest(String body) {
        String s = QSignHelper.getInstance().sign(body);
        return HttpRequest.post("https://u.y.qq.com/cgi-bin/musics.fcg?format=json&sign=" + s).body(body);
    }

    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质(128k 320k flac hires dolby master)
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        // 获取 mediaMid
        String infoReqBody = String.format("{\"comm\":{\"ct\":\"19\",\"cv\":\"1859\",\"uin\":\"0\"},\"req\":{\"module\":\"music.pf_song_detail_svr\"," +
                "\"method\":\"get_song_detail_yqq\",\"param\":{\"song_type\":0,\"song_mid\":\"%s\"}}}", mid);
        String infoBody = signRequest(infoReqBody).executeAsync().body();
        JSONObject infoBodyJson = JSONObject.parseObject(infoBody);
        if (infoBodyJson.getIntValue("code") != 0 || infoBodyJson.getJSONObject("req").getIntValue("code") != 0)
            return "";
        String mediaMid = infoBodyJson.getJSONObject("req").getJSONObject("data")
                .getJSONObject("track_info").getJSONObject("file").getString("media_mid");
        // 获取 url
        String reqBody = String.format("{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"filename\":[\"%s\"]," +
                        "\"guid\":\"%s\",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"%s\",\"loginflag\":1,\"platform\":\"20\"}}," +
                        "\"comm\":{\"qq\":\"%s\",\"authst\":\"%s\",\"ct\":\"26\",\"cv\":\"2010101\",\"v\":\"2010101\"}}",
                hMap.get(quality) + mediaMid + eMap.get(quality), guid, mid, uin, loginuin, qqmusic_key);
        String urlBody = signRequest(reqBody).executeAsync().body();
        JSONObject urlJson = JSONObject.parseObject(urlBody);
        JSONObject data = urlJson.getJSONObject("req_0").getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String sip = data.getJSONArray("sip").getString(0);
        String url = data.getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
        String trackUrl = sip + url;
        return StringUtil.isEmpty(url) ? "" : trackUrl;
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("001CnSwn2xF1ee", "128k"));
//        System.out.println(getInstance().getTrackUrl("0039MnYb0qxYhV", "flac"));
//    }
}
