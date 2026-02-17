package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.qq.LittleYouziQqTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.qq.QqTrackReqV2;
import net.doge.sdk.service.music.info.impl.musicurl.track.qq.VkeysQqTrackReq;
import net.doge.util.core.StringUtil;

public class QqMusicUrlReq {
    private static QqMusicUrlReq instance;

    private QqMusicUrlReq() {
    }

    public static QqMusicUrlReq getInstance() {
        if (instance == null) instance = new QqMusicUrlReq();
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
                quality = AudioQuality.KEYS[AudioQuality.MASTER];
                break;
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
                quality = AudioQuality.KEYS[AudioQuality.SUPER];
                break;
            case AudioQuality.HIGH:
                quality = AudioQuality.KEYS[AudioQuality.HIGH];
                break;
            default:
                quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                break;
        }
        String trackUrl = VkeysQqTrackReq.getInstance().getTrackUrl(id, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = LittleYouziQqTrackReq.getInstance().getTrackUrl(id, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = QqTrackReqV2.getInstance().getTrackUrl(id, quality);
        return trackUrl;
    }
}
