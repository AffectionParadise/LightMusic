package net.doge.sdk.service.playlist.menu;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.playlist.menu.impl.NcPlaylistMenuReq;

public class PlaylistMenuReq {
    private static PlaylistMenuReq instance;

    private PlaylistMenuReq() {
    }

    public static PlaylistMenuReq getInstance() {
        if (instance == null) instance = new PlaylistMenuReq();
        return instance;
    }

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo netPlaylistInfo) {
        int source = netPlaylistInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcPlaylistMenuReq.getInstance().getSimilarPlaylists(netPlaylistInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取歌单收藏者
     *
     * @return
     */
    public CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo playlistInfo, int page, int limit) {
        int source = playlistInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcPlaylistMenuReq.getInstance().getPlaylistSubscribers(playlistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
