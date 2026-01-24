package net.doge.sdk.service.music.info.trackhero.kw;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.common.JsonUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwTrackHeroV3 {
    private static KwTrackHeroV3 instance;

    private KwTrackHeroV3() {
        initMap();
        initBlacklist();
    }

    public static KwTrackHeroV3 getInstance() {
        if (instance == null) instance = new KwTrackHeroV3();
        return instance;
    }

    private Map<String, String> brMap = new HashMap<>();
    // url 黑名单，为排除某些试听音频
    private List<String> subStrBlacklist = new LinkedList<>();

    private void initMap() {
        brMap.put("128k", "128kmp3");
        brMap.put("320k", "320kmp3");
        brMap.put("flac", "2000kflac");
        // 返回的是加密音频
        brMap.put("hires", "4000kflac");
    }

    private void initBlacklist() {
        subStrBlacklist.add("/n2/73/84/3759149332.mp3");
    }

    /**
     * 获取酷我音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质(128k 320k flac)
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        String urlBody = HttpRequest.get(String.format("https://mobi.kuwo.cn/mobi.s?f=web&source=kwplayer_ar_5.0.0.0_B_jiakong_vh.apk&type=convert_url_with_sign&rid=%s&br=%s&user=0",
                        mid, brMap.get(quality)))
                .executeAsync()
                .body();
        JSONObject urlJson = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(urlJson)) return "";
        String trackUrl = urlJson.getString("url");
        // 排除黑名单音频 url
        for (String subStr : subStrBlacklist)
            if (trackUrl.contains(subStr)) return "";
        return trackUrl;
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("228908", "128k"));
//        System.out.println(getInstance().getTrackUrl("228908", "320k"));
//        System.out.println(getInstance().getTrackUrl("228908", "flac"));
//    }
}
