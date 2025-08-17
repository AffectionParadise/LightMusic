package net.doge.sdk.entity.music.info.trackhero.kw;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.common.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class KwTrackHeroV3 {
    private static KwTrackHeroV3 instance;

    private KwTrackHeroV3() {
        initMap();
    }

    public static KwTrackHeroV3 getInstance() {
        if (instance == null) instance = new KwTrackHeroV3();
        return instance;
    }

    private Map<String, String> brMap = new HashMap<>();

    private void initMap() {
        brMap.put("128k", "128kmp3");
        brMap.put("320k", "320kmp3");
        brMap.put("flac", "2000kflac");
        brMap.put("flac24bit", "4000kflac");
    }

    /**
     * 获取酷我音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质(128k 320k flac)
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        String urlBody = HttpRequest.get(String.format(" http://nmobi.kuwo.cn/mobi.s?f=web&from=PC&source=kwwear_ar_2.2.3_Fwatch.apk&type=convert_url_with_sign&rid=%s&br=%s",
                        mid, brMap.get(quality)))
                .executeAsync()
                .body();
        JSONObject urlJson = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(urlJson)) return "";
        String trackUrl = urlJson.getString("url");
        return trackUrl;
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("228908", "flac"));
//    }
}
