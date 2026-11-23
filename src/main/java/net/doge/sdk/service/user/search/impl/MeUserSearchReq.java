package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MeUserSearchReq {
    private static MeUserSearchReq instance;

    private MeUserSearchReq() {
    }

    public static MeUserSearchReq getInstance() {
        if (instance == null) instance = new MeUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (猫耳)
    private final String SEARCH_USER_ME_API = "https://www.missevan.com/sound/getsearch";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("s", keyword);
        params.put("type", 1);
        params.put("p", page);
        params.put("page_size", limit);
        String userInfoBody = HttpRequest.get(SEARCH_USER_ME_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject info = userInfoJson.getJSONObject("info");
        t = info.getJSONObject("pagination").getIntValue("count");
        JSONArray userArray = info.getJSONArray("Datas");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("username");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("avatar2");
//                String bgImgUrl = userJson.getString("coverurl2");
            Integer follow = userJson.getIntValue("follownum");
            Integer fan = userJson.getIntValue("fansnum");
//                Integer radioCount = userJson.getIntValue("albumnum");
            Integer programCount = userJson.getIntValue("soundnum");
//                String sign = userJson.getString("userintro");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetResourceSource.ME);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setFollow(follow);
            userInfo.setFan(fan);
//                userInfo.setRadioCount(radioCount);
            userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);
//                userInfo.setBgImgUrl(bgImgUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            r.add(userInfo);
        }
        return new CommonResult<>(r, t);
    }
}
