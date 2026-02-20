package net.doge.sdk.service.playlist.rcmd;

import net.doge.constant.service.source.NetResourceSource;
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
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            NcRecommendPlaylistReq ncRecommendPlaylistReq = NcRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> ncRecommendPlaylistReq.getDiscoverPlaylists(page, limit));
            if (dt) executor.submit(() -> ncRecommendPlaylistReq.getRecommendPlaylists(page, limit));
            if (!dt) executor.submit(() -> ncRecommendPlaylistReq.getStylePlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
            KgRecommendPlaylistReq kgRecommendPlaylistReq = KgRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> kgRecommendPlaylistReq.getRecommendPlaylists(page));
            if (dt) executor.submit(() -> kgRecommendPlaylistReq.getTopPlaylists(page, limit));
            executor.submit(() -> kgRecommendPlaylistReq.getRecommendTagPlaylists(tag, page, limit));
            executor.submit(() -> kgRecommendPlaylistReq.getNewTagPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            QqRecommendPlaylistReq qqRecommendPlaylistReq = QqRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> qqRecommendPlaylistReq.getRecommendPlaylistsQqDaily(page, limit));
//            executor.submit(() -> qqRecommendPlaylistReq.getRecommendPlaylists(page, limit));
            executor.submit(() -> qqRecommendPlaylistReq.getNewPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL) {
            KwRecommendPlaylistReq kwRecommendPlaylistReq = KwRecommendPlaylistReq.getInstance();
            if (dt) {
                executor.submit(() -> kwRecommendPlaylistReq.getRecommendPlaylists(page, limit));
                executor.submit(() -> kwRecommendPlaylistReq.getNewPlaylists(page, limit));
            }
        }
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
            MgRecommendPlaylistReq mgRecommendPlaylistReq = MgRecommendPlaylistReq.getInstance();
            if (dt) {
                executor.submit(() -> mgRecommendPlaylistReq.getSquarePlaylists(limit));
                executor.submit(() -> mgRecommendPlaylistReq.getIndexRecPlaylists(page, limit));
                executor.submit(() -> mgRecommendPlaylistReq.getRecNewPlaylists(page, limit));
//                executor.submit(() -> mgRecommendPlaylistReq.getRecommendPlaylists(page));
//                executor.submit(() -> mgRecommendPlaylistReq.getNewPlaylists(page));
            }
        }
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL) {
            if (dt) executor.submit(() -> QiRecommendPlaylistReq.getInstance().getRecPlaylists(page, limit));
        }
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
            MeRecommendPlaylistReq meRecommendPlaylistReq = MeRecommendPlaylistReq.getInstance();
            if (dt) executor.submit(() -> meRecommendPlaylistReq.getRecPlaylists(page, limit));
            executor.submit(() -> meRecommendPlaylistReq.getNewPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.FS || src == NetResourceSource.ALL) {
            executor.submit(() -> FsRecommendPlaylistReq.getInstance().getNewPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.BI || src == NetResourceSource.ALL) {
            if (dt) {
                executor.submit(() -> BiRecommendPlaylistReq.getInstance().getRecPlaylists(page, limit));
                executor.submit(() -> BiRecommendPlaylistReq.getInstance().getHotPlaylists(page, limit));
                executor.submit(() -> BiRecommendPlaylistReq.getInstance().getAllPlaylists(page, limit));
            }
        }
        return executor.getResult();
    }
}
