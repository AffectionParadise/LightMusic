package net.doge.sdk.service.music.rcmd;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.music.rcmd.impl.hotmusic.*;

public class HotMusicRecommendReq {
    private static HotMusicRecommendReq instance;

    private HotMusicRecommendReq() {
    }

    public static HotMusicRecommendReq getInstance() {
        if (instance == null) instance = new HotMusicRecommendReq();
        return instance;
    }

    /**
     * 获取飙升歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusicRecommend(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetMusicInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (dt) {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
                NcHotMusicRecommendReq ncHotMusicRecommendReq = NcHotMusicRecommendReq.getInstance();
                executor.submit(() -> ncHotMusicRecommendReq.getUpMusic(page, limit));
                executor.submit(() -> ncHotMusicRecommendReq.getHotMusic(page, limit));
            }
            if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
                KgHotMusicRecommendReq kgHotMusicRecommendReq = KgHotMusicRecommendReq.getInstance();
                executor.submit(() -> kgHotMusicRecommendReq.getCardSong(tag, page, limit));
                executor.submit(() -> kgHotMusicRecommendReq.getUpMusic(page, limit));
                executor.submit(() -> kgHotMusicRecommendReq.getTop500(page, limit));
            }
            if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
                QqHotMusicRecommendReq qqHotMusicRecommendReq = QqHotMusicRecommendReq.getInstance();
                executor.submit(() -> qqHotMusicRecommendReq.getPopularMusic(page, limit));
                executor.submit(() -> qqHotMusicRecommendReq.getHotMusic(page, limit));
            }
            if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
                KwHotMusicRecommendReq kwHotMusicRecommendReq = KwHotMusicRecommendReq.getInstance();
                executor.submit(() -> kwHotMusicRecommendReq.getUpMusic(page, limit));
                executor.submit(() -> kwHotMusicRecommendReq.getHotMusic(page, limit));
            }
            if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
                executor.submit(() -> MgHotMusicRecommendReq.getInstance().getHotMusic(page, limit));
            }
            if (src == NetMusicSource.HF || src == NetMusicSource.ALL) {
                executor.submit(() -> HfHotMusicRecommendReq.getInstance().getHotMusic(tag, page, limit));
            }
            if (src == NetMusicSource.GG || src == NetMusicSource.ALL) {
                executor.submit(() -> GgHotMusicRecommendReq.getInstance().getHotMusic(tag, page, limit));
            }
            if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
                FsHotMusicRecommendReq fsHotMusicRecommendReq = FsHotMusicRecommendReq.getInstance();
                executor.submit(() -> fsHotMusicRecommendReq.getSpreadYcSong(page, limit));
                executor.submit(() -> fsHotMusicRecommendReq.getShareYcSong(page, limit));
                executor.submit(() -> fsHotMusicRecommendReq.getSpreadFcSong(page, limit));
                executor.submit(() -> fsHotMusicRecommendReq.getShareFcSong(page, limit));
                executor.submit(() -> fsHotMusicRecommendReq.getHotBzSong(page, limit));
                executor.submit(() -> fsHotMusicRecommendReq.getRankBzSong(page, limit));
            }
            if (src == NetMusicSource.FA || src == NetMusicSource.ALL) {
                executor.submit(() -> FaHotMusicRecommendReq.getInstance().getHotMusic(page, limit));
            }
        } else {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
                executor.submit(() -> NcHotMusicRecommendReq.getInstance().getStyleHotSong(tag, page, limit));
            if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
                KgHotMusicRecommendReq kgHotMusicRecommendReq = KgHotMusicRecommendReq.getInstance();
                executor.submit(() -> kgHotMusicRecommendReq.getCardSong(tag, page, limit));
                executor.submit(() -> kgHotMusicRecommendReq.getThemeSong(tag, page, limit));
                executor.submit(() -> kgHotMusicRecommendReq.getFmSong(tag, page, limit));
                executor.submit(() -> kgHotMusicRecommendReq.getIpSong(tag, page, limit));
            }
            if (src == NetMusicSource.HF || src == NetMusicSource.ALL)
                executor.submit(() -> HfHotMusicRecommendReq.getInstance().getHotMusic(tag, page, limit));
            if (src == NetMusicSource.GG || src == NetMusicSource.ALL)
                executor.submit(() -> GgHotMusicRecommendReq.getInstance().getHotMusic(tag, page, limit));
        }
        return executor.getResult();
    }
}
