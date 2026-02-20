package net.doge.sdk.service.artist.rcmd;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.artist.rcmd.impl.*;

public class ArtistListReq {
    private static ArtistListReq instance;

    private ArtistListReq() {
    }

    public static ArtistListReq getInstance() {
        if (instance == null) instance = new ArtistListReq();
        return instance;
    }

    /**
     * 获取歌手排行
     */
    public CommonResult<NetArtistInfo> getArtistLists(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetArtistInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            NcArtistListReq ncArtistListReq = NcArtistListReq.getInstance();
            executor.submit(() -> ncArtistListReq.getArtistRank(tag, page, limit));
            executor.submit(() -> ncArtistListReq.getHotArtist(page, limit));
            executor.submit(() -> ncArtistListReq.getCatArtist(tag, page, limit));
            executor.submit(() -> ncArtistListReq.getStyleArtist(tag, page, limit));
        }
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
            KgArtistListReq kgArtistListReq = KgArtistListReq.getInstance();
            executor.submit(() -> kgArtistListReq.getHotArtist(tag, page, limit));
            executor.submit(() -> kgArtistListReq.getUpArtist(tag, page, limit));
            executor.submit(() -> kgArtistListReq.getIpArtist(tag, page, limit));
        }
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            executor.submit(() -> QqArtistListReq.getInstance().getArtistRank(tag, page, limit));
        }
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL) {
            KwArtistListReq kwArtistListReq = KwArtistListReq.getInstance();
            executor.submit(() -> kwArtistListReq.getArtistRank(tag, page, limit));
            executor.submit(() -> kwArtistListReq.getAllArtists(tag, page, limit));
        }
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
            MgArtistListReq mgArtistListReq = MgArtistListReq.getInstance();
            if (dt) executor.submit(() -> mgArtistListReq.getArtistRank(page, limit));
            if (dt) executor.submit(() -> mgArtistListReq.getArtistRank2(page, limit));
            executor.submit(() -> mgArtistListReq.getCatArtists(tag, page, limit));
        }
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL) {
            QiArtistListReq qiArtistListReq = QiArtistListReq.getInstance();
            if (dt) executor.submit(() -> qiArtistListReq.getRecArtists(page, limit));
            executor.submit(() -> qiArtistListReq.getCatArtists(tag, page, limit));
        }
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
            MeArtistListReq meArtistListReq = MeArtistListReq.getInstance();
            executor.submit(() -> meArtistListReq.getCatCVs(tag, page, limit));
            executor.submit(() -> meArtistListReq.getCatOrganizations(tag, page, limit));
        }
        return executor.getResult();
    }
}
