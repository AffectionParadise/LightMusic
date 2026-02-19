package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.qs.QsTrackReq;

public class QsMusicUrlReq {
    private static QsMusicUrlReq instance;

    private QsMusicUrlReq() {
    }

    public static QsMusicUrlReq getInstance() {
        if (instance == null) instance = new QsMusicUrlReq();
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
                quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                break;
            case AudioQuality.LOSSLESS:
                quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                break;
            case AudioQuality.SUPER:
                quality = AudioQuality.KEYS[AudioQuality.SUPER];
                break;
            case AudioQuality.HIGH:
                quality = AudioQuality.KEYS[AudioQuality.HIGH];
                break;
            default:
                quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                break;
        }
        return QsTrackReq.getInstance().getTrackUrl(id, quality);
    }
}
