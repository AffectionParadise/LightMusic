//package net.doge.sdk.entity.music.info.trackhero.qq;
//
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.util.common.CryptoUtil;
//
//import java.nio.charset.StandardCharsets;
//
//public class QqTrackHero {
//    private static QqTrackHero instance;
//
//    private QqTrackHero() {
//    }
//
//    public static QqTrackHero getInstance() {
//        if (instance == null) instance = new QqTrackHero();
//        return instance;
//    }
//
//    /**
//     * 获取 QQ 音乐歌曲链接
//     *
//     * @param mid     歌曲 id
//     * @param quality 品质(sq hr hq mp3)
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        String platform = "qq";
//        String device = "MI 14 Pro Max";
//        String osVersion = "13";
//        long time = System.currentTimeMillis() / 1000;
//        String lowerCase = CryptoUtil.md5("d86b856be4a7ea7a5bc9b6c4eed46f4e" + time + "6562653262383463363633646364306534333668").toLowerCase();
//
//        String s6 = "{\\\"method\\\":\\\"GetMusicUrl\\\",\\\"platform\\\":\\\"" + platform + "\\\",\\\"t1\\\":\\\"" + mid + "\\\",\\\"t2\\\":\\\"" + quality + "\\\"}";
//        String s7 = "{\\\"uid\\\":\\\"\\\",\\\"token\\\":\\\"\\\",\\\"deviceid\\\":\\\"84ac82836212e869dbeea73f09ebe52b\\\",\\\"appVersion\\\":\\\"4.1.4\\\"," +
//                "\\\"vercode\\\":\\\"4140\\\",\\\"device\\\":\\\"" + device + "\\\",\\\"osVersion\\\":\\\"" + osVersion + "\\\"}";
//        String s8 = "{\n\t\"text_1\":\t\"" + s6 + "\",\n\t\"text_2\":\t\"" + s7 + "\",\n\t\"sign_1\":\t\"" + lowerCase + "\",\n\t\"time\":\t\"" + time + "\",\n\t\"sign_2\":\t\""
//                + CryptoUtil.md5(s6.replace("\\", "") + s7.replace("\\", "") + lowerCase + time + "NDRjZGIzNzliNzEe").toLowerCase() + "\"\n}";
//
//        String hex = CryptoUtil.bytesToHex(s8.getBytes(StandardCharsets.UTF_8)).toUpperCase();
//        byte[] compressedBytes = CryptoUtil.compress(hex.getBytes(StandardCharsets.UTF_8));
//
//        try {
//            String url = "http://app.kzti.top/client/cgi-bin/api.fcg";
//            HttpResponse resp = HttpRequest.post(url).body(compressedBytes).executeAsync();
//            byte[] decompressed = CryptoUtil.decompress(resp.bodyBytes());
//            String body = new String(decompressed, StandardCharsets.UTF_8);
//            String trackUrl = JSONObject.parseObject(body).getString("data");
//            return trackUrl;
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("0039MnYb0qxYhV", "sq"));
//    }
//}
