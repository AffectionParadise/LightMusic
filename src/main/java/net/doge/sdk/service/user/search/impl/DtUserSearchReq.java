package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DtUserSearchReq {
    private static DtUserSearchReq instance;

    private DtUserSearchReq() {
    }

    public static DtUserSearchReq getInstance() {
        if (instance == null) instance = new DtUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (堆糖)
    private final String SEARCH_USER_DT_API = "https://www.duitang.com/napi/people/list/by_search/";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t = 0;

        Map<String, Object> params = new HashMap<>();
        params.put("kw", keyword);
        params.put("start", (page - 1) * limit);
        params.put("limit", limit);
        params.put("type", "people");
        params.put("_type", "");
        params.put("_", System.currentTimeMillis());
        String userInfoBody = HttpRequest.get(SEARCH_USER_DT_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONArray userArray = data.getJSONArray("object_list");
        if (JsonUtil.notEmpty(userArray)) {
            t = data.getIntValue("total");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                Integer follow = userJson.getIntValue("followCount");
                Integer fan = userJson.getIntValue("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetResourceSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
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
