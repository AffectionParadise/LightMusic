package net.doge.sdk.service.artist.rcmd;

import net.doge.constant.service.NetMusicSource;
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
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            NcArtistListReq ncArtistListReq = NcArtistListReq.getInstance();
            executor.submit(() -> ncArtistListReq.getArtistRank(tag, page, limit));
            executor.submit(() -> ncArtistListReq.getHotArtist(page, limit));
            executor.submit(() -> ncArtistListReq.getCatArtist(tag, page, limit));
            executor.submit(() -> ncArtistListReq.getStyleArtist(tag, page, limit));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            KgArtistListReq kgArtistListReq = KgArtistListReq.getInstance();
            executor.submit(() -> kgArtistListReq.getHotArtist(tag, page, limit));
            executor.submit(() -> kgArtistListReq.getUpArtist(tag, page, limit));
            executor.submit(() -> kgArtistListReq.getIpArtist(tag, page, limit));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            executor.submit(() -> QqArtistListReq.getInstance().getArtistRank(tag, page, limit));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            KwArtistListReq kwArtistListReq = KwArtistListReq.getInstance();
            executor.submit(() -> kwArtistListReq.getArtistRank(tag, page, limit));
            executor.submit(() -> kwArtistListReq.getAllArtists(tag, page, limit));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            MgArtistListReq mgArtistListReq = MgArtistListReq.getInstance();
            if (dt) executor.submit(() -> mgArtistListReq.getArtistRank(page, limit));
            if (dt) executor.submit(() -> mgArtistListReq.getArtistRank2(page, limit));
            executor.submit(() -> mgArtistListReq.getCatArtists(tag, page, limit));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            QiArtistListReq qiArtistListReq = QiArtistListReq.getInstance();
            if (dt) executor.submit(() -> qiArtistListReq.getRecArtists(page, limit));
            executor.submit(() -> qiArtistListReq.getCatArtists(tag, page, limit));
        }
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            MeArtistListReq meArtistListReq = MeArtistListReq.getInstance();
            executor.submit(() -> meArtistListReq.getCatCVs(tag, page, limit));
            executor.submit(() -> meArtistListReq.getCatOrganizations(tag, page, limit));
        }
        return executor.getResult();
    }
}
