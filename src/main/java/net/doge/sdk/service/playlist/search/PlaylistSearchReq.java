package net.doge.sdk.service.playlist.search;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.playlist.search.impl.*;

public class PlaylistSearchReq {
    private static PlaylistSearchReq instance;

    private PlaylistSearchReq() {
    }

    public static PlaylistSearchReq getInstance() {
        if (instance == null) instance = new PlaylistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(int src, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            executor.submit(() -> KgPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            executor.submit(() -> QqPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            executor.submit(() -> KwPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            executor.submit(() -> MgPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
            executor.submit(() -> FsPlaylistSearchReq.getInstance().searchPlaylists(keyword, page, limit));
        return executor.getResult();
    }
}
