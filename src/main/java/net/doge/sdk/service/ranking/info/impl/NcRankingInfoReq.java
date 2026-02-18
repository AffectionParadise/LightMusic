package net.doge.sdk.service.ranking.info.impl;

import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.playlist.info.PlaylistInfoReq;

public class NcRankingInfoReq {
    private static NcRankingInfoReq instance;

    private NcRankingInfoReq() {
    }

    public static NcRankingInfoReq getInstance() {
        if (instance == null) instance = new NcRankingInfoReq();
        return instance;
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String id, int source, int page, int limit) {
        return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(id, source, page, limit);
    }
}
