package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkCommentReq {
    private static HkCommentReq instance;

    private HkCommentReq() {
    }

    public static HkCommentReq getInstance() {
        if (instance == null) instance = new HkCommentReq();
        return instance;
    }

    // 评论 API (好看)
    private final String COMMENTS_HK_API = "https://haokan.baidu.com/videoui/api/commentget?url_key=%s&pn=%s&rn=%s&child_rn=1";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = null;
        if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            id = mvInfo.getId();
        }

        String commentInfoBody = HttpRequest.get(String.format(COMMENTS_HK_API, id, page, limit))
                .executeAsStr();
        JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
        JSONObject data = commentInfoJson.getJSONObject("data");
        total = data.getIntValue("comment_count");
        JSONArray commentArray = data.getJSONArray("list");
        for (int i = 0, len = commentArray.size(); i < len; i++) {
            JSONObject commentJson = commentArray.getJSONObject(i);

            String userId = commentJson.getString("third_id");
            String username = commentJson.getString("uname");
            String profileUrl = commentJson.getString("avatar");
            String content = commentJson.getString("content");
            String time = TimeUtil.msToPhrase(commentJson.getLong("create_time") * 1000);
            Integer likedCount = commentJson.getIntValue("like_count");

            NetCommentInfo commentInfo = new NetCommentInfo();
            commentInfo.setSource(NetMusicSource.HK);
            commentInfo.setUserId(userId);
            commentInfo.setUsername(username);
            commentInfo.setProfileUrl(profileUrl);
            commentInfo.setContent(content);
            commentInfo.setTime(time);
            commentInfo.setLikedCount(likedCount);

            String finalProfileUrl1 = profileUrl;
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                commentInfo.setProfile(profile);
            });

            res.add(commentInfo);

            // 回复
            JSONArray replies = commentJson.getJSONArray("reply_list");
            for (int j = 0, s = replies.size(); j < s; j++) {
                JSONObject cj = replies.getJSONObject(j);

                userId = cj.getString("third_id");
                username = cj.getString("uname");
                profileUrl = cj.getString("avatar");
                content = cj.getString("content");
                time = TimeUtil.msToPhrase(cj.getLong("create_time") * 1000);
                likedCount = cj.getIntValue("like_count");

                NetCommentInfo ci = new NetCommentInfo();
                ci.setSource(NetMusicSource.HK);
                ci.setSub(true);
                ci.setUserId(userId);
                ci.setUsername(username);
                ci.setProfileUrl(profileUrl);
                ci.setContent(content);
                ci.setTime(time);
                ci.setLikedCount(likedCount);

                String finalProfileUrl = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                    ci.setProfile(profile);
                });

                res.add(ci);
            }
        }

        return new CommonResult<>(res, total);
    }
}
