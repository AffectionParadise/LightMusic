package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsCommentReq {
    private static QsCommentReq instance;

    private QsCommentReq() {
    }

    public static QsCommentReq getInstance() {
        if (instance == null) instance = new QsCommentReq();
        return instance;
    }

    // 评论 API (汽水)
    private final String COMMENTS_QS_API = "https://api.qishui.com/luna/pc/comments";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = resource.getId();
        boolean hotOnly = I18n.getText("hotComment").equals(type);

        Map<String, Object> params = new HashMap<>();
        // 任意资源 id 都可以使用该参数
        params.put("group_id", id);
        params.put("cursor", (page - 1) * limit);
        params.put("count", limit);
        params.put("group_type", 0);
        params.put("image_strategy", 2);
        params.put("sort_type", hotOnly ? "hot" : "time");
        String commentInfoBody = SdkCommon.qsRequest(COMMENTS_QS_API, params)
                .executeAsStr();
        JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
        JSONArray commentArray = commentInfoJson.getJSONArray("comments");
        total = commentInfoJson.getIntValue("count");
        if (JsonUtil.notEmpty(commentArray)) {
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject userJson = commentJson.getJSONObject("user");

                String userId = userJson.getString("id");
                String username = userJson.getString("nickname");
                String profileUrl = userJson.getJSONObject("medium_avatar_url").getJSONArray("urls").getString(0);
                String content = commentJson.getString("content");
                String time = TimeUtil.msToPhrase(commentJson.getLong("time_created") * 1000);
                String location = commentJson.getString("ip_label");
                Integer likedCount = commentJson.getIntValue("count_digged");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetResourceSource.QS);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLocation(location);
                commentInfo.setLikedCount(likedCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                    commentInfo.setProfile(profile);
                });

                res.add(commentInfo);
            }
        }

        return new CommonResult<>(res, total);
    }
}
