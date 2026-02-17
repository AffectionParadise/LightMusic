package net.doge.sdk.service.artist.search;

import net.doge.constant.service.NetMusicSource;
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
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            executor.submit(() -> QqArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            executor.submit(() -> KwArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            executor.submit(() -> MgArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            executor.submit(() -> QiArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            executor.submit(() -> MeArtistSearchReq.getInstance().searchArtists(keyword, page, limit));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            executor.submit(() -> DbArtistSearchReq.getInstance().searchArtists(keyword, page));
        return executor.getResult();
    }
}
