package net.doge.sdk.service.album.menu;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.album.menu.impl.DbAlbumMenuReq;
import net.doge.sdk.service.album.menu.impl.DtAlbumMenuReq;

public class AlbumMenuReq {
    private static AlbumMenuReq instance;

    private AlbumMenuReq() {
    }

    public static AlbumMenuReq getInstance() {
        if (instance == null) instance = new AlbumMenuReq();
        return instance;
    }

    /**
     * 获取相似专辑
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getSimilarAlbums(NetAlbumInfo albumInfo) {
        int source = albumInfo.getSource();
        switch (source) {
            case NetResourceSource.DB:
                return DbAlbumMenuReq.getInstance().getSimilarAlbums(albumInfo);
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
            case NetResourceSource.DT:
                return DtAlbumMenuReq.getInstance().getAlbumImgUrls(albumInfo, page, limit, cursor);
            default:
                return CommonResult.create();
        }
    }
}
