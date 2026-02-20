package net.doge.sdk.service.mv.info;

import net.doge.constant.service.source.NetResourceSource;
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
            case NetResourceSource.NC:
                return NcMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.KG:
                return KgMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.QQ:
                return QqMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.KW:
                return KwMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.QI:
                return QiMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.FS:
                return FsMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.HK:
                return HkMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.BI:
                return BiMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.YY:
                return YyMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.FA:
                return FaMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            case NetResourceSource.LZ:
                return LzMvUrlReq.getInstance().fetchMvUrl(mvInfo);
            default:
                return "";
        }
    }
}
