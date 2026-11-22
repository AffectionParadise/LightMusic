package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.constant.qq.QqSearchDevice;
import net.doge.sdk.common.constant.qq.QqSearchType;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqUserSearchReq {
    private static QqUserSearchReq instance;

    private QqUserSearchReq() {
    }

    public static QqUserSearchReq getInstance() {
        if (instance == null) instance = new QqUserSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t;

        String userInfoBody = SdkCommon.qqSearchRequest(QqSearchDevice.PC, QqSearchType.USER, keyword, page, limit)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("req").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray userArray = data.getJSONObject("body").getJSONObject("user").getJSONArray("list");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("encrypt_uin");
            String userName = userJson.getString("title");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("pic");
            Integer fan = userJson.getIntValue("fans_num");
            Integer playlistCount = userJson.getIntValue("diss_num");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetResourceSource.QQ);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setAvatarUrl(avatarThumbUrl);
            userInfo.setFan(fan);
            userInfo.setPlaylistCount(playlistCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            r.add(userInfo);
        }

//        String userInfoBody = SdkCommon.qqSearchRequest(QqSearchDevice.MOBILE, QqSearchType.USER, keyword, page, limit)
//                .executeAsStr();
//        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
//        JSONObject data = userInfoJson.getJSONObject("req").getJSONObject("data");
//        t = data.getJSONObject("meta").getIntValue("sum");
//        JSONArray userArray = data.getJSONObject("body").getJSONArray("item_user");
//        for (int i = 0, len = userArray.size(); i < len; i++) {
//            JSONObject userJson = userArray.getJSONObject(i);
//
//            String userId = userJson.getString("encrypt_uin");
//            String userName = HtmlUtil.removeHtmlLabel(userJson.getString("title"));
//            String gender = "保密";
//            String avatarThumbUrl = userJson.getString("pic");
//            Integer fan = (int) LangUtil.parseNumber(RegexUtil.getGroup1("(.*?)人关注", userJson.getString("subtitle")));
//                Integer playlistCount = userJson.getIntValue("diss_num");
//
//            NetUserInfo userInfo = new NetUserInfo();
//            userInfo.setSource(NetResourceSource.QQ);
//            userInfo.setId(userId);
//            userInfo.setName(userName);
//            userInfo.setGender(gender);
//            userInfo.setAvatarThumbUrl(avatarThumbUrl);
//            userInfo.setAvatarUrl(avatarThumbUrl);
//            userInfo.setFan(fan);
//                userInfo.setPlaylistCount(playlistCount);
//            GlobalExecutors.imageExecutor.execute(() -> {
//                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
//                userInfo.setAvatarThumb(avatarThumb);
//            });
//
//            r.add(userInfo);
//        }

        return new CommonResult<>(r, t);
    }
}
