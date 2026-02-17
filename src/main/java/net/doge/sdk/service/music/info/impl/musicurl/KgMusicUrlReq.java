package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.kg.KgTrackReqV2;

public class KgMusicUrlReq {
    private static KgMusicUrlReq instance;

    private KgMusicUrlReq() {
    }

    public static KgMusicUrlReq getInstance() {
        if (instance == null) instance = new KgMusicUrlReq();
        return instance;
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String hash = musicInfo.getHash();
        String quality;
        switch (AudioQuality.quality) {
            case AudioQuality.MASTER:
            case AudioQuality.ATMOSPHERE:
                quality = AudioQuality.KEYS[AudioQuality.ATMOSPHERE];
                break;
            case AudioQuality.HI_RES:
                quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                break;
            case AudioQuality.LOSSLESS:
                quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                break;
            case AudioQuality.SUPER:
            case AudioQuality.HIGH:
                quality = AudioQuality.KEYS[AudioQuality.HIGH];
                break;
            default:
                quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                break;
        }
        String trackUrl = KgTrackReqV2.getInstance().getTrackUrl(hash, quality);
        // Cgg 有时返回的 url 不是正确音源，优先使用官方 api
//            if (StringUtil.isEmpty(trackUrl)) trackUrl = CggKgTrackHero.getInstance().getTrackUrl(hash, quality);
        return trackUrl;
    }
}
