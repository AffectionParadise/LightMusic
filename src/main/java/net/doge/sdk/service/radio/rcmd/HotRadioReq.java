package net.doge.sdk.service.radio.rcmd;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.radio.rcmd.impl.hotradio.DbHotRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.hotradio.MeHotRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.hotradio.NcHotRadioReq;
import net.doge.sdk.service.radio.rcmd.impl.hotradio.XmHotRadioReq;

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
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (dt) {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
                NcHotRadioReq ncHotRadioReq = NcHotRadioReq.getInstance();
                executor.submit(() -> ncHotRadioReq.getDailyRadios(page, limit));
                executor.submit(() -> ncHotRadioReq.getHotRadios(page, limit));
                executor.submit(() -> ncHotRadioReq.getRadiosRanking(page, limit));
                executor.submit(() -> ncHotRadioReq.getRecRadios(page, limit));
            }
            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                MeHotRadioReq meHotRadioReq = MeHotRadioReq.getInstance();
                executor.submit(() -> meHotRadioReq.getWeekRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getMonthRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getAllTimeRadios(page, limit));
                executor.submit(() -> meHotRadioReq.getCatRadios(tag, page, limit));
            }
            if (src == NetMusicSource.DB || src == NetMusicSource.ALL) {
                DbHotRadioReq dbHotRadioReq = DbHotRadioReq.getInstance();
                executor.submit(() -> dbHotRadioReq.getTopRadios(page));
                executor.submit(() -> dbHotRadioReq.getCatGameRadios(tag, page));
            }
        } else {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
                NcHotRadioReq ncHotRadioReq = NcHotRadioReq.getInstance();
                executor.submit(() -> ncHotRadioReq.getCatHotRadios(tag, page, limit));
                executor.submit(() -> ncHotRadioReq.getCatRecRadios(tag, page, limit));
            }
            if (src == NetMusicSource.XM || src == NetMusicSource.ALL) {
                XmHotRadioReq xmHotRadioReq = XmHotRadioReq.getInstance();
                executor.submit(() -> xmHotRadioReq.getCatRadios(tag, page, limit));
                executor.submit(() -> xmHotRadioReq.getChannelRadios(tag, page, limit));
                executor.submit(() -> xmHotRadioReq.getCatRadioRanking(tag, page, limit));
            }
            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                executor.submit(() -> MeHotRadioReq.getInstance().getCatRadios(tag, page, limit));
            }
            if (src == NetMusicSource.DB || src == NetMusicSource.ALL) {
                DbHotRadioReq dbHotRadioReq = DbHotRadioReq.getInstance();
                executor.submit(() -> dbHotRadioReq.getCatRadios(tag, page, limit));
                executor.submit(() -> dbHotRadioReq.getCatGameRadios(tag, page));
            }
        }
        return executor.getResult();
    }
}
