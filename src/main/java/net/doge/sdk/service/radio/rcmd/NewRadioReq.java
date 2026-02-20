package net.doge.sdk.service.radio.rcmd;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.radio.rcmd.impl.newradio.MeNewRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.newradio.MgNewRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.newradio.NcNewRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.newradio.QqNewRadioReq;

public class NewRadioReq {
    private static NewRadioReq instance;

    private NewRadioReq() {
    }

    public static NewRadioReq getInstance() {
        if (instance == null) instance = new NewRadioReq();
        return instance;
    }

    /**
     * 获取新晋电台
     */
    public CommonResult<NetRadioInfo> getNewRadios(int src, int page, int limit) {
        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            NcNewRadioReq ncNewRadioReq = NcNewRadioReq.getInstance();
            executor.submit(() -> ncNewRadioReq.getNewRadios(page, limit));
            executor.submit(() -> ncNewRadioReq.getPersonalizedRadios(page, limit));
            executor.submit(() -> ncNewRadioReq.getRecommendRadios(page, limit));
            executor.submit(() -> ncNewRadioReq.getPayRadios(page, limit));
            executor.submit(() -> ncNewRadioReq.getPayGiftRadios(page, limit));
        }
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            executor.submit(() -> QqNewRadioReq.getInstance().getRecommendRadios(page, limit));
        }
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
            executor.submit(() -> MgNewRadioReq.getInstance().getTimingRadios(page, limit));
        }
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
            MeNewRadioReq meNewRadioReq = MeNewRadioReq.getInstance();
            executor.submit(() -> meNewRadioReq.getRecRadios(page, limit));
            executor.submit(() -> meNewRadioReq.getSummerRadios(page, limit));
        }
        return executor.getResult();
    }
}
