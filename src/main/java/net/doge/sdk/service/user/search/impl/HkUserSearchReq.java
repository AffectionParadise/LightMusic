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
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkUserSearchReq {
    private static HkUserSearchReq instance;

    private HkUserSearchReq() {
    }

    public static HkUserSearchReq getInstance() {
        if (instance == null) instance = new HkUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (好看)
    private final String SEARCH_USER_HK_API
            = "https://haokan.baidu.com/haokan/ui-search/pc/search/author?pn=%s&query=%s&rn=%s&timestamp=1683718494563&type=author&version=1";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_HK_API, page, encodedKeyword, limit))
                .cookie(SdkCommon.HK_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        t = data.getIntValue("has_more") == 1 ? (page + 1) * limit : page * limit;
        JSONArray userArray = data.getJSONArray("list");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("appid");
            String userName = userJson.getString("name");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("author_icon");
            Integer fan = userJson.getIntValue("fansCnt");
            Integer programCount = userJson.getIntValue("videoCnt");

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

            r.add(userInfo);
        }
        return new CommonResult<>(r, t);
    }
}
