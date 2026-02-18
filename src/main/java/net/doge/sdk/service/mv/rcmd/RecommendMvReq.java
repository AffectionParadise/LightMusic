package net.doge.sdk.service.mv.rcmd;

import net.doge.constant.service.NetMusicSource;
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
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            NcRecommendMvReq ncRecommendMvReq = NcRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> ncRecommendMvReq.getRecommendMv(page, limit));
            executor.submit(() -> ncRecommendMvReq.getMvRanking(tag, page, limit));
            executor.submit(() -> ncRecommendMvReq.getNewMv(tag, page, limit));
            executor.submit(() -> ncRecommendMvReq.getAllMv(tag, page, limit));
            if (dt) executor.submit(() -> ncRecommendMvReq.getExclusiveMv(page, limit));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            KgRecommendMvReq kgRecommendMvReq = KgRecommendMvReq.getInstance();
            executor.submit(() -> kgRecommendMvReq.getRecommendMv(tag, page, limit));
            executor.submit(() -> kgRecommendMvReq.getIpMv(tag, page, limit));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            QqRecommendMvReq qqRecommendMvReq = QqRecommendMvReq.getInstance();
            executor.submit(() -> qqRecommendMvReq.getRecommendMv(tag, page, limit));
            executor.submit(() -> qqRecommendMvReq.getNewMv(tag, page, limit));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            executor.submit(() -> KwRecommendMvReq.getInstance().getRecommendMv(tag, page, limit));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> QiRecommendMvReq.getInstance().getRecommendMv(tag, page, limit));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            if (dt) {
                FsRecommendMvReq fsRecommendMvReq = FsRecommendMvReq.getInstance();
                executor.submit(() -> fsRecommendMvReq.getRecommendMv(page, limit));
                executor.submit(() -> fsRecommendMvReq.getHotMv(page, limit));
                executor.submit(() -> fsRecommendMvReq.getNewMv(page, limit));
            }
        }
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL) {
            HkRecommendMvReq hkRecommendMvReq = HkRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> hkRecommendMvReq.getGuessVideo(page, limit));
            if (dt) executor.submit(() -> hkRecommendMvReq.getTopVideo(page, limit));
            executor.submit(() -> hkRecommendMvReq.getRecommendVideo(tag, limit));
        }
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL) {
            BiRecommendMvReq biRecommendMvReq = BiRecommendMvReq.getInstance();
            if (dt) executor.submit(() -> biRecommendMvReq.getHotVideo(page, limit));
            executor.submit(() -> biRecommendMvReq.getCatRankVideo(tag, page, limit));
            executor.submit(() -> biRecommendMvReq.getCatNewVideo(tag, page, limit));
        }
        if (src == NetMusicSource.FA || src == NetMusicSource.ALL) {
            FaRecommendMvReq faRecommendMvReq = FaRecommendMvReq.getInstance();
            executor.submit(() -> faRecommendMvReq.getVideo(tag, page, limit));
            executor.submit(() -> faRecommendMvReq.getLive(tag, page, limit));
        }
        if (src == NetMusicSource.LZ || src == NetMusicSource.ALL) {
            executor.submit(() -> LzRecommendMvReq.getInstance().getVideo(tag, page, limit));
        }
        return executor.getResult();
    }
}
