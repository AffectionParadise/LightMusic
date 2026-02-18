package net.doge.sdk.service.playlist.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.playlist.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class PlaylistInfoReq {
    private static PlaylistInfoReq instance;

    private PlaylistInfoReq() {
    }

    public static PlaylistInfoReq getInstance() {
        if (instance == null) instance = new PlaylistInfoReq();
        return instance;
    }

    /**
     * 根据歌单 id 和 source 预加载歌单信息
     */
    public void preloadPlaylistInfo(NetPlaylistInfo playlistInfo) {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImgThumb(SdkUtil.extractCover(playlistInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(int source, String id) {
        switch (source) {
            case NetMusicSource.NC:
                return NcPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.KG:
                return KgPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.QQ:
                return QqPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.KW:
                return KwPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.MG:
                return MgPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.QI:
                return QiPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.ME:
                return MePlaylistInfoReq.getInstance().getPlaylistInfo(id);
            case NetMusicSource.BI:
                return BiPlaylistInfoReq.getInstance().getPlaylistInfo(id);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;
        int source = playlistInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                NcPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.KG:
                KgPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.QQ:
                QqPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.KW:
                KwPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.MG:
                MgPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.QI:
                QiPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.FS:
                FsPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.ME:
                MePlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
            case NetMusicSource.BI:
                BiPlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
                break;
        }
    }

    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(String id, int source, int page, int limit) {
        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
        playlistInfo.setSource(source);
        playlistInfo.setId(id);
        return getMusicInfoInPlaylist(playlistInfo, page, limit);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        int source = playlistInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.KG:
                return KgPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.QQ:
                return QqPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.KW:
                return KwPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.MG:
                return MgPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.QI:
                return QiPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.FS:
                return FsPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.ME:
                return MePlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            case NetMusicSource.BI:
                return BiPlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
