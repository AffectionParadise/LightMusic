package net.doge.sdk.service.rank.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.rank.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class RankInfoReq {
    private static RankInfoReq instance;

    private RankInfoReq() {
    }

    public static RankInfoReq getInstance() {
        if (instance == null) instance = new RankInfoReq();
        return instance;
    }

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public void preloadRankInfo(NetRankInfo rankInfo) {
        // 信息完整直接跳过
        if (rankInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> rankInfo.setCoverImgThumb(SdkUtil.extractCover(rankInfo.getCoverImgUrl())));
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankInfo(NetRankInfo rankInfo) {
        // 信息完整直接跳过
        if (rankInfo.isIntegrated()) return;
        int source = rankInfo.getSource();
        switch (source) {
            case NetResourceSource.QQ:
                QqRankInfoReq.getInstance().fillRankInfo(rankInfo);
                break;
            case NetResourceSource.MG:
                MgRankInfoReq.getInstance().fillRankInfo(rankInfo);
                break;
            case NetResourceSource.ME:
                MeRankInfoReq.getInstance().fillRankInfo(rankInfo);
                break;
            default:
                GlobalExecutors.imageExecutor.execute(() -> rankInfo.setCoverImg(SdkUtil.getImageFromUrl(rankInfo.getCoverImgUrl())));
                break;
        }
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int source, int page, int limit) {
        switch (source) {
            case NetResourceSource.NC:
                return NcRankInfoReq.getInstance().getMusicInfoInRank(id, source, page, limit);
            case NetResourceSource.KG:
                return KgRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            case NetResourceSource.QQ:
                return QqRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            case NetResourceSource.KW:
                return KwRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            case NetResourceSource.MG:
                return MgRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            case NetResourceSource.QI:
                return QiRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            case NetResourceSource.ME:
                return MeRankInfoReq.getInstance().getMusicInfoInRank(id, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
