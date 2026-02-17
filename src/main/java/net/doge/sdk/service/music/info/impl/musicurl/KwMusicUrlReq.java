package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.kw.KwTrackReqV3;

public class KwMusicUrlReq {
    private static KwMusicUrlReq instance;

    private KwMusicUrlReq() {
    }

    public static KwMusicUrlReq getInstance() {
        if (instance == null) instance = new KwMusicUrlReq();
        return instance;
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String quality;
        switch (AudioQuality.quality) {
            case AudioQuality.MASTER:
            case AudioQuality.ATMOSPHERE:
            case AudioQuality.HI_RES:
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
        return KwTrackReqV3.getInstance().getTrackUrl(id, quality);
    }
}
