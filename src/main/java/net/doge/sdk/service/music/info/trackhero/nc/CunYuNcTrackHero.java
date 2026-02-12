package net.doge.sdk.service.music.info.trackhero.nc;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CunYuNcTrackHero {
    private static CunYuNcTrackHero instance;

    private CunYuNcTrackHero() {
        initMap();
    }

    public static CunYuNcTrackHero getInstance() {
        if (instance == null) instance = new CunYuNcTrackHero();
        return instance;
    }

    // 歌曲 URL 获取 API
    private final String SONG_URL_API = "https://www.cunyuapi.top/163music_play?id=%s&quality=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
        qualityMap.put("standard", "standard");
        qualityMap.put("hq", "exhigh");
        qualityMap.put("lossless", "lossless");
        qualityMap.put("hires", "hires");
        qualityMap.put("jyeffect", "jyeffect");
        qualityMap.put("atmosphere", "sky");
        qualityMap.put("master", "jymaster");
        qualityMap.put("dolby", "dolby");
    }

    /**
     * 获取网易云音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String songBody = HttpRequest.get(String.format(SONG_URL_API, id, qualityMap.get(quality)))
                .executeAsync()
                .body();
        JSONObject data = JSONObject.parseObject(songBody);
        String trackUrl = data.getString("song_file_url");
        return trackUrl;
    }

//    public static void main(String[] args) throws IOException {
//        CunYuNcTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("2600493765", "standard"));
//        System.out.println(trackHero.getTrackUrl("2600493765", "hq"));
//        System.out.println(trackHero.getTrackUrl("2600493765", "lossless"));
//    }
}
