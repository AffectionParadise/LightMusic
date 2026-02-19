package net.doge.sdk.service.playlist.rcmd;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist.*;

public class RecommendPlaylistReq {
    private static RecommendPlaylistReq instance;

    private RecommendPlaylistReq() {
    }

    public static RecommendPlaylistReq getInstance() {
        if (instance == null) instance = new RecommendPlaylistReq();
        return instance;
    }

    /**
     * 获取推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            NcRecommendPlaylistReq ncRecommendPlaylistReq = NcRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> ncRecommendPlaylistReq.getDiscoverPlaylists(page, limit));
            if (dt) executor.submit(() -> ncRecommendPlaylistReq.getRecommendPlaylists(page, limit));
            if (!dt) executor.submit(() -> ncRecommendPlaylistReq.getStylePlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            KgRecommendPlaylistReq kgRecommendPlaylistReq = KgRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> kgRecommendPlaylistReq.getRecommendPlaylists(page));
            if (dt) executor.submit(() -> kgRecommendPlaylistReq.getTopPlaylists(page, limit));
            executor.submit(() -> kgRecommendPlaylistReq.getRecommendTagPlaylists(tag, page, limit));
            executor.submit(() -> kgRecommendPlaylistReq.getNewTagPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            QqRecommendPlaylistReq qqRecommendPlaylistReq = QqRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> qqRecommendPlaylistReq.getRecommendPlaylistsQqDaily(page, limit));
//            executor.submit(() -> qqRecommendPlaylistReq.getRecommendPlaylists(page, limit));
            executor.submit(() -> qqRecommendPlaylistReq.getNewPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            KwRecommendPlaylistReq kwRecommendPlaylistReq = KwRecommendPlaylistReq.getInstance();
            if (dt) {
                executor.submit(() -> kwRecommendPlaylistReq.getRecommendPlaylists(page, limit));
                executor.submit(() -> kwRecommendPlaylistReq.getNewPlaylists(page, limit));
            }
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            MgRecommendPlaylistReq mgRecommendPlaylistReq = MgRecommendPlaylistReq.getInstance();
            if (dt) {
                executor.submit(() -> mgRecommendPlaylistReq.getSquarePlaylists(limit));
                executor.submit(() -> mgRecommendPlaylistReq.getIndexRecPlaylists(page, limit));
                executor.submit(() -> mgRecommendPlaylistReq.getRecNewPlaylists(page, limit));
//                executor.submit(() -> mgRecommendPlaylistReq.getRecommendPlaylists(page));
//                executor.submit(() -> mgRecommendPlaylistReq.getNewPlaylists(page));
            }
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> QiRecommendPlaylistReq.getInstance().getRecPlaylists(page, limit));
        }
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            MeRecommendPlaylistReq meRecommendPlaylistReq = MeRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> meRecommendPlaylistReq.getRecPlaylists(page, limit));
            executor.submit(() -> meRecommendPlaylistReq.getNewPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            executor.submit(() -> FsRecommendPlaylistReq.getInstance().getNewPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL) {
            if (dt) executor.submit(() -> BiRecommendPlaylistReq.getInstance().getRecPlaylists(page, limit));
        }
        return executor.getResult();
    }
}
