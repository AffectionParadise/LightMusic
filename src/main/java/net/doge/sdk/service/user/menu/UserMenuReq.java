package net.doge.sdk.service.user.menu;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.*;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.user.menu.impl.*;
import net.doge.util.core.net.UrlUtil;

public class UserMenuReq {
    private static UserMenuReq instance;

    private UserMenuReq() {
    }

    public static UserMenuReq getInstance() {
        if (instance == null) instance = new UserMenuReq();
        return instance;
    }

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo commentInfo, int page, int limit) {
        int source = commentInfo.getSource();
        String id = UrlUtil.encodeAll(commentInfo.getUserId());
        String name = commentInfo.getUsername();

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(source);
        userInfo.setId(id);
        userInfo.setName(name);

        return getUserPlaylists(userInfo, page, limit);
    }

    /**
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo commentInfo, int page, int limit) {
        int source = commentInfo.getSource();
        String id = UrlUtil.encodeAll(commentInfo.getUserId());

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(source);
        userInfo.setId(id);

        return getUserAlbums(userInfo, page, limit);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcUserMenuReq.getInstance().getUserPlaylists(userInfo, page, limit);
            case NetResourceSource.QQ:
                return QqUserMenuReq.getInstance().getUserPlaylists(userInfo, page, limit);
            case NetResourceSource.ME:
                return MeUserMenuReq.getInstance().getUserPlaylists(userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.QQ:
                return QqUserMenuReq.getInstance().getUserAlbums(userInfo, page, limit);
            case NetResourceSource.DB:
                return DbUserMenuReq.getInstance().getUserAlbums(userInfo, page);
            case NetResourceSource.DT:
                return DtUserMenuReq.getInstance().getUserAlbums(userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcUserMenuReq.getInstance().getUserRadios(userInfo, page, limit);
            case NetResourceSource.XM:
                return XmUserMenuReq.getInstance().getUserRadios(userInfo, page, limit);
            case NetResourceSource.ME:
                return MeUserMenuReq.getInstance().getUserRadios(userInfo, page, limit);
            case NetResourceSource.DB:
                return DbUserMenuReq.getInstance().getUserRadios(userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int sortType, int page, int limit, String cursor) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.HK:
                return HkUserMenuReq.getInstance().getUserVideos(userInfo, page, limit, cursor);
            case NetResourceSource.BI:
                return BiUserMenuReq.getInstance().getUserVideos(userInfo, sortType, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFollows(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            case NetResourceSource.XM:
                return XmUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            case NetResourceSource.ME:
                return MeUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            case NetResourceSource.FS:
                return FsUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            case NetResourceSource.DT:
                return DtUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            case NetResourceSource.BI:
                return BiUserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFans(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            case NetResourceSource.XM:
                return XmUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            case NetResourceSource.ME:
                return MeUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            case NetResourceSource.FS:
                return FsUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            case NetResourceSource.DT:
                return DtUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            case NetResourceSource.BI:
                return BiUserMenuReq.getInstance().getUserFans(userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }
}
