package net.doge.sdk.service.user.info;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.user.info.impl.*;
import net.doge.sdk.util.SdkUtil;

public class UserInfoReq {
    private static UserInfoReq instance;

    private UserInfoReq() {
    }

    public static UserInfoReq getInstance() {
        if (instance == null) instance = new UserInfoReq();
        return instance;
    }

    /**
     * 根据用户 id 预加载用户信息
     */
    public void preloadUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatarThumb(SdkUtil.extractCover(userInfo.getAvatarThumbUrl())));
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;
        int source = userInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                NcUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.QQ:
                QqUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.XM:
                XmUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.HF:
                HfUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.GG:
                GgUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.ME:
                MeUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.FS:
                FsUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.HK:
                HkUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.DB:
                DbUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.DT:
                DtUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
            case NetMusicSource.BI:
                BiUserInfoReq.getInstance().fillUserInfo(userInfo);
                break;
        }
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcUserInfoReq.getInstance().getMusicInfoInUser(recordType, userInfo, page, limit);
            case NetMusicSource.XM:
                return XmUserInfoReq.getInstance().getMusicInfoInUser(recordType, userInfo, page, limit);
            case NetMusicSource.HF:
                return HfUserInfoReq.getInstance().getMusicInfoInUser(userInfo, page, limit);
            case NetMusicSource.GG:
                return GgUserInfoReq.getInstance().getMusicInfoInUser(userInfo, page, limit);
            case NetMusicSource.ME:
                return MeUserInfoReq.getInstance().getMusicInfoInUser(recordType, userInfo, page, limit);
            case NetMusicSource.FS:
                return FsUserInfoReq.getInstance().getMusicInfoInUser(userInfo, page, limit);
            case NetMusicSource.BI:
                return BiUserInfoReq.getInstance().getMusicInfoInUser(recordType, userInfo, page, limit);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(int source, String id) {
        switch (source) {
            case NetMusicSource.NC:
                return NcUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.XM:
                return XmUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.HF:
                return HfUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.GG:
                return GgUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.ME:
                return MeUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.FS:
                return FsUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.HK:
                return HkUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.DB:
                return DbUserInfoReq.getInstance().getUserInfo(id);
            case NetMusicSource.BI:
                return BiUserInfoReq.getInstance().getUserInfo(id);
            default:
                return CommonResult.create();
        }
    }
}
