package net.doge.sdk.service.user.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.util.SdkUtil;

public class QqUserInfoReq {
    private static QqUserInfoReq instance;

    private QqUserInfoReq() {
    }

    public static QqUserInfoReq getInstance() {
        if (instance == null) instance = new QqUserInfoReq();
        return instance;
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        userInfo.setSign("");
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(userInfo.getAvatarUrl())));
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl("")));
    }
}
