package net.doge.sdk.service.music.info.trackhero.qq;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class VkeysQqTrackHero {
    private static VkeysQqTrackHero instance;

    private VkeysQqTrackHero() {
        initMap();
    }

    public static VkeysQqTrackHero getInstance() {
        if (instance == null) instance = new VkeysQqTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API (QQ)
    private final String SONG_URL_QQ_API = "https://api.vkeys.cn/v2/music/tencent/geturl?mid=%s&quality=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        // 试听
        qualityMap.put("trial", "1");
        // 有损 48k
        qualityMap.put("48k", "2");
        // 有损 97k
        qualityMap.put("97k", "3");
        // 标准 193k
        qualityMap.put("standard", "4");
        // 标准 86k
        qualityMap.put("86k", "5");
        // 标准 128k
        qualityMap.put("128k", "6");
        // 标准 173k
        qualityMap.put("173k", "7");
        // HQ
        qualityMap.put("hq", "8");
        // HQ 提高
        qualityMap.put("super", "9");
        // 无损
        qualityMap.put("lossless", "10");
        // HI-RES
        qualityMap.put("hires", "11");
        // Dolby
        qualityMap.put("dolby", "12");
        // 至臻全景声
        qualityMap.put("atmosphere", "13");
        // 至臻母带
        qualityMap.put("master", "14");
        // AI 伴奏消音
        qualityMap.put("aiAccompaniment", "15");
        // AI 人声消音
        qualityMap.put("aiVocal", "16");
    }

    /**
     * 获取网易云音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String songBody = HttpRequest.get(String.format(SONG_URL_QQ_API, id, qualityMap.get(quality)))
                .executeAsync()
                .body();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String trackUrl = data.getString("url");
        return trackUrl;
    }

//    public static void main(String[] args) {
//        VkeysQqTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", "standard"));
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", "hq"));
//        System.out.println(trackHero.getTrackUrl("0039MnYb0qxYhV", "lossless"));
//    }
}
