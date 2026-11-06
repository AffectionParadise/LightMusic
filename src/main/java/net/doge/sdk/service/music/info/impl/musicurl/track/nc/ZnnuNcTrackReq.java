package net.doge.sdk.service.music.info.impl.musicurl.track.nc;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.log.LogUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ZnnuNcTrackReq {
    private static ZnnuNcTrackReq instance;

    private ZnnuNcTrackReq() {
        initMap();
    }

    public static ZnnuNcTrackReq getInstance() {
        if (instance == null) instance = new ZnnuNcTrackReq();
        return instance;
    }

    // 认证参数获取 API
    private final String AUTH_API = "https://music.znnu.com/api/key";
    // 歌曲 URL 获取 API
    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/netease.py
    private final String SONG_URL_API = "https://music.znnu.com/api/song";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
        qualityMap.put("jyeffect", "jyeffect");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "sky");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "jymaster");
        qualityMap.put("dolby", "dolby");
    }

    // 生成签名
    private String generateSignature(Map<String, Object> params, String timestamp, String domain) {
        StringBuilder paramsStrBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramsStrBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        String data = timestamp + domain + paramsStrBuilder;
        String hmacSecretKey = "a09d0f3700a279584e1515354fbe08a7ee1c617f919543142fa625b82f1b5ad0";
        return CryptoUtil.hmacSha256Hex(data, hmacSecretKey);
    }

    /**
     * 获取网易云音乐歌曲链接
     *
     * @param id      歌曲 id
     * @param quality 品质
     * @return
     */
    public String getTrackUrl(String id, String quality) {
        try {
            // 获取认证参数
            String authBody = HttpRequest.get(AUTH_API)
                    .executeAsStr();
            JSONObject authJson = JSONObject.parseObject(authBody);
            if (authJson.getIntValue("code") != 200) return "";
            JSONObject authData = authJson.getJSONObject("data");
            String keyToken = authData.getString("keyToken");
            String b64Key = authData.getString("key");
            // 获取 url
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String domain = "music.znnu.com";
            // TreeMap 有助于 key 排序
            Map<String, Object> params = new TreeMap<>();
            params.put("act", "song");
            params.put("id", id);
            params.put("level", qualityMap.get(quality));
            params.put("rawInput", String.format("https://music.163.com/#/song?id=%s", id));
            params.put("ip", RandomUtil.randomIpv4());
            String signature = generateSignature(params, timestamp, domain);
            params.put("timestamp", timestamp);
            params.put("domain", domain);
            params.put("signature", signature);
            String rawBody = HttpRequest.post(SONG_URL_API)
                    .header(Header.REFERER, "https://music.znnu.com/")
                    .header("X-Key-Token", keyToken)
                    .header("X-Referer", "musicParser")
                    // 注意此处以表单形式传入！
                    .form(params)
                    .executeAsStr();
            JSONObject rawJson = JSONObject.parseObject(rawBody);
            if (rawJson.getIntValue("code") != 200) return "";
            JSONObject rawData = rawJson.getJSONObject("data");
            // AES-GCM 解密
            byte[] iv = CryptoUtil.base64Decode(rawData.getString("iv"));
            byte[] ciphertext = CryptoUtil.base64Decode(rawData.getString("ciphertext"));
            byte[] tag = CryptoUtil.base64Decode(rawData.getString("tag"));
            byte[] key = CryptoUtil.base64Decode(b64Key);
            byte[] dataBytes = ArrayUtil.concat(ciphertext, tag);
            byte[] decrypted = CryptoUtil.aesGcmDecrypt(dataBytes, key, 128, iv);
            String urlBody = new String(decrypted, StandardCharsets.UTF_8);
            JSONObject urlJson = JSONObject.parseObject(urlBody);
            String trackUrl = urlJson.getString("url");
            if (StringUtil.isEmpty(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

    public static void main(String[] args) {
        ZnnuNcTrackReq trackReq = getInstance();
        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
    }
}
