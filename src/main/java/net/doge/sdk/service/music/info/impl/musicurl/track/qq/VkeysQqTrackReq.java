package net.doge.sdk.service.music.info.impl.musicurl.track.qq;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class VkeysQqTrackReq {
    private static VkeysQqTrackReq instance;

    private VkeysQqTrackReq() {
        initMap();
    }

    public static VkeysQqTrackReq getInstance() {
        if (instance == null) instance = new VkeysQqTrackReq();
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
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "4");
        // 标准 86k
        qualityMap.put("86k", "5");
        // 标准 128k
        qualityMap.put("128k", "6");
        // 标准 173k
        qualityMap.put("173k", "7");
        // HQ
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "8");
        // HQ 提高
        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "9");
        // 无损
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "10");
        // HI-RES
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "11");
        // Dolby
        qualityMap.put("dolby", "12");
        // 至臻全景声
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "13");
        // 至臻母带
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "14");
        // AI 伴奏消音
        qualityMap.put("aiAccompaniment", "15");
        // AI 人声消音
        qualityMap.put("aiVocal", "16");
    }

    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param mid      歌曲 mid
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        try {
            String songBody = HttpRequest.get(String.format(SONG_URL_QQ_API, mid, qualityMap.get(quality)))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            if (JsonUtil.isEmpty(data)) return "";
            String trackUrl = data.getString("url");
            if (StringUtil.isEmpty(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

//    public static void main(String[] args) {
//        VkeysQqTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
