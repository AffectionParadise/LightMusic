package net.doge.sdk.service.mv.info;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.service.mv.info.impl.mvurl.*;

public class MvUrlReq {
    private static MvUrlReq instance;

    private MvUrlReq() {
    }

    public static MvUrlReq getInstance() {
        if (instance == null) instance = new MvUrlReq();
        return instance;
    }

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        int source = mvInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.KG:
                return KgMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.QQ:
                return QqMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.KW:
                return KwMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.QI:
                return QiMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.FS:
                return FsMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.HK:
                return HkMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.BI:
                return BiMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.YY:
                return YyMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.FA:
                return FaMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetMusicSource.LZ:
                return LzMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            default:
                return "";
        }
    }
}
