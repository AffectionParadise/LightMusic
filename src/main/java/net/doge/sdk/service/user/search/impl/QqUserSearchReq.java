package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

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

        String userInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 8))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray userArray = data.getJSONObject("body").getJSONObject("user").getJSONArray("list");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("encrypt_uin");
            String userName = userJson.getString("title");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("pic");
            Integer fan = userJson.getIntValue("fans_num");
//                Integer playlistCount = userJson.getIntValue("diss_num");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.QQ);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setAvatarUrl(avatarThumbUrl);
            userInfo.setFan(fan);
//                userInfo.setPlaylistCount(playlistCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            r.add(userInfo);
        }
        return new CommonResult<>(r, t);
    }
}
