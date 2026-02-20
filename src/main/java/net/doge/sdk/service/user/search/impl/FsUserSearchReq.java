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
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class FsUserSearchReq {
    private static FsUserSearchReq instance;

    private FsUserSearchReq() {
    }

    public static FsUserSearchReq getInstance() {
        if (instance == null) instance = new FsUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (5sing)
    private final String SEARCH_USER_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=1&type=2";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_FS_API, encodedKeyword, page))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(userInfoBody);
        t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
        JSONArray userArray = data.getJSONArray("list");
        if (JsonUtil.notEmpty(userArray)) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String name = HtmlUtil.removeHtmlLabel(userJson.getString("nickName"));
                int sex = userJson.getIntValue("sex");
                String gender = sex == 0 ? "♂ 男" : sex == 1 ? "♀ 女" : "保密";
                String avatarThumbUrl = userJson.getString("pictureUrl");
                Integer follow = userJson.getIntValue("follow");
                Integer fan = userJson.getIntValue("fans");
                Integer programCount = userJson.getIntValue("totalSong");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetResourceSource.FS);
                userInfo.setId(userId);
                userInfo.setName(name);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);
                userInfo.setProgramCount(programCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });
                r.add(userInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
