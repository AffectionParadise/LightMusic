package net.doge.sdk.service.ranking.fetch;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.ranking.fetch.impl.*;

public class RankingFetchReq {
    private static RankingFetchReq instance;

    private RankingFetchReq() {
    }

    public static RankingFetchReq getInstance() {
        if (instance == null) instance = new RankingFetchReq();
        return instance;
    }

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings(int src) {
        MultiCommonResultCallableExecutor<NetRankingInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcRankingFetchReq.getInstance().getRankings());
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgRankingFetchReq.getInstance().getRankings());
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            QqRankingFetchReq qqRankingFetchReq = QqRankingFetchReq.getInstance();
            executor.submit(() -> qqRankingFetchReq.getRankings());
            executor.submit(() -> qqRankingFetchReq.getRankings2());
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            KwRankingFetchReq kwRankingFetchReq = KwRankingFetchReq.getInstance();
            executor.submit(() -> kwRankingFetchReq.getRankings());
            executor.submit(() -> kwRankingFetchReq.getRankings2());
//            executor.submit(()-> kwRankingFetchReq.getRecRankings());
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            executor.submit(() -> MgRankingFetchReq.getInstance().getRankings());
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            executor.submit(() -> QiRankingFetchReq.getInstance().getRankings());
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            executor.submit(() -> MeRankingFetchReq.getInstance().getRankings());
        return executor.getResult();
    }
}
