package net.doge.sdk.service.radio.menu;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.radio.menu.impl.DbRadioMenuReq;
import net.doge.sdk.service.radio.menu.impl.MeRadioMenuReq;
import net.doge.sdk.service.radio.menu.impl.NcRadioMenuReq;

public class RadioMenuReq {
    private static RadioMenuReq instance;

    private RadioMenuReq() {
    }

    public static RadioMenuReq getInstance() {
        if (instance == null) instance = new RadioMenuReq();
        return instance;
    }

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo radioInfo, int page, int limit) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcRadioMenuReq.getInstance().getRadioSubscribers(radioInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取相似电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetResourceSource.ME:
                return MeRadioMenuReq.getInstance().getSimilarRadios(radioInfo);
            case NetResourceSource.DB:
                return DbRadioMenuReq.getInstance().getSimilarRadios(radioInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo radioInfo) {
        int source = radioInfo.getSource();
        switch (source) {
            case NetResourceSource.ME:
                return MeRadioMenuReq.getInstance().getRadioArtists(radioInfo);
            case NetResourceSource.DB:
                return DbRadioMenuReq.getInstance().getRadioArtists(radioInfo);
            default:
                return CommonResult.create();
        }
    }
}
