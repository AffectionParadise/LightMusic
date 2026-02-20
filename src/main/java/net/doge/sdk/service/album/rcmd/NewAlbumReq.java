package net.doge.sdk.service.album.rcmd;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.album.rcmd.impl.*;

public class NewAlbumReq {
    private static NewAlbumReq instance;

    private NewAlbumReq() {
    }

    public static NewAlbumReq getInstance() {
        if (instance == null) instance = new NewAlbumReq();
        return instance;
    }


    /**
     * 获取新碟上架
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(int src, String tag, int page, int limit) {
        final String defaultTag = "默认";
        MultiCommonResultCallableExecutor<NetAlbumInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = defaultTag.equals(tag);
        if (dt) {
            if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
                NcNewAlbumReq ncNewAlbumReq = NcNewAlbumReq.getInstance();
                executor.submit(() -> ncNewAlbumReq.getNewAlbums(tag, page, limit));
                executor.submit(() -> ncNewAlbumReq.getNewestAlbums(page, limit));
                executor.submit(() -> ncNewAlbumReq.getNewestDiAlbums(page, limit));
                executor.submit(() -> ncNewAlbumReq.getAllNewAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
                executor.submit(() -> KgNewAlbumReq.getInstance().getNewAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
                executor.submit(() -> QqNewAlbumReq.getInstance().getNewAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.MG || src == NetResourceSource.ALL) {
                MgNewAlbumReq mgNewAlbumReq = MgNewAlbumReq.getInstance();
                executor.submit(() -> mgNewAlbumReq.getNewAlbums(page, limit));
                executor.submit(() -> mgNewAlbumReq.getNewAlbumsRank(page, limit));
            }
            if (src == NetResourceSource.QI || src == NetResourceSource.ALL) {
                QiNewAlbumReq qiNewAlbumReq = QiNewAlbumReq.getInstance();
                executor.submit(() -> qiNewAlbumReq.getIndexNewAlbums(page, limit));
                executor.submit(() -> qiNewAlbumReq.getNewAlbums(page, limit));
                executor.submit(() -> qiNewAlbumReq.getXDAlbums(page, limit));
            }
            if (src == NetResourceSource.DT || src == NetResourceSource.ALL) {
                executor.submit(() -> DtNewAlbumReq.getInstance().getRecAlbums(page, limit));
            }
            if (src == NetResourceSource.DB || src == NetResourceSource.ALL) {
                executor.submit(() -> DbNewAlbumReq.getInstance().getTopAlbums(page));
            }
            if (src == NetResourceSource.LZ || src == NetResourceSource.ALL) {
                executor.submit(() -> LzNewAlbumReq.getInstance().getAlbums(page, limit));
            }
        } else {
            if (src == NetResourceSource.NC || src == NetResourceSource.ALL) {
                NcNewAlbumReq ncNewAlbumReq = NcNewAlbumReq.getInstance();
                executor.submit(() -> ncNewAlbumReq.getNewAlbums(tag, page, limit));
                executor.submit(() -> ncNewAlbumReq.getAllNewAlbums(tag, page, limit));
                executor.submit(() -> ncNewAlbumReq.getLangDiAlbums(tag, page, limit));
                executor.submit(() -> ncNewAlbumReq.getStyleAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.KG || src == NetResourceSource.ALL) {
                KgNewAlbumReq kgNewAlbumReq = KgNewAlbumReq.getInstance();
                executor.submit(() -> kgNewAlbumReq.getNewAlbums(tag, page, limit));
                executor.submit(() -> kgNewAlbumReq.getIpAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.QQ || src == NetResourceSource.ALL) {
                executor.submit(() -> QqNewAlbumReq.getInstance().getNewAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.DT || src == NetResourceSource.ALL) {
                executor.submit(() -> DtNewAlbumReq.getInstance().getCatAlbums(tag, page, limit));
            }
            if (src == NetResourceSource.DB || src == NetResourceSource.ALL) {
                executor.submit(() -> DbNewAlbumReq.getInstance().getCatAlbums(tag, page, limit));
            }
        }
        return executor.getResult();
    }
}
