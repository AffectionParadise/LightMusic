package net.doge.sdk.service.mv.search;

import net.doge.constant.service.source.NetResourceSource;
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
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            executor.submit(() -> NcMvSearchReq.getInstance().searchMvs(keyword, page, limit));
            executor.submit(() -> NcMvSearchReq.getInstance().searchVideos(keyword, page, limit));
        }
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
            executor.submit(() -> KgMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
            executor.submit(() -> QqMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL)
            executor.submit(() -> KwMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetResourceSource.HK || src == NetResourceSource.ALL)
            executor.submit(() -> HkMvSearchReq.getInstance().searchMvs(keyword, page, limit));
        if (src == NetResourceSource.BI || src == NetResourceSource.ALL)
            executor.submit(() -> BiMvSearchReq.getInstance().searchMvs(keyword, page));
        if (src == NetResourceSource.YY || src == NetResourceSource.ALL)
            executor.submit(() -> YyMvSearchReq.getInstance().searchMvs(keyword, page, limit, cursor));
        return executor.getResult();
    }
}
