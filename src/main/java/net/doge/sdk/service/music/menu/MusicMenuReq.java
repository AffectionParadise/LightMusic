package net.doge.sdk.service.music.menu;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.music.menu.impl.*;

public class MusicMenuReq {
    private static MusicMenuReq instance;

    private MusicMenuReq() {
    }

    public static MusicMenuReq getInstance() {
        if (instance == null) instance = new MusicMenuReq();
        return instance;
    }

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcMusicMenuReq.getInstance().getSimilarSongs(musicInfo);
            case NetMusicSource.QQ:
                return QqMusicMenuReq.getInstance().getSimilarSongs(musicInfo);
            case NetMusicSource.HF:
                return HfMusicMenuReq.getInstance().getSimilarSongs(musicInfo);
            case NetMusicSource.GG:
                return GgMusicMenuReq.getInstance().getSimilarSongs(musicInfo);
            case NetMusicSource.ME:
                return MeMusicMenuReq.getInstance().getSimilarSongs(musicInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcMusicMenuReq.getInstance().getRelatedPlaylists(musicInfo);
            case NetMusicSource.QQ:
                return QqMusicMenuReq.getInstance().getRelatedPlaylists(musicInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取推荐电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getRecRadios(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetMusicSource.ME:
                return MeMusicMenuReq.getInstance().getRecRadios(musicInfo);
            default:
                return CommonResult.create();
        }
    }
}
