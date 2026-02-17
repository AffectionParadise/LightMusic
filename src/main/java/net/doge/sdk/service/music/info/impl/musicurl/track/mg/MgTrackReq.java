package net.doge.sdk.service.music.info.impl.musicurl.track.mg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
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
//    private final String SONG_URL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";
    private final String SONG_URL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/strategy/listen-url/v2.4?songId=%s&toneFlag=%s&resourceType=2";

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
        String urlBody = HttpRequest.get(String.format(SONG_URL_MG_API, id, qualityMap.get(quality)))
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
        return data.getString("url");
        //            String songBody = HttpRequest.get(String.format(SONG_URL_MG_API, id))
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
//            JSONArray rateFormats = data.getJSONArray("rateFormats");
//            rateFormats.addAll(data.getJSONArray("newRateFormats"));
//            String quality, urlKey;
//            String[] qs = {"SQ", "HQ", "PQ", "LQ"};
//            switch (AudioQuality.quality) {
//                case AudioQuality.HI_RES:
//                case AudioQuality.LOSSLESS:
//                    quality = "SQ";
//                    urlKey = "androidUrl";
//                    break;
//                case AudioQuality.SUPER:
//                    quality = "HQ";
//                    urlKey = "url";
//                    break;
//                case AudioQuality.HIGH:
//                    quality = "PQ";
//                    urlKey = "url";
//                    break;
//                default:
//                    quality = "LQ";
//                    urlKey = "url";
//                    break;
//            }
//            for (int i = rateFormats.size() - 1; i >= 0; i--) {
//                JSONObject urlJson = rateFormats.getJSONObject(i);
//                if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("formatType"))) continue;
//                String ftp = urlJson.getString(urlKey);
//                if (StringUtil.isEmpty(ftp)) continue;
//                String url = ftp.replaceFirst("ftp://[^/]+", "https://freetyst.nf.migu.cn");
//                return StringUtil.urlEncodeBlank(url);
//            }
    }
}
