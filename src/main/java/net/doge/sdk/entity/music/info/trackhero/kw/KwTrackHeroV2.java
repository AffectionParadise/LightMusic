package net.doge.sdk.entity.music.info.trackhero.kw;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KwTrackHeroV2 {
    private static KwTrackHeroV2 instance;

    private KwTrackHeroV2() {
        initMap();
    }

    public static KwTrackHeroV2 getInstance() {
        if (instance == null) instance = new KwTrackHeroV2();
        return instance;
    }

    private Map<String, String> formatMap = new HashMap<>();
    private Map<String, String> brMap = new HashMap<>();

    private void initMap() {
        formatMap.put("128k", "mp3");
        formatMap.put("320k", "mp3");
        formatMap.put("flac", "flac");

        brMap.put("128k", "128kmp3");
        brMap.put("320k", "320kmp3");
        brMap.put("flac", "2000kflac");
    }

    /**
     * 获取酷我音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质(128k 320k flac)
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        String params = String.format(mid + "?isMv=0&format=%s&br=%s&level=", formatMap.get(quality), brMap.get(quality));
        String url = "https://bd-api.kuwo.cn/api/service/music/downloadInfo/" + params;
        HttpResponse resp = HttpRequest.get(url)
                .header(Header.USER_AGENT, "okhttp/3.10.0")
                .header("channel", "qq")
                .header("plat", "ar")
                .header("net", "wifi")
                .header("ver", "3.1.2")
                .header("uid", "")
                .header("devId", "0")
                .executeAsync();
        JSONObject urlJson = JSONObject.parseObject(resp.body()).getJSONObject("data");
        String trackUrl = urlJson.getString("url");
        return trackUrl;
    }
}
