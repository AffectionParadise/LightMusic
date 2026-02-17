package net.doge.sdk.service.music.info.impl.musicurl;

import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicurl.track.nc.CunYuNcTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.nc.CyruiNcTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.nc.NcTrackReq;
import net.doge.sdk.service.music.info.impl.musicurl.track.nc.TmetuNcTrackReq;
import net.doge.util.core.StringUtil;

public class NcMusicUrlReq {
    private static NcMusicUrlReq instance;

    private NcMusicUrlReq() {
    }

    public static NcMusicUrlReq getInstance() {
        if (instance == null) instance = new NcMusicUrlReq();
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
            case AudioQuality.HIGH:
                quality = AudioQuality.KEYS[AudioQuality.HIGH];
                break;
            default:
                quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                break;
        }
        String trackUrl = TmetuNcTrackReq.getInstance().getTrackUrl(id, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = CunYuNcTrackReq.getInstance().getTrackUrl(id, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = CyruiNcTrackReq.getInstance().getTrackUrl(id, quality);
        if (StringUtil.isEmpty(trackUrl)) trackUrl = NcTrackReq.getInstance().getTrackUrl(id, quality);
        return trackUrl;
    }
}
