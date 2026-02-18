package net.doge.sdk.service.album.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.album.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class AlbumInfoReq {
    private static AlbumInfoReq instance;

    private AlbumInfoReq() {
    }

    public static AlbumInfoReq getInstance() {
        if (instance == null) instance = new AlbumInfoReq();
        return instance;
    }

    /**
     * 根据专辑 id 预加载专辑信息
     */
    public void preloadAlbumInfo(NetAlbumInfo albumInfo) {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImgThumb(SdkUtil.extractCover(albumInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(int source, String id) {
        switch (source) {
            case NetMusicSource.NC:
                return NcAlbumInfoReq.getInstance().getAlbumInfo(id);
            case NetMusicSource.KG:
                return KgAlbumInfoReq.getInstance().getAlbumInfo(id);
            case NetMusicSource.QQ:
                return QqAlbumInfoReq.getInstance().getAlbumInfo(id);
            case NetMusicSource.KW:
                return KwAlbumInfoReq.getInstance().getAlbumInfo(id);
            case NetMusicSource.MG:
                return MgAlbumInfoReq.getInstance().getAlbumInfo(id);
            case NetMusicSource.QI:
                return QiAlbumInfoReq.getInstance().getAlbumInfo(id);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;
        int source = albumInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                NcAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.KG:
                KgAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.QQ:
                QqAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.KW:
                KwAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.MG:
                MgAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.QI:
                QiAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.DB:
                DbAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.DT:
                DtAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
            case NetMusicSource.LZ:
                LzAlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
                break;
        }
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        int source = albumInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.KG:
                return KgAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.QQ:
                return QqAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.KW:
                return KwAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.MG:
                return MgAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.QI:
                return QiAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            case NetMusicSource.LZ:
                return LzAlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取专辑照片链接
     */
    public CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        int source = albumInfo.getSource();
        switch (source) {
            case NetMusicSource.DB:
                return DbAlbumInfoReq.getInstance().getAlbumImgUrls(albumInfo, page, limit, cursor);
            default:
                return CommonResult.create();
        }
    }
}
