package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.kg.ChkszKgTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.kg.KgTrackReqV2;
import net.doge.sdk.service.music.info.impl.musicurl.track.kg.QqovoKgTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.kg.XuanluogeKgTrackReq;
import net.doge.util.core.StringUtil;

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
    public String fetchMusicUrl(NetMusicInfo musicInfo, boolean forDownload) {
        String hash = musicInfo.getHash();
        String quality;
        switch (forDownload ? AudioQuality.downQuality : AudioQuality.playQuality) {
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
            case AudioQuality.HIGH:
                quality = AudioQuality.KEYS[AudioQuality.HIGH];
                break;
            default:
                quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                break;
        }
        String trackUrl = QqovoKgTrackReq.getInstance().getTrackUrl(hash, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = ChkszKgTrackReq.getInstance().getTrackUrl(hash, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = XuanluogeKgTrackReq.getInstance().getTrackUrl(hash, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = KgTrackReqV2.getInstance().getTrackUrl(hash, quality);
        return trackUrl;
    }
}
