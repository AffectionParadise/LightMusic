package net.doge.sdk.service.artist.menu;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.*;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.artist.menu.impl.*;

public class ArtistMenuReq {
    private static ArtistMenuReq instance;

    private ArtistMenuReq() {
    }

    public static ArtistMenuReq getInstance() {
        if (instance == null) instance = new ArtistMenuReq();
        return instance;
    }

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                return NcArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            // 酷狗
            case NetMusicSource.KG:
                return KgArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            // QQ
            case NetMusicSource.QQ:
                return QqArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            // 酷我
            case NetMusicSource.KW:
                return KwArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            // 咪咕
            case NetMusicSource.MG:
                return MgArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            // 千千
            case NetMusicSource.QI:
                return QiArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                return NcArtistMenuReq.getInstance().getMvInfoInArtist(artistInfo, page, limit);
            // 酷狗
            case NetMusicSource.KG:
                return KgArtistMenuReq.getInstance().getMvInfoInArtist(artistInfo, page, limit);
            // QQ
            case NetMusicSource.QQ:
                return QqArtistMenuReq.getInstance().getMvInfoInArtist(artistInfo, page, limit);
            // 酷我
            case NetMusicSource.KW:
                return KwArtistMenuReq.getInstance().getMvInfoInArtist(artistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取歌手照片链接
     */
    public CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        int source = artistInfo.getSource();
        switch (source) {
            // 豆瓣
            case NetMusicSource.DB:
                return DbArtistMenuReq.getInstance().getArtistImgUrls(artistInfo, page);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo) {
        int source = netArtistInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                return NcArtistMenuReq.getInstance().getSimilarArtists(netArtistInfo);
            // 酷狗
            case NetMusicSource.KG:
                return KgArtistMenuReq.getInstance().getSimilarArtists(netArtistInfo);
            // QQ
            case NetMusicSource.QQ:
                return QqArtistMenuReq.getInstance().getSimilarArtists(netArtistInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                return NcArtistMenuReq.getInstance().getArtistFans(artistInfo, page, limit);
            // 猫耳
            case NetMusicSource.ME:
                return MeArtistMenuReq.getInstance().getArtistFans(artistInfo, page, limit);
            // 豆瓣
            case NetMusicSource.DB:
                return DbArtistMenuReq.getInstance().getArtistFans(artistInfo, page);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page, int limit) {
        int source = netArtistInfo.getSource();
        switch (source) {
            // 猫耳
            case NetMusicSource.ME:
                return MeArtistMenuReq.getInstance().getArtistBuddies(netArtistInfo, page, limit);
            // 豆瓣
            case NetMusicSource.DB:
                return DbArtistMenuReq.getInstance().getArtistBuddies(netArtistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        switch (source) {
            // 猫耳
            case NetMusicSource.ME:
                return MeArtistMenuReq.getInstance().getArtistRadios(artistInfo, page, limit);
            // 豆瓣
            case NetMusicSource.DB:
                return DbArtistMenuReq.getInstance().getArtistRadios(artistInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
