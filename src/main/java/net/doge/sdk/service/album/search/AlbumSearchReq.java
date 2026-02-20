package net.doge.sdk.service.album.search;

import net.doge.constant.service.source.NetResourceSource;
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
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
            executor.submit(() -> NcAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
            executor.submit(() -> KgAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
            executor.submit(() -> QqAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL)
            executor.submit(() -> KwAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL)
            executor.submit(() -> MgAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL)
            executor.submit(() -> QiAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.DB || src == NetResourceSource.ALL)
            executor.submit(() -> DbAlbumSearchReq.getInstance().searchAlbums(keyword, page, limit));
        if (src == NetResourceSource.DT || src == NetResourceSource.ALL) {
            DtAlbumSearchReq dtAlbumSearchReq = DtAlbumSearchReq.getInstance();
            executor.submit(() -> dtAlbumSearchReq.searchAlbums(keyword, page, limit));
            executor.submit(() -> dtAlbumSearchReq.searchAlbums2(keyword, page, limit));
        }
        return executor.getResult();
    }
}
