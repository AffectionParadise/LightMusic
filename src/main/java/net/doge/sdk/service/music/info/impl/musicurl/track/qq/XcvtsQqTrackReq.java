package net.doge.sdk.service.music.info.impl.musicurl.track.qq;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.log.LogUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XcvtsQqTrackReq {
    private static XcvtsQqTrackReq instance;

    private XcvtsQqTrackReq() {
        initMap();
        initBlacklist();
    }

    public static XcvtsQqTrackReq getInstance() {
        if (instance == null) instance = new XcvtsQqTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (QQ)
    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/qq.py
    private final String SONG_URL_QQ_API = "https://api.xcvts.cn/api/music/qq?apiKey=%s&mid=%s&type=%s";

    private final String[] REQUEST_KEYS = {
            "Nzg5OTMzNDRiOWJmMTEwNTY1NTU5OTAwOWNkYmEzZDI=",
            "Y2U3NzhlYjBkMTg1OGVkZmI0YjIwNzFhMTE1ZjFlZGY=",
            "NzRhNjdhZjM3ZjUyODg4MjYxNmRkMzU1OTdlYTc0MGQ="
    };

    private Map<String, String> qualityMap = new HashMap<>();
    // url 黑名单
    private Set<String> urlBlacklist = new HashSet<>();

    private void initMap() {
        // 标准品质
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "低品质");
        // HQ
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "中品质");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "HQ高品质");
        // 无损
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "SQ无损");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "臻品2.0");
        // 至臻全景声
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "臻品全景声");
        // 至臻母带
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "臻品母带");
    }

    private void initBlacklist() {
        urlBlacklist.add("&API=api.xcvts.cn");
    }

    private String decodeRequestKey(String requestKey) {
        return CryptoUtil.base64DecodeStr(requestKey);
    }

    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param mid     歌曲 mid
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        try {
            String key = decodeRequestKey(RandomUtil.randomChoose(REQUEST_KEYS));
            String songBody = HttpRequest.get(String.format(SONG_URL_QQ_API, key, mid, qualityMap.get(quality)))
                    .executeAsStr();
            JSONObject songJson = JSONObject.parseObject(songBody);
            if (songJson.getIntValue("code") != 0) return "";
            JSONObject data = songJson.getJSONObject("data");
            if (JsonUtil.isEmpty(data)) return "";
            String trackUrl = data.getString("music");
            if (StringUtil.isEmpty(trackUrl) || urlBlacklist.contains(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

    public static void main(String[] args) {
        XcvtsQqTrackReq trackReq = getInstance();
        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.STANDARD]));
        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.HIGH]));
        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.SUPER]));
        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.HI_RES]));
        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.ATMOSPHERE]));
        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.MASTER]));
    }
}
