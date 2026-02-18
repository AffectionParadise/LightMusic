package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

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
            = "https://www.ximalaya.com/revision/search/main?kw=%s&page=%s&spellchecker=true&condition=relation&rows=%s&core=user&device=iPhone";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_XM_API, encodedKeyword, page, limit))
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
                userInfo.setSource(NetMusicSource.XM);
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
