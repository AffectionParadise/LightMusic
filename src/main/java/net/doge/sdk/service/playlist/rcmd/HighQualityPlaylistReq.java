package net.doge.sdk.service.playlist.rcmd;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist.*;

public class HighQualityPlaylistReq {
    private static HighQualityPlaylistReq instance;

    private HighQualityPlaylistReq() {
    }

    public static HighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new HighQualityPlaylistReq();
        return instance;
    }

    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public CommonResult<NetPlaylistInfo> getHighQualityPlaylists(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            NcHighQualityPlaylistReq ncHighQualityPlaylistReq = NcHighQualityPlaylistReq.getInstance();
            executor.submit(() -> ncHighQualityPlaylistReq.getHighQualityPlaylists(tag, page, limit));
            executor.submit(() -> ncHighQualityPlaylistReq.getHotPickedPlaylists(tag, page, limit));
            executor.submit(() -> ncHighQualityPlaylistReq.getNewPickedPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            KgHighQualityPlaylistReq kgHighQualityPlaylistReq = KgHighQualityPlaylistReq.getInstance();
            executor.submit(() -> kgHighQualityPlaylistReq.getTopPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getTagPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getHotCollectedTagPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getUpTagPlaylists(tag, page, limit));
            if (dt) executor.submit(() -> kgHighQualityPlaylistReq.getHotPlaylists(page, limit));
//            executor.submit(() -> kgHighQualityPlaylistReq.getIpPlaylists(page, limit));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            executor.submit(() -> QqHighQualityPlaylistReq.getInstance().getCatPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            KwHighQualityPlaylistReq kwHighQualityPlaylistReq = KwHighQualityPlaylistReq.getInstance();
            if (dt) executor.submit(() -> kwHighQualityPlaylistReq.getHotPlaylists(page, limit));
            if (dt) executor.submit(() -> kwHighQualityPlaylistReq.getDefaultPlaylists(page, limit));
            executor.submit(() -> kwHighQualityPlaylistReq.getCatPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            MgHighQualityPlaylistReq mgHighQualityPlaylistReq = MgHighQualityPlaylistReq.getInstance();
            if (dt) executor.submit(() -> mgHighQualityPlaylistReq.getRecHotPlaylists(page, limit));
//            if (dt) executor.submit(() -> mgHighQualityPlaylistReq.getRecommendPlaylists(page, limit));
            executor.submit(() -> mgHighQualityPlaylistReq.getCatPlaylists(tag, page));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            executor.submit(() -> QiHighQualityPlaylistReq.getInstance().getCatPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            MeHighQualityPlaylistReq meHighQualityPlaylistReq = MeHighQualityPlaylistReq.getInstance();
            executor.submit(() -> meHighQualityPlaylistReq.getCatPlaylists(tag, page, limit));
            executor.submit(() -> meHighQualityPlaylistReq.getExpPlaylists(tag, page, limit));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            executor.submit(() -> FsHighQualityPlaylistReq.getInstance().getHotPlaylists(tag, page, limit));
        }
        return executor.getResult();
    }
}
