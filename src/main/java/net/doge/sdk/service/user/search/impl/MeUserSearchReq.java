package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeUserSearchReq {
    private static MeUserSearchReq instance;

    private MeUserSearchReq() {
    }

    public static MeUserSearchReq getInstance() {
        if (instance == null) instance = new MeUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (猫耳)
    private final String SEARCH_USER_ME_API = "https://www.missevan.com/sound/getsearch?s=%s&type=1&p=%s&page_size=%s";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_ME_API, encodedKeyword, page, limit))
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
