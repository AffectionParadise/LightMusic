package net.doge.sdk.service.music.rcmd;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.music.rcmd.impl.newmusic.*;

public class NewMusicReq {
    private static NewMusicReq instance;

    private NewMusicReq() {
    }

    public static NewMusicReq getInstance() {
        if (instance == null) instance = new NewMusicReq();
        return instance;
    }

    /**
     * 获取推荐歌曲 + 新歌速递
     */
    public CommonResult<NetMusicInfo> getNewMusic(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetMusicInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            NcNewMusicReq ncNewMusicReq = NcNewMusicReq.getInstance();
            if (dt) executor.submit(() -> ncNewMusicReq.getRecommendNewSong(page, limit));
            executor.submit(() -> ncNewMusicReq.getFastNewSong(tag, page, limit));
            if (!dt) executor.submit(() -> ncNewMusicReq.getStyleNewSong(tag, page, limit));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            KgNewMusicReq kgNewMusicReq = KgNewMusicReq.getInstance();
            if (dt) executor.submit(() -> kgNewMusicReq.getEverydaySong(page, limit));
            executor.submit(() -> kgNewMusicReq.getRecommendNewSong(tag, page, limit));
            executor.submit(() -> kgNewMusicReq.getStyleSong(tag, page, limit));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            executor.submit(() -> QqNewMusicReq.getInstance().getRecommendNewSong(tag, page, limit));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> KwNewMusicReq.getInstance().getRecommendNewSong(page, limit));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> MgNewMusicReq.getInstance().getRecommendNewSong(page, limit));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> QiNewMusicReq.getInstance().getRecommendNewSong(page, limit));
        }
        if (src == NetMusicSource.HF || src == NetMusicSource.ALL) {
            executor.submit(() -> HfNewMusicReq.getInstance().getRecommendNewSong(tag, page, limit));
        }
        if (src == NetMusicSource.GG || src == NetMusicSource.ALL) {
            executor.submit(() -> GgNewMusicReq.getInstance().getRecommendNewSong(tag, page, limit));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            FsNewMusicReq fsNewMusicReq = FsNewMusicReq.getInstance();
            executor.submit(() -> fsNewMusicReq.getLatestYcSong(tag, page, limit));
            executor.submit(() -> fsNewMusicReq.getWebsiteRecYcSong(tag, page, limit));
            executor.submit(() -> fsNewMusicReq.getCandiRecYcSong(tag, page, limit));
            executor.submit(() -> fsNewMusicReq.getLatestFcSong(tag, page, limit));
            executor.submit(() -> fsNewMusicReq.getWebsiteRecFcSong(tag, page, limit));
            executor.submit(() -> fsNewMusicReq.getCandiRecFcSong(tag, page, limit));
            if (dt) executor.submit(() -> fsNewMusicReq.getAllBzSong(page, limit));
        }
        return executor.getResult();
    }
}
