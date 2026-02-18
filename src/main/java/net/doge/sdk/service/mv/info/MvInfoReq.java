package net.doge.sdk.service.mv.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.VideoQuality;
import net.doge.constant.core.os.Format;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.service.mv.info.impl.mvinfo.KgMvInfoReq;
import net.doge.sdk.service.mv.info.impl.mvinfo.KwMvInfoReq;
import net.doge.sdk.service.mv.info.impl.mvinfo.NcMvInfoReq;
import net.doge.sdk.service.mv.info.impl.mvinfo.QqMvInfoReq;
import net.doge.sdk.util.SdkUtil;

public class MvInfoReq {
    private static MvInfoReq instance;

    private MvInfoReq() {
    }

    public static MvInfoReq getInstance() {
        if (instance == null) instance = new MvInfoReq();
        return instance;
    }

    /**
     * 根据 MV id 预加载 MV 信息
     */
    public void preloadMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> mvInfo.setCoverImgThumb(SdkUtil.extractMvCover(mvInfo.getCoverImgUrl())));
    }

    /**
     * 根据 MV id 补全 MV 信息(只包含 url)
     */
    public void fillMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated() && mvInfo.isQualityMatch()) return;

        String url = MvUrlReq.getInstance().fetchMvUrl(mvInfo);
        mvInfo.setUrl(url);

        if (url.contains(".mp4")) mvInfo.setFormat(Format.MP4);
        else if (url.contains(".flv")) mvInfo.setFormat(Format.FLV);

        // 更新画质
        mvInfo.setQuality(VideoQuality.quality);
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        int source = mvInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                NcMvInfoReq.getInstance().fillMvDetail(mvInfo);
                break;
            case NetMusicSource.KG:
                KgMvInfoReq.getInstance().fillMvDetail(mvInfo);
                break;
            case NetMusicSource.QQ:
                QqMvInfoReq.getInstance().fillMvDetail(mvInfo);
                break;
            case NetMusicSource.KW:
                KwMvInfoReq.getInstance().fillMvDetail(mvInfo);
                break;
        }
    }
}
