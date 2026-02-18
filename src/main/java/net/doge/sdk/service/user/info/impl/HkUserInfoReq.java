package net.doge.sdk.service.user.info.impl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkUserInfoReq {
    private static HkUserInfoReq instance;

    private HkUserInfoReq() {
    }

    public static HkUserInfoReq getInstance() {
        if (instance == null) instance = new HkUserInfoReq();
        return instance;
    }

    // 用户信息 API (好看)
    private final String USER_DETAIL_HK_API = "https://haokan.baidu.com/author/%s";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HK_API, id))
                .cookie(SdkCommon.HK_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(RegexUtil.getGroup1("\"author_info\":(\\{.*?\\})", userInfoBody));

        if (!userInfo.hasSign()) userInfo.setSign(userInfoJson.getString("wishes"));
        if (!userInfo.hasFan()) userInfo.setFan(userInfoJson.getIntValue("fansCnt"));
        if (!userInfo.hasProgramCount()) userInfo.setProgramCount(userInfoJson.getIntValue("videoCnt"));

        String avatarUrl = userInfoJson.getString("avatar");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl("")));
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id) {
        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HK_API, id))
                .cookie(SdkCommon.HK_COOKIE)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(RegexUtil.getGroup1("\"author_info\":(\\{.*?\\})", userInfoBody));

        String userId = data.getString("id");
        String userName = data.getString("name");
        String gender = "保密";
        String avatarThumbUrl = data.getString("avatar");
        Integer fan = data.getIntValue("fansCnt");
        Integer programCount = data.getIntValue("videoCnt");

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(NetMusicSource.HK);
        userInfo.setId(userId);
        userInfo.setName(userName);
        userInfo.setGender(gender);
        userInfo.setAvatarThumbUrl(avatarThumbUrl);
        userInfo.setFan(fan);
        userInfo.setProgramCount(programCount);

        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
            userInfo.setAvatarThumb(avatarThumb);
        });

        res.add(userInfo);

        return new CommonResult<>(res, t);
    }
}
