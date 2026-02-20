package net.doge.sdk.service.artist.search;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.artist.search.impl.*;

public class ArtistSearchReq {
    private static ArtistSearchReq instance;

    private ArtistSearchReq() {
    }

    public static ArtistSearchReq getInstance() {
        if (instance == null) instance = new ArtistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(int src, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetArtistInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
            executor.submit(() -> NcArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
            executor.submit(() -> KgArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
            executor.submit(() -> QqArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL)
            executor.submit(() -> KwArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL)
            executor.submit(() -> MgArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL)
            executor.submit(() -> QiArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL)
            executor.submit(() -> MeArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetResourceSource.DB || src == NetResourceSource.ALL)
            executor.submit(() -> DbArtistSearchReq.getInstance().searchArtists(keyword, page));
        return executor.getResult();
    }
}
