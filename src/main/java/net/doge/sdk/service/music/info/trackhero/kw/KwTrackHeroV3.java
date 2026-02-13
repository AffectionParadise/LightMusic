package net.doge.sdk.service.music.info.trackhero.kw;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.util.core.JsonUtil;

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

    // 歌曲 URL 获取 API (酷我)
    private final String SONG_URL_KW_API = "https://mobi.kuwo.cn/mobi.s?f=web&source=kwplayer_ar_5.0.0.0_B_jiakong_vh.apk&type=convert_url_with_sign&rid=%s&br=%s&user=0";

    private Map<String, String> brMap = new HashMap<>();
    // url 黑名单，为排除某些试听音频
    private List<String> subStrBlacklist = new LinkedList<>();

    private void initMap() {
        brMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128kmp3");
        brMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320kmp3");
        brMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "2000kflac");
        // 返回的是加密音频
        brMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "4000kflac");
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
        String urlBody = HttpRequest.get(String.format(SONG_URL_KW_API, mid, brMap.get(quality)))
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(urlJson)) return "";
        String trackUrl = urlJson.getString("url");
        // 排除黑名单音频 url
        for (String subStr : subStrBlacklist)
            if (trackUrl.contains(subStr)) return "";
        return trackUrl;
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("228908", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(getInstance().getTrackUrl("228908", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(getInstance().getTrackUrl("228908", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
