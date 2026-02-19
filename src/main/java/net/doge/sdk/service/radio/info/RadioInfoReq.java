package net.doge.sdk.service.radio.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.radio.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class RadioInfoReq {
    private static RadioInfoReq instance;

    private RadioInfoReq() {
    }

    public static RadioInfoReq getInstance() {
        if (instance == null) instance = new RadioInfoReq();
        return instance;
    }

    /**
     * 根据电台 id 预加载电台信息
     */
    public void preloadRadioInfo(NetRadioInfo radioInfo) {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImgThumb(SdkUtil.extractCover(radioInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据电台 id 获取电台
     */
    public CommonResult<NetRadioInfo> getRadioInfo(int source, String id) {
        switch (source) {
            case NetMusicSource.NC:
                return NcRadioInfoReq.getInstance().getRadioInfo(id);
            case NetMusicSource.XM:
                return XmRadioInfoReq.getInstance().getRadioInfo(id);
            case NetMusicSource.ME:
                return MeRadioInfoReq.getInstance().getRadioInfo(id);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;
        int source = radioInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                NcRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
            case NetMusicSource.QQ:
                QqRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
            case NetMusicSource.MG:
                MgRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
            case NetMusicSource.XM:
                XmRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
            case NetMusicSource.ME:
                MeRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
            case NetMusicSource.DB:
                DbRadioInfoReq.getInstance().fillRadioInfo(radioInfo);
                break;
        }
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int sortType, int page, int limit) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcRadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, page, limit);
            case NetMusicSource.QQ:
                return QqRadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, page, limit);
            case NetMusicSource.MG:
                return MgRadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, page, limit);
            case NetMusicSource.XM:
                return XmRadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, sortType, page, limit);
            case NetMusicSource.ME:
                return MeRadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取电台照片链接
     */
    public CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetMusicSource.DB:
                return DbRadioInfoReq.getInstance().getRadioImgUrls(radioInfo, page);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取电台海报链接
     */
    public CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetMusicSource.DB:
                return DbRadioInfoReq.getInstance().getRadioPosterUrls(radioInfo, page);
            default:
                return CommonResult.create();
        }
    }
}
