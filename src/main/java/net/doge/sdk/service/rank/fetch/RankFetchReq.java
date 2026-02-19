package net.doge.sdk.service.rank.fetch;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.rank.fetch.impl.*;

public class RankFetchReq {
    private static RankFetchReq instance;

    private RankFetchReq() {
    }

    public static RankFetchReq getInstance() {
        if (instance == null) instance = new RankFetchReq();
        return instance;
    }

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks(int src) {
        MultiCommonResultCallableExecutor<NetRankInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcRankFetchReq.getInstance().getRanks());
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgRankFetchReq.getInstance().getRanks());
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            QqRankFetchReq qqRankFetchReq = QqRankFetchReq.getInstance();
            executor.submit(() -> qqRankFetchReq.getRanks());
            executor.submit(() -> qqRankFetchReq.getRanksV2());
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            KwRankFetchReq kwRankFetchReq = KwRankFetchReq.getInstance();
            executor.submit(() -> kwRankFetchReq.getRanks());
            executor.submit(() -> kwRankFetchReq.getRanksV2());
//            executor.submit(()-> kwRankFetchReq.getRecRanks());
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            executor.submit(() -> MgRankFetchReq.getInstance().getRanks());
            executor.submit(() -> MgRankFetchReq.getInstance().getRanksV2());
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            executor.submit(() -> QiRankFetchReq.getInstance().getRanks());
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            executor.submit(() -> MeRankFetchReq.getInstance().getRanks());
        return executor.getResult();
    }
}
