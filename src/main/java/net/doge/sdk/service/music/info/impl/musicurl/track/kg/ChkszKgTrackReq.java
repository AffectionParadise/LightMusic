package net.doge.sdk.service.music.info.impl.musicurl.track.kg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class ChkszKgTrackReq {
    private static ChkszKgTrackReq instance;

    private ChkszKgTrackReq() {
        initMap();
    }

    public static ChkszKgTrackReq getInstance() {
        if (instance == null) instance = new ChkszKgTrackReq();
        return instance;
    }

    // 歌曲 URL 获取 API (酷狗)
    // https://github.com/CharlesPikachu/musicdl/blob/master/musicdl/modules/sources/kugou.py
    private final String SONG_URL_KG_API = "https://api.chksz.com/api/kugou_music?id=%s&size=%s";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128k");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320k");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "master");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "master");
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
            String songBody = HttpRequest.get(String.format(SONG_URL_KG_API, hash, qualityMap.get(quality)))
                    .header(Header.REFERER, "https://cp.chksz.top/")
                    .executeAsStr();
            JSONObject urlJson = JSONObject.parseObject(songBody);
            if (urlJson.getIntValue("code") != 200) return "";
            String trackUrl = urlJson.getString("url");
            if (StringUtil.isEmpty(trackUrl)) return "";
            return trackUrl;
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

    public static void main(String[] args) {
        ChkszKgTrackReq trackReq = getInstance();
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
    }
}
