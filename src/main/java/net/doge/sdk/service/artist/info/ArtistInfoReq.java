package net.doge.sdk.service.artist.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.artist.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class ArtistInfoReq {
    private static ArtistInfoReq instance;

    private ArtistInfoReq() {
    }

    public static ArtistInfoReq getInstance() {
        if (instance == null) instance = new ArtistInfoReq();
        return instance;
    }

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public void preloadArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;
        int source = artistInfo.getSource();
        switch (source) {
            case NetResourceSource.KW:
                KwArtistInfoReq.getInstance().preloadArtistInfo(artistInfo);
                break;
            default:
                GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImgThumb(SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl())));
                break;
        }
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(int source, String id) {
        switch (source) {
            case NetResourceSource.NC:
                return NcArtistInfoReq.getInstance().getArtistInfo(id);
            case NetResourceSource.KG:
                return KgArtistInfoReq.getInstance().getArtistInfo(id);
            case NetResourceSource.QQ:
                return QqArtistInfoReq.getInstance().getArtistInfo(id);
            case NetResourceSource.KW:
                return KwArtistInfoReq.getInstance().getArtistInfo(id);
            case NetResourceSource.MG:
                return MgArtistInfoReq.getInstance().getArtistInfo(id);
            case NetResourceSource.QI:
                return QiArtistInfoReq.getInstance().getArtistInfo(id);
//            case NetMusicSource.DB:
//                return DbArtistInfoReq.getInstance().getArtistInfo(id);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;
        int source = artistInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                NcArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.KG:
                KgArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.QQ:
                QqArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.KW:
                KwArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.MG:
                MgArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.QI:
                QiArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.ME:
                MeArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            case NetResourceSource.DB:
                DbArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
        }
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.KG:
                return KgArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.QQ:
                return QqArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.KW:
                return KwArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.MG:
                return MgArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.QI:
                return QiArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            case NetResourceSource.ME:
                return MeArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
