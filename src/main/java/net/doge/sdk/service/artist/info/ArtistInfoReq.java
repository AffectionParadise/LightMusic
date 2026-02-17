package net.doge.sdk.service.artist.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
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
            case NetMusicSource.KW:
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
            // 网易云
            case NetMusicSource.NC:
                return NcArtistInfoReq.getInstance().getArtistInfo(id);
            // 酷狗
            case NetMusicSource.KG:
                return KgArtistInfoReq.getInstance().getArtistInfo(id);
            // QQ
            case NetMusicSource.QQ:
                return QqArtistInfoReq.getInstance().getArtistInfo(id);
            // 酷我
            case NetMusicSource.KW:
                return KwArtistInfoReq.getInstance().getArtistInfo(id);
            // 咪咕
            case NetMusicSource.MG:
                return MgArtistInfoReq.getInstance().getArtistInfo(id);
            // 千千
            case NetMusicSource.QI:
                return QiArtistInfoReq.getInstance().getArtistInfo(id);
            // 豆瓣
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
            // 网易云
            case NetMusicSource.NC:
                NcArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 酷狗
            case NetMusicSource.KG:
                KgArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // QQ
            case NetMusicSource.QQ:
                QqArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 酷我
            case NetMusicSource.KW:
                KwArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 咪咕
            case NetMusicSource.MG:
                MgArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 千千
            case NetMusicSource.QI:
                QiArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 猫耳
            case NetMusicSource.ME:
                MeArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
                break;
            // 豆瓣
            case NetMusicSource.DB:
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
            // 网易云
            case NetMusicSource.NC:
                return NcArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // 酷狗
            case NetMusicSource.KG:
                return KgArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // QQ
            case NetMusicSource.QQ:
                return QqArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // 酷我
            case NetMusicSource.KW:
                return KwArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // 咪咕
            case NetMusicSource.MG:
                return MgArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // 千千
            case NetMusicSource.QI:
                return QiArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            // 猫耳
            case NetMusicSource.ME:
                return MeArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
