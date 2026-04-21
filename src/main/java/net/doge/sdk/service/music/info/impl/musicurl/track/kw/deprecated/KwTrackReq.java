//package net.doge.sdk.entity.music.info.trackhero.kw;
//
//import cn.hutool.http.HttpRequest;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.util.common.JsonUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class KwTrackHero {
//    private static KwTrackHero instance;
//
//    private KwTrackHero() {
//        initMap();
//    }
//
//    public static KwTrackHero getInstance() {
//        if (instance == null) instance = new KwTrackHero();
//        return instance;
//    }
//
//    private Map<String, String> eMap = new HashMap<>();
//    private Map<String, String> hMap = new HashMap<>();
//
//    private void initMap() {
//        eMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "mp3");
//        eMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "mp3");
//        eMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
//        eMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "flac");
//
//        hMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128k");
//        hMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320k");
//        hMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "2000k");
//        hMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "4000k");
//    }
//
//    /**
//     * 获取酷我音乐歌曲链接
//     *
//     * @param mid     歌曲 id
//     * @param quality 品质(128k 320k flac)
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        String urlBody = HttpRequest.get(String.format("http://nmobi.kuwo.cn/mobi.s?f=web&user=0611d9b202ca0820&android_id=0611d9b202ca0820&prod=kwplayer_ar_11.3.0.0&corp=kuwo&newver=3" +
//                                "&vipver=11.3.0.0&source=kwwear_ar_2.2.3_Fwatch.apk&p2p=1&q36=8899378ed08282acc723cdbc100010615202&approval=false&loginUid=0&loginSid=0&appuid=2788274549&allpay=0" +
//                                "&notrace=0&oaid=3d3e1ab6-b5a1-4767-be7c-55fe63786812&type=convert_url_with_sign&br=%s%s&format=%s&sig=0&rid=%s&priority=bitrate&network=4G&localUid=-1" +
//                                "&mode=audition&from=PC&token=bad205aa9e8a50181d948b1cafc16091&bc_token=1462dab652d0a688811e13bddf48596a&timestamp=1755249729&uid=2788274549",
//                        hMap.get(quality), eMap.get(quality), eMap.get(quality), mid))
//                .executeAsync()
//                .body();
//        JSONObject urlJson = JSONObject.parseObject(urlBody).getJSONObject("data");
//        if (JsonUtil.isEmpty(urlJson)) return "";
//        String trackUrl = urlJson.getString("url");
//        return trackUrl;
//    }
//
/// /    public static void main(String[] args) {
/// /        System.out.println(getInstance().getTrackUrl("228908", AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.LOSSLESS]));
/// /    }
//}
