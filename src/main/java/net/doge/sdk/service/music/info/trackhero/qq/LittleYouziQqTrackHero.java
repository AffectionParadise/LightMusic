package net.doge.sdk.service.music.info.trackhero.qq;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class LittleYouziQqTrackHero {
    private static LittleYouziQqTrackHero instance;

    private LittleYouziQqTrackHero() {
        initMap();
    }

    public static LittleYouziQqTrackHero getInstance() {
        if (instance == null) instance = new LittleYouziQqTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API (QQ)
    private final String SONG_URL_QQ_API = "https://www.littleyouzi.com/api/v2/qqmusic?mid=%s&quality=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        // 标准品质
        qualityMap.put("standard", "4");
        // HQ
        qualityMap.put("hq", "8");
        qualityMap.put("super", "8");
        // 无损
        qualityMap.put("lossless", "5");
        qualityMap.put("hires", "5");
        // nac
        qualityMap.put("nac", "7");
        // 至臻全景声
        qualityMap.put("atmosphere", "1");
        // 至臻母带
        qualityMap.put("master", "0");
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
        String trackUrl = data.getString("audio");
        return trackUrl;
    }

//    public static void main(String[] args) {
//        LittleYouziQqTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", "standard"));
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", "hq"));
//        System.out.println(trackHero.getTrackUrl("0039MnYb0qxYhV", "lossless"));
//    }
}
