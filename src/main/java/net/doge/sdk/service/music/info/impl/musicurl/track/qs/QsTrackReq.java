package net.doge.sdk.service.music.info.impl.musicurl.track.qs;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class QsTrackReq {
    private static QsTrackReq instance;

    private QsTrackReq() {
        initMap();
    }

    public static QsTrackReq getInstance() {
        if (instance == null) instance = new QsTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (汽水)
    private final String SONG_URL_QS_API = "https://api.qishui.com/luna/h5/track?track_id=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "medium");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "higher");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "highest");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hi_res");
    }

    /**
     * 获取网易云音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        String songBody = HttpRequest.get(String.format(SONG_URL_QS_API, id))
                .executeAsStr();
        JSONObject songJson = JSONObject.parseObject(songBody);
        // 需要 vip 才能播放，会返回试听 url，排除掉
        boolean needVip = songJson.getJSONObject("track").getJSONObject("label_info").getBooleanValue("only_vip_playable");
        if (needVip) return "";
        // 先获取 urlPlayerInfo
        JSONObject data = songJson.getJSONObject("track_player");
        String urlPlayerInfo = data.getString("url_player_info");
        String urlBody = HttpRequest.get(urlPlayerInfo)
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(urlBody);
        JSONArray urlArray = urlJson.getJSONObject("Result").getJSONObject("Data").getJSONArray("PlayInfoList");
        JSONObject uj = SdkUtil.findFeatureObj(urlArray, "Quality", qualityMap.get(quality));
        if (JsonUtil.isEmpty(uj)) return "";
        String trackUrl = uj.getString("MainPlayUrl");
        if (StringUtil.isEmpty(trackUrl)) return "";
        return trackUrl;
    }

//    public static void main(String[] args) {
//        QsTrackReq trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("7495039122983356470", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackHero.getTrackUrl("7495039122983356470", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackHero.getTrackUrl("7495039122983356470", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
