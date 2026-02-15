package net.doge.sdk.service.music.info.trackhero.qq;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

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
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "4");
        // HQ
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "8");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "8");
        // 无损
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "5");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "5");
        // nac
        qualityMap.put("nac", "7");
        // 至臻全景声
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "1");
        // 至臻母带
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "0");
    }

    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String songBody = HttpRequest.get(String.format(SONG_URL_QQ_API, id, qualityMap.get(quality)))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String trackUrl = data.getString("audio");
        if (StringUtil.isEmpty(trackUrl)) return "";
        return trackUrl;
    }

//    public static void main(String[] args) {
//        LittleYouziQqTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
