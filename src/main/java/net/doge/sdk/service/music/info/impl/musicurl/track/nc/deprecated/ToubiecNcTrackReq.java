//package net.doge.sdk.service.music.info.impl.musicurl.track.nc;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.array.ArrayUtil;
//import net.doge.util.core.crypto.CryptoUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.http.constant.Header;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ToubiecNcTrackReq {
//    private static ToubiecNcTrackReq instance;
//
//    private ToubiecNcTrackReq() {
//        initMap();
//    }
//
//    public static ToubiecNcTrackReq getInstance() {
//        if (instance == null) instance = new ToubiecNcTrackReq();
//        return instance;
//    }
//
//    // 认证参数获取 API
//    private final String AUTH_API = "https://nextmusic.toubiec.cn/api/key";
//    // 歌曲 URL 获取 API
//    private final String SONG_URL_API = "https://nextmusic.toubiec.cn/api/getSongUrl";
//
//    // 加密参数
//    private final int TAG_LENGTH = 128;
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
//        qualityMap.put("jyeffect", "jyeffect");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "sky");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "jymaster");
//        qualityMap.put("dolby", "dolby");
//    }
//
//    // 加密 payload
//    private String encryptPayload(String payload, String key) {
//        // payload 转字节
//        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
//        byte[] keyBytes = CryptoUtil.base64Decode(key);
//        // 生成随机 nonce
//        byte[] nonce = ArrayUtil.randomBytes(12);
//        // AES-GCM 加密
//        byte[] encrypted = CryptoUtil.aesGcmEncrypt(payloadBytes, keyBytes, TAG_LENGTH, nonce);
//        // 提取认证标签(最后 16 字节)和密文(前面部分)
//        int tagLength = TAG_LENGTH / 8;
//        byte[] tag = new byte[tagLength];
//        byte[] cipherText = new byte[encrypted.length - tagLength];
//        System.arraycopy(encrypted, encrypted.length - tagLength, tag, 0, tagLength);
//        System.arraycopy(encrypted, 0, cipherText, 0, encrypted.length - tagLength);
//        // Base64 编码
//        String encodedNonce = CryptoUtil.base64Encode(nonce);
//        String encodedTag = CryptoUtil.base64Encode(tag);
//        String encodedCipherText = CryptoUtil.base64Encode(cipherText);
//        return String.join(".", encodedNonce, encodedTag, encodedCipherText);
//    }
//
//    // 解密 payload
//    private String decryptPayload(String payload, String key) {
//        // 分割字符串
//        String[] parts = payload.split("\\.");
//        if (parts.length != 3) throw new IllegalArgumentException("Invalid format: expected nonce.tag.ciphertext");
//        // Base64 解码
//        byte[] nonce = CryptoUtil.base64Decode(parts[0]);
//        byte[] tag = CryptoUtil.base64Decode(parts[1]);
//        byte[] cipherText = CryptoUtil.base64Decode(parts[2]);
//        // 拼接密文和标签
//        byte[] cipherTextWithTag = ArrayUtil.concat(cipherText, tag);
//        // AES-GCM 解密
//        byte[] keyBytes = CryptoUtil.base64Decode(key);
//        byte[] decrypted = CryptoUtil.aesGcmDecrypt(cipherTextWithTag, keyBytes, TAG_LENGTH, nonce);
//        return new String(decrypted, StandardCharsets.UTF_8);
//    }
//
//    /**
//     * 获取网易云音乐歌曲链接
//     *
//     * @param id      歌曲 id
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String id, String quality) {
//        // 获取认证参数
//        String authBody = HttpRequest.post(AUTH_API)
//                .referer("https://wyapi.toubiec.cn/")
//                .executeAsStr();
//        JSONObject authJson = JSONObject.parseObject(authBody);
//        if (authJson.getIntValue("code") != 200) return "";
//        JSONObject authData = authJson.getJSONObject("data");
//        String keyId = authData.getString("keyId");
//        String keyToken = authData.getString("keyToken");
//        String key = authData.getString("key");
//        // 获取 url
//        String payload = String.format("{\"id\":\"%s\",\"level\":\"%s\",\"timestamp\":%s}", id, qualityMap.get(quality), System.currentTimeMillis());
//        String rawBody = HttpRequest.post(SONG_URL_API)
//                .referer("https://wyapi.toubiec.cn/")
//                .jsonBody(String.format("{\"keyId\":\"%s\",\"keyToken\":\"%s\",\"data\":\"%s\"}", keyId, keyToken, encryptPayload(payload, key)))
//                .executeAsStr();
//        JSONObject rawJson = JSONObject.parseObject(rawBody);
//        String urlBody = decryptPayload(rawJson.getString("ciphertext"), key);
//        JSONObject urlJson = JSONObject.parseObject(urlBody);
//        if (urlJson.getIntValue("code") != 200) return "";
//        JSONObject data = urlJson.getJSONObject("data");
//        String trackUrl = data.getString("url");
//        if (StringUtil.isEmpty(trackUrl)) return "";
//        return trackUrl;
//    }
//
//    public static void main(String[] args) {
//        ToubiecNcTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
