package net.doge.sdk.service.album.menu;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.album.menu.impl.DbAlbumMenuReq;

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
            // 豆瓣
            case NetMusicSource.DB:
                return DbAlbumMenuReq.getInstance().getSimilarAlbums(albumInfo);
            default:
                return CommonResult.create();
        }
    }
}
