package net.doge.sdk.service.music.info.impl.musicurl.track.mg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class MgTrackReq {
    private static MgTrackReq instance;

    private MgTrackReq() {
        initMap();
    }

    public static MgTrackReq getInstance() {
        if (instance == null) instance = new MgTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (咪咕)
    private final String SONG_URL_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/strategy/pc/listen/v1.0?scene=&netType=01&resourceType=2&copyrightId=%s&contentId=%s&toneFlag=%s";
    // 歌曲信息 API (咪咕)
    private final String SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "PQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "HQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "SQ");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "ZQ");
    }

    /**
     * 获取咪咕音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        // 先根据 id 获取 contentId
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject songJson = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
        String contentId = songJson.getString("contentId");

        String urlBody = HttpRequest.get(String.format(SONG_URL_MG_API, id, contentId, qualityMap.get(quality)))
                .header("aversionid", "")
                .header("token", "")
                .header("channel", "0146832")
                .header("language", "Chinese")
                .header("ua", "Android_migu")
                .header("mode", "android")
                .header("os", "Android 10")
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(urlBody).getJSONObject("data");
        if (JsonUtil.isEmpty(data)) return "";
        String trackUrl = data.getString("url");
        if (StringUtil.isEmpty(trackUrl)) return "";
        return trackUrl;
    }
}
