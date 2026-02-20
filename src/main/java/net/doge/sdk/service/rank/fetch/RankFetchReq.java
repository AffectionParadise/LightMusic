package net.doge.sdk.service.rank.fetch;

import net.doge.constant.service.source.NetResourceSource;
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
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
            executor.submit(() -> NcRankFetchReq.getInstance().getRanks());
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
            executor.submit(() -> KgRankFetchReq.getInstance().getRanks());
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            QqRankFetchReq qqRankFetchReq = QqRankFetchReq.getInstance();
            executor.submit(() -> qqRankFetchReq.getRanks());
            executor.submit(() -> qqRankFetchReq.getRanksV2());
        }
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL) {
            KwRankFetchReq kwRankFetchReq = KwRankFetchReq.getInstance();
            executor.submit(() -> kwRankFetchReq.getRanks());
            executor.submit(() -> kwRankFetchReq.getRanksV2());
//            executor.submit(()-> kwRankFetchReq.getRecRanks());
        }
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
            executor.submit(() -> MgRankFetchReq.getInstance().getRanks());
            executor.submit(() -> MgRankFetchReq.getInstance().getRanksV2());
        }
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL)
            executor.submit(() -> QiRankFetchReq.getInstance().getRanks());
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL)
            executor.submit(() -> MeRankFetchReq.getInstance().getRanks());
        return executor.getResult();
    }
}
