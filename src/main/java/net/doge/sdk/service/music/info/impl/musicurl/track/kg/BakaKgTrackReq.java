package net.doge.sdk.service.music.info.impl.musicurl.track.kg;

import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpUtil;
import net.doge.util.core.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class BakaKgTrackReq {
    private static BakaKgTrackReq instance;

    private BakaKgTrackReq() {
        initMap();
    }

    public static BakaKgTrackReq getInstance() {
        if (instance == null) instance = new BakaKgTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (酷狗)
    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/kugou.py
    private final String SONG_URL_KG_API = "https://api.baka.plus/meting/?server=kugou&type=url&id=%s&br=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "320");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "2000");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "2000");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "2000");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "2000");
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
            String url = String.format(SONG_URL_KG_API, hash, qualityMap.get(quality));
            String trackUrl = HttpUtil.getRedirectUrl(url);
            if (StringUtil.isEmpty(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

//    public static void main(String[] args) {
//        BakaKgTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
}
