package net.doge.sdk.service.music.info.impl.musicurl.track.kg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.log.LogUtil;
import net.doge.util.core.net.UrlUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class QqovoKgTrackReq {
    private static QqovoKgTrackReq instance;

    private QqovoKgTrackReq() {
        initMap();
    }

    public static QqovoKgTrackReq getInstance() {
        if (instance == null) instance = new QqovoKgTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (酷狗)
    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/kugou.py
    private final String AUTH_API = "https://qqovo.top/api/session/bootstrap";
    private final String SONG_URL_KG_API = "https://qqovo.top/api/meting?server=kugou&type=url&id=%s&quality=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "master");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "master");
    }

    // 构造 Header
    private Map<String, String> buildHeaders(String url, String apiSignKey, Map<String, String> baseHeaders) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String query = uri.getQuery();

            String sortedQuery = UrlUtil.sortQuery(query);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonce = RandomUtil.randomUuid();

            String payload = String.join("\n", Method.GET.getValue(), path, sortedQuery, "", timestamp, nonce);
            String signature = CryptoUtil.hmacSha256Base64Url(payload, apiSignKey);

            Map<String, String> headers = new HashMap<>(baseHeaders);
            headers.put("X-OM-Ts", timestamp);
            headers.put("X-OM-Nonce", nonce);
            headers.put("X-OM-Sign", signature);

            return headers;
        } catch (URISyntaxException e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 获取酷狗音乐歌曲链接
     *
     * @param hash    歌曲 hash
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String hash, String quality) {
        try {
            // 获取 apikey
            Map<String, String> baseHeaders = new HashMap<>();
            baseHeaders.put(Header.REFERER, "https://qqovo.top/room/4SVWQK");
            String deviceId = RandomUtil.randomUuid();
            HttpRequest authRequest = HttpRequest.post(AUTH_API)
                    .headers(baseHeaders)
                    .jsonBody(String.format("{\"deviceId\":\"%s\"}", deviceId))
                    .session();
            String authBody = authRequest.executeAsStr();
            JSONObject authJson = JSONObject.parseObject(authBody);
            String apiSignKey = authJson.getString("apiSignKey");
            // 获取 url
            String url = String.format(SONG_URL_KG_API, hash, qualityMap.get(quality));
            String songBody = HttpRequest.get(url)
                    .client(authRequest.client())
                    .headers(buildHeaders(url, apiSignKey, baseHeaders))
                    .executeAsStr();
            JSONObject urlJson = JSONObject.parseObject(songBody);
            String trackUrl = urlJson.getString("url");
            if (StringUtil.isEmpty(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

    public static void main(String[] args) {
        QqovoKgTrackReq trackReq = getInstance();
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HI_RES]));
    }
}
