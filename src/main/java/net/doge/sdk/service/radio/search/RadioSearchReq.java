package net.doge.sdk.service.radio.search;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.radio.search.impl.DbRadioSearchReq;
import net.doge.sdk.service.radio.search.impl.MeRadioSearchReq;
import net.doge.sdk.service.radio.search.impl.NcRadioSearchReq;
import net.doge.sdk.service.radio.search.impl.XmRadioSearchReq;

public class RadioSearchReq {
    private static RadioSearchReq instance;

    private RadioSearchReq() {
    }

    public static RadioSearchReq getInstance() {
        if (instance == null) instance = new RadioSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(int src, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
            executor.submit(() -> NcRadioSearchReq.getInstance().searchRadios(keyword, page, limit));
        if (src == NetResourceSource.XM || src == NetResourceSource.ALL)
            executor.submit(() -> XmRadioSearchReq.getInstance().searchRadios(keyword, page, limit));
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL)
            executor.submit(() -> MeRadioSearchReq.getInstance().searchRadios(keyword, page, limit));
        if (src == NetResourceSource.DB || src == NetResourceSource.ALL) {
            DbRadioSearchReq dbRadioSearchReq = DbRadioSearchReq.getInstance();
            executor.submit(() -> dbRadioSearchReq.searchRadios(keyword, page, limit));
            executor.submit(() -> dbRadioSearchReq.searchBookRadios(keyword, page, limit));
            executor.submit(() -> dbRadioSearchReq.searchGameRadios(keyword, page, limit));
        }
        return executor.getResult();
    }
}
