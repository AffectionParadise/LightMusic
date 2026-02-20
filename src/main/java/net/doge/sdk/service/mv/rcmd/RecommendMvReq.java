package net.doge.sdk.service.mv.rcmd;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.mv.rcmd.impl.*;

public class RecommendMvReq {
    private static RecommendMvReq instance;

    private RecommendMvReq() {
    }

    public static RecommendMvReq getInstance() {
        if (instance == null) instance = new RecommendMvReq();
        return instance;
    }

    /**
     * 获取推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMvs(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetMvInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            NcRecommendMvReq ncRecommendMvReq = NcRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> ncRecommendMvReq.getRecommendMv(page, limit));
            executor.submit(() -> ncRecommendMvReq.getMvRank(tag, page, limit));
            executor.submit(() -> ncRecommendMvReq.getNewMv(tag, page, limit));
            executor.submit(() -> ncRecommendMvReq.getAllMv(tag, page, limit));
            if (dt) executor.submit(() -> ncRecommendMvReq.getExclusiveMv(page, limit));
        }
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
            KgRecommendMvReq kgRecommendMvReq = KgRecommendMvReq.getInstance();
            executor.submit(() -> kgRecommendMvReq.getRecommendMv(tag, page, limit));
            executor.submit(() -> kgRecommendMvReq.getIpMv(tag, page, limit));
        }
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            QqRecommendMvReq qqRecommendMvReq = QqRecommendMvReq.getInstance();
            executor.submit(() -> qqRecommendMvReq.getRecommendMv(tag, page, limit));
            executor.submit(() -> qqRecommendMvReq.getNewMv(tag, page, limit));
        }
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL) {
            executor.submit(() -> KwRecommendMvReq.getInstance().getRecommendMv(tag, page, limit));
        }
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL) {
            if (dt) executor.submit(() -> QiRecommendMvReq.getInstance().getRecommendMv(page, limit));
        }
        if (src == NetResourceSource.FS || src == NetResourceSource.ALL) {
            if (dt) {
                FsRecommendMvReq fsRecommendMvReq = FsRecommendMvReq.getInstance();
                executor.submit(() -> fsRecommendMvReq.getRecommendMv(page, limit));
                executor.submit(() -> fsRecommendMvReq.getHotMv(page, limit));
                executor.submit(() -> fsRecommendMvReq.getNewMv(page, limit));
            }
        }
        if (src == NetResourceSource.HK || src == NetResourceSource.ALL) {
            HkRecommendMvReq hkRecommendMvReq = HkRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> hkRecommendMvReq.getGuessVideo(page, limit));
            if (dt) executor.submit(() -> hkRecommendMvReq.getTopVideo(page, limit));
            executor.submit(() -> hkRecommendMvReq.getRecommendVideo(tag, limit));
        }
        if (src == NetResourceSource.BI || src == NetResourceSource.ALL) {
            BiRecommendMvReq biRecommendMvReq = BiRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> biRecommendMvReq.getHotVideo(page, limit));
            executor.submit(() -> biRecommendMvReq.getCatRankVideo(tag, page, limit));
            executor.submit(() -> biRecommendMvReq.getCatNewVideo(tag, page, limit));
        }
        if (src == NetResourceSource.FA || src == NetResourceSource.ALL) {
            FaRecommendMvReq faRecommendMvReq = FaRecommendMvReq.getInstance();
            executor.submit(() -> faRecommendMvReq.getVideo(tag, page, limit));
            executor.submit(() -> faRecommendMvReq.getLive(tag, page, limit));
        }
        if (src == NetResourceSource.LZ || src == NetResourceSource.ALL) {
            executor.submit(() -> LzRecommendMvReq.getInstance().getVideo(tag, page, limit));
        }
        return executor.getResult();
    }
}
