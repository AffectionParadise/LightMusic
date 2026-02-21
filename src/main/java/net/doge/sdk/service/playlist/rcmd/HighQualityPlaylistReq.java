package net.doge.sdk.service.playlist.rcmd;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
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
        final String defaultTag = I18n.getText("defaultTag");
        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
            NcHighQualityPlaylistReq ncHighQualityPlaylistReq = NcHighQualityPlaylistReq.getInstance();
            executor.submit(() -> ncHighQualityPlaylistReq.getHighQualityPlaylists(tag, page, limit));
            executor.submit(() -> ncHighQualityPlaylistReq.getHotPickedPlaylists(tag, page, limit));
            executor.submit(() -> ncHighQualityPlaylistReq.getNewPickedPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
            KgHighQualityPlaylistReq kgHighQualityPlaylistReq = KgHighQualityPlaylistReq.getInstance();
            executor.submit(() -> kgHighQualityPlaylistReq.getTopPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getTagPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getHotCollectedTagPlaylists(tag, page, limit));
            executor.submit(() -> kgHighQualityPlaylistReq.getUpTagPlaylists(tag, page, limit));
            if (dt) executor.submit(() -> kgHighQualityPlaylistReq.getHotPlaylists(page, limit));
//            executor.submit(() -> kgHighQualityPlaylistReq.getIpPlaylists(page, limit));
        }
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
            executor.submit(() -> QqHighQualityPlaylistReq.getInstance().getCatPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.KW || src == NetResourceSource.ALL) {
            KwHighQualityPlaylistReq kwHighQualityPlaylistReq = KwHighQualityPlaylistReq.getInstance();
            if (dt) executor.submit(() -> kwHighQualityPlaylistReq.getHotPlaylists(page, limit));
            if (dt) executor.submit(() -> kwHighQualityPlaylistReq.getDefaultPlaylists(page, limit));
            executor.submit(() -> kwHighQualityPlaylistReq.getCatPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
            MgHighQualityPlaylistReq mgHighQualityPlaylistReq = MgHighQualityPlaylistReq.getInstance();
            if (dt) executor.submit(() -> mgHighQualityPlaylistReq.getRecHotPlaylists(page, limit));
//            if (dt) executor.submit(() -> mgHighQualityPlaylistReq.getRecommendPlaylists(page, limit));
            executor.submit(() -> mgHighQualityPlaylistReq.getCatPlaylists(tag, page));
        }
        if (src == NetResourceSource.QI || src == NetResourceSource.ALL) {
            executor.submit(() -> QiHighQualityPlaylistReq.getInstance().getCatPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL) {
            MeHighQualityPlaylistReq meHighQualityPlaylistReq = MeHighQualityPlaylistReq.getInstance();
            executor.submit(() -> meHighQualityPlaylistReq.getCatPlaylists(tag, page, limit));
            executor.submit(() -> meHighQualityPlaylistReq.getExpPlaylists(tag, page, limit));
        }
        if (src == NetResourceSource.FS || src == NetResourceSource.ALL) {
            executor.submit(() -> FsHighQualityPlaylistReq.getInstance().getHotPlaylists(tag, page, limit));
        }
        return executor.getResult();
    }
}
