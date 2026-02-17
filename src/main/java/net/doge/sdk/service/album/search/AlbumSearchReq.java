package net.doge.sdk.service.album.search;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.album.search.impl.*;

public class AlbumSearchReq {
    private static AlbumSearchReq instance;

    private AlbumSearchReq() {
    }

    public static AlbumSearchReq getInstance() {
        if (instance == null) instance = new AlbumSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(int src, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetAlbumInfo> executor = new MultiCommonResultCallableExecutor<>();
        // 网易云
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 酷狗
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // QQ
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            executor.submit(() -> QqAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 酷我
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            executor.submit(() -> KwAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 咪咕
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            executor.submit(() -> MgAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 千千
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            executor.submit(() -> QiAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 豆瓣
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            executor.submit(() -> DbAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        // 堆糖
        if (src == NetMusicSource.DT || src == NetMusicSource.ALL) {
            DtAlbumSearchReq dtAlbumSearchReq = DtAlbumSearchReq.getInstance();
            executor.submit(() -> dtAlbumSearchReq.searchAlbums(keyword, page, limit));
            executor.submit(() -> dtAlbumSearchReq.searchAlbums2(keyword, page, limit));
        }
        return executor.getResult();
    }
}
