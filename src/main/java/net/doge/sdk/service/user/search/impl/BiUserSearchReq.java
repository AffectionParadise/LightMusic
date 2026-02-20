package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiUserSearchReq {
    private static BiUserSearchReq instance;

    private BiUserSearchReq() {
    }

    public static BiUserSearchReq getInstance() {
        if (instance == null) instance = new BiUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (哔哩哔哩)
    private final String SEARCH_USER_BI_API = "https://api.bilibili.com/x/web-interface/search/type?search_type=bili_user&keyword=%s&page=%s";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page) {
        List<NetUserInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_BI_API, encodedKeyword, page))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONArray userArray = data.getJSONArray("result");
        if (JsonUtil.notEmpty(userArray)) {
            t = data.getIntValue("numResults");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("mid");
                String userName = userJson.getString("uname");
                int gen = userJson.getIntValue("gender");
                String gender = gen == 1 ? "♂ 男" : gen == 2 ? "♀ 女" : "保密";
                String avatarThumbUrl = "https:" + userJson.getString("upic");
                Integer programCount = userJson.getIntValue("videos");
                Integer fan = userJson.getIntValue("fans");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetResourceSource.BI);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setProgramCount(programCount);
                userInfo.setFan(fan);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                r.add(userInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
