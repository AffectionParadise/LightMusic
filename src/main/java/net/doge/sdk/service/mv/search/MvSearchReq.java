package net.doge.sdk.service.mv.search;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.mv.search.impl.*;

public class MvSearchReq {
    private static MvSearchReq instance;

    private MvSearchReq() {
    }

    public static MvSearchReq getInstance() {
        if (instance == null) instance = new MvSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(int src, String keyword, int page, int limit, String cursor) {
        MultiCommonResultCallableExecutor<NetMvInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcMvSearchReq.getInstance().searchVideos(keyword, page, limit));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            executor.submit(() -> QqMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            executor.submit(() -> KwMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL)
            executor.submit(() -> HkMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL)
            executor.submit(() -> BiMvSearchReq.getInstance().searchMvs(keyword, page));
        if (src == NetMusicSource.YY || src == NetMusicSource.ALL)
            executor.submit(() -> YyMvSearchReq.getInstance().searchMvs(keyword, page, limit, cursor));
        return executor.getResult();
    }
}
