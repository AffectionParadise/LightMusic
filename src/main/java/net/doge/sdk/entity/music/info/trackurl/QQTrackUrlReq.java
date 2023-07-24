package net.doge.sdk.entity.music.info.trackurl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.common.CryptoUtil;

import java.nio.charset.StandardCharsets;

public class QQTrackUrlReq {
    /**
     * 获取 QQ 音乐歌曲链接
     *
     * @param mid     歌曲 id
     * @param quality 品质(sq hr hq mp3)
     * @return
     */
    public String getTrackUrl(String mid, String quality) {
        String platform = "qq";
        String device = "MI 14 Pro Max";
        String osVersion = "27";
        long time = System.currentTimeMillis() / 1000;
        String lowerCase = CryptoUtil.hashMD5("f389249d91bd845c9b817db984054cfb" + time + "6562653262383463363633646364306534333663").toLowerCase();

        String s6 = "{\\\"method\\\":\\\"GetMusicUrl\\\",\\\"platform\\\":\\\"" + platform + "\\\",\\\"t1\\\":\\\"" + mid + "\\\",\\\"t2\\\":\\\"" + quality + "\\\"}";
        String s7 = "{\\\"uid\\\":\\\"\\\",\\\"token\\\":\\\"\\\",\\\"deviceid\\\":\\\"84ac82836212e869dbeea73f09ebe52b\\\",\\\"appVersion\\\":\\\"4.1.0.V4\\\"," +
                "\\\"vercode\\\":\\\"4100\\\",\\\"device\\\":\\\"" + device + "\\\",\\\"osVersion\\\":\\\"" + osVersion + "\\\"}";
        String s8 = "{\n\t\"text_1\":\t\"" + s6 + "\",\n\t\"text_2\":\t\"" + s7 + "\",\n\t\"sign_1\":\t\"" + lowerCase + "\",\n\t\"time\":\t\""
                + time + "\",\n\t\"sign_2\":\t\"" + CryptoUtil.hashMD5(s6.replace("\\", "") + s7.replace("\\", "")
                + lowerCase + time + "NDRjZGIzNzliNzEx").toLowerCase() + "\"\n}";

        String key = "6480fedae539deb2";
        byte[] aesBytes = CryptoUtil.aesEncrypt(s8.getBytes(StandardCharsets.UTF_8), "ECB", key.getBytes(StandardCharsets.UTF_8), null);
        s8 = CryptoUtil.bytesToHex(aesBytes);
        byte[] encodedBytes = CryptoUtil.bytesToHex(s8.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
        byte[] compressedBytes = CryptoUtil.compress(encodedBytes);
        String[] urls = {
                "http://app.kzti.top:1030/client/cgi-bin/api.fcg",
                "http://119.91.134.171:1030/client/cgi-bin/api.fcg",
                "http://106.52.68.150:1030/client/cgi-bin/api.fcg"
        };
        String url = urls[0];

        try {
            HttpResponse resp = HttpRequest.post(url).body(compressedBytes).execute();
            byte[] decompressed = CryptoUtil.decompress(resp.bodyBytes());
            String body = new String(decompressed, StandardCharsets.UTF_8);
            String trackUrl = JSONObject.parseObject(body).getString("data");
            return trackUrl;
        } catch (Exception e) {
            return "";
        }
    }
}
