package net.doge.sdk.service.rank.info.impl;

import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.playlist.info.PlaylistInfoReq;

public class NcRankInfoReq {
    private static NcRankInfoReq instance;

    private NcRankInfoReq() {
    }

    public static NcRankInfoReq getInstance() {
        if (instance == null) instance = new NcRankInfoReq();
        return instance;
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int source, int page, int limit) {
        return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(id, source, page, limit);
    }
}
