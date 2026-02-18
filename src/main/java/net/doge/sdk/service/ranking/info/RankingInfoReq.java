package net.doge.sdk.service.ranking.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.ranking.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class RankingInfoReq {
    private static RankingInfoReq instance;

    private RankingInfoReq() {
    }

    public static RankingInfoReq getInstance() {
        if (instance == null) instance = new RankingInfoReq();
        return instance;
    }

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public void preloadRankingInfo(NetRankingInfo rankingInfo) {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImgThumb(SdkUtil.extractCover(rankingInfo.getCoverImgUrl())));
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankingInfo(NetRankingInfo rankingInfo) {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;
        int source = rankingInfo.getSource();
        switch (source) {
            case NetMusicSource.QQ:
                QqRankingInfoReq.getInstance().fillRankingInfo(rankingInfo);
                break;
            case NetMusicSource.MG:
                MgRankingInfoReq.getInstance().fillRankingInfo(rankingInfo);
                break;
            case NetMusicSource.ME:
                MeRankingInfoReq.getInstance().fillRankingInfo(rankingInfo);
                break;
            default:
                GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
                break;
        }
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String id, int source, int page, int limit) {
        switch (source) {
            case NetMusicSource.NC:
                return NcRankingInfoReq.getInstance().getMusicInfoInRanking(id, source, page, limit);
            case NetMusicSource.KG:
                return KgRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            case NetMusicSource.QQ:
                return QqRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            case NetMusicSource.KW:
                return KwRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            case NetMusicSource.MG:
                return MgRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            case NetMusicSource.QI:
                return QiRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            case NetMusicSource.ME:
                return MeRankingInfoReq.getInstance().getMusicInfoInRanking(id, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
