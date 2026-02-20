package net.doge.sdk.service.mv.menu;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.mv.menu.impl.BiMvMenuReq;
import net.doge.sdk.service.mv.menu.impl.HkMvMenuReq;
import net.doge.sdk.service.mv.menu.impl.NcMvMenuReq;
import net.doge.sdk.service.mv.menu.impl.QqMvMenuReq;

public class MvMenuReq {
    private static MvMenuReq instance;

    private MvMenuReq() {
    }

    public static MvMenuReq getInstance() {
        if (instance == null) instance = new MvMenuReq();
        return instance;
    }

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo musicInfo, int page, int limit) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcMvMenuReq.getInstance().getRelatedMvs(musicInfo, page, limit);
            case NetResourceSource.QQ:
                return QqMvMenuReq.getInstance().getRelatedMvs(musicInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcMvMenuReq.getInstance().getSimilarMvs(netMvInfo);
            case NetResourceSource.QQ:
                return QqMvMenuReq.getInstance().getSimilarMvs(netMvInfo);
            case NetResourceSource.HK:
                return HkMvMenuReq.getInstance().getSimilarMvs(netMvInfo);
            case NetResourceSource.BI:
                return BiMvMenuReq.getInstance().getSimilarMvs(netMvInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取视频分集
     *
     * @return
     */
    public CommonResult<NetMvInfo> getVideoEpisodes(NetMvInfo netMvInfo, int page, int limit) {
        int source = netMvInfo.getSource();
        switch (source) {
            case NetResourceSource.BI:
                return BiMvMenuReq.getInstance().getVideoEpisodes(netMvInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
