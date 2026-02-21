package net.doge.sdk.service.radio.rcmd;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.radio.rcmd.impl.hotradio.*;

public class HotRadioReq {
    private static HotRadioReq instance;

    private HotRadioReq() {
    }

    public static HotRadioReq getInstance() {
        if (instance == null) instance = new HotRadioReq();
        return instance;
    }

    /**
     * 获取个性电台 + 今日优选 + 热门电台 + 热门电台榜
     */
    public CommonResult<NetRadioInfo> getHotRadios(int src, String tag, int page, int limit) {
        final String defaultTag = I18n.getText("defaultTag");
        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (dt) {
            if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
                NcHotRadioReq ncHotRadioReq = NcHotRadioReq.getInstance();
                executor.submit(() -> ncHotRadioReq.getDailyRadios(page, limit));
                executor.submit(() -> ncHotRadioReq.getHotRadios(page, limit));
                executor.submit(() -> ncHotRadioReq.getRadiosRank(page, limit));
                executor.submit(() -> ncHotRadioReq.getRecRadios(page, limit));
            }
            if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
                MeHotRadioReq meHotRadioReq = MeHotRadioReq.getInstance();
                executor.submit(() -> meHotRadioReq.getWeekRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getMonthRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getAllTimeRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getCatRadios(tag, page, limit));
            }
            if (src == NetResourceSource.DB || src == NetResourceSource.ALL) {
                DbHotRadioReq dbHotRadioReq = DbHotRadioReq.getInstance();
                executor.submit(() -> dbHotRadioReq.getTopRadios(page));
                executor.submit(() -> dbHotRadioReq.getCatGameRadios(tag, page));
            }
        } else {
            if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
                NcHotRadioReq ncHotRadioReq = NcHotRadioReq.getInstance();
                executor.submit(() -> ncHotRadioReq.getCatHotRadios(tag, page, limit));
                executor.submit(() -> ncHotRadioReq.getCatRecRadios(tag, page, limit));
            }
            if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
                executor.submit(() -> MgHotRadioReq.getInstance().getCatRadios(tag, page, limit));
            }
            if (src == NetResourceSource.XM || src == NetResourceSource.ALL) {
                XmHotRadioReq xmHotRadioReq = XmHotRadioReq.getInstance();
                executor.submit(() -> xmHotRadioReq.getCatRadios(tag, page, limit));
                executor.submit(() -> xmHotRadioReq.getChannelRadios(tag, page, limit));
                executor.submit(() -> xmHotRadioReq.getCatRadioRank(tag, page, limit));
            }
            if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
                executor.submit(() -> MeHotRadioReq.getInstance().getCatRadios(tag, page, limit));
            }
            if (src == NetResourceSource.DB || src == NetResourceSource.ALL) {
                DbHotRadioReq dbHotRadioReq = DbHotRadioReq.getInstance();
                executor.submit(() -> dbHotRadioReq.getCatRadios(tag, page, limit));
                executor.submit(() -> dbHotRadioReq.getCatGameRadios(tag, page));
            }
        }
        return executor.getResult();
    }
}
