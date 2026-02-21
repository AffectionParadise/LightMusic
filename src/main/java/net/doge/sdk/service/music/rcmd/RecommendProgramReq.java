package net.doge.sdk.service.music.rcmd;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.music.rcmd.impl.program.MeRecommendProgramReq;
import net.doge.sdk.service.music.rcmd.impl.program.NcRecommendProgramReq;

public class RecommendProgramReq {
    private static RecommendProgramReq instance;

    private RecommendProgramReq() {
    }

    public static RecommendProgramReq getInstance() {
        if (instance == null) instance = new RecommendProgramReq();
        return instance;
    }

    /**
     * 获取推荐节目
     */
    public CommonResult<NetMusicInfo> getRecommendPrograms(int src, String tag, int page, int limit) {
        final String defaultTag = I18n.getText("defaultTag");
        MultiCommonResultCallableExecutor<NetMusicInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = tag.equals(defaultTag);
        if (dt) {
            if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
                NcRecommendProgramReq ncRecommendProgramReq = NcRecommendProgramReq.getInstance();
                executor.submit(() -> ncRecommendProgramReq.getRecommendPrograms(page, limit));
                executor.submit(() -> ncRecommendProgramReq.getPersonalizedPrograms(page, limit));
                executor.submit(() -> ncRecommendProgramReq.get24HoursPrograms(page, limit));
                executor.submit(() -> ncRecommendProgramReq.getProgramsRank(page, limit));
            }
            if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
                executor.submit(() -> MeRecommendProgramReq.getInstance().getRecPrograms(page, limit));
            }
        } else {
            if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
                MeRecommendProgramReq meRecommendProgramReq = MeRecommendProgramReq.getInstance();
                executor.submit(() -> meRecommendProgramReq.getExpPrograms(tag, page, limit));
                executor.submit(() -> meRecommendProgramReq.getIndexCatPrograms(tag, page, limit));
                executor.submit(() -> meRecommendProgramReq.getIndexCatNewPrograms(tag, page, limit));
                executor.submit(() -> meRecommendProgramReq.getIndexCatProgramsRank(tag, page, limit));
            }
        }
        return executor.getResult();
    }
}
