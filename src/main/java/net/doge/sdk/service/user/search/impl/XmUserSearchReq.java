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

public class XmUserSearchReq {
    private static XmUserSearchReq instance;

    private XmUserSearchReq() {
    }

    public static XmUserSearchReq getInstance() {
        if (instance == null) instance = new XmUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (喜马拉雅)
    private final String SEARCH_USER_XM_API
            = "https://www.ximalaya.com/revision/search/main";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t = 0;

        Map<String, Object> params = new HashMap<>();
        params.put("kw", keyword);
        params.put("page", page);
        params.put("spellchecker", true);
        params.put("condition", "relation");
        params.put("rows", limit);
        params.put("core", "user");
        params.put("device", "iPhone");
        String userInfoBody = HttpRequest.get(SEARCH_USER_XM_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject result = userInfoJson.getJSONObject("data").getJSONObject("user");
        if (JsonUtil.notEmpty(result)) {
            t = result.getIntValue("total");
            JSONArray userArray = result.getJSONArray("docs");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("uid");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = userJson.getString("logoPic").replaceFirst("http:", "https:");
                Integer follow = userJson.getIntValue("followingsCount");
                Integer fan = userJson.getIntValue("followersCount");
                Integer radioCount = userJson.getIntValue("albumCount");
                Integer programCount = userJson.getIntValue("tracksCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetResourceSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                r.add(userInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
