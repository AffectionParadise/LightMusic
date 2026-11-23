package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmCommentReq {
    private static XmCommentReq instance;

    private XmCommentReq() {
    }

    public static XmCommentReq getInstance() {
        if (instance == null) instance = new XmCommentReq();
        return instance;
    }

    // 电台评论 API (喜马拉雅)
    private final String RADIO_COMMENTS_XM_API = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1";
    // 节目评论 API (喜马拉雅)
    private final String COMMENTS_XM_API = "https://www.ximalaya.com/revision/comment/queryComments";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = resource.getId();
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        boolean isRadio = resource instanceof NetRadioInfo;

        JSONArray commentArray;
        if (isRadio) {
            Map<String, Object> params = new HashMap<>();
            params.put("albumId", id);
            params.put("order", hotOnly ? "content-score-desc" : "time-desc");
            params.put("pageId", page);
            params.put("pageSize", limit);
            String commentInfoBody = HttpRequest.get(RADIO_COMMENTS_XM_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            JSONObject comments = data.getJSONObject("comments");
            total = comments.getIntValue("totalCount");
            commentArray = comments.getJSONArray("list");
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("trackId", id);
            params.put("page", page);
            params.put("pageSize", limit);
            String commentInfoBody = HttpRequest.get(COMMENTS_XM_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            total = data.getIntValue("totalComment");
            commentArray = data.getJSONArray("comments");
        }
        if (JsonUtil.notEmpty(commentArray)) {
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);

                String userId = commentJson.getString("uid");
                String username = commentJson.getString("nickname");
                String smallHeader = commentJson.getString("smallHeader");
                String profileUrl = isRadio ? smallHeader.replaceFirst("http:", "https:") : "https:" + smallHeader;
                String content = commentJson.getString("content");
                String time = TimeUtil.msToPhrase(commentJson.getLong(isRadio ? "createdAt" : "commentTime"));
                Integer likedCount = commentJson.getIntValue("likes");
                Integer score = commentJson.getIntValue("newAlbumScore", -1);

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetResourceSource.XM);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLikedCount(likedCount);
                commentInfo.setScore(score);

                String finalProfileUrl1 = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                    commentInfo.setProfile(profile);
                });

                res.add(commentInfo);

                // 回复
                JSONArray replies = commentJson.getJSONArray("replies");
                if (JsonUtil.isEmpty(replies)) continue;
                for (int j = 0, s = replies.size(); j < s; j++) {
                    JSONObject cj = replies.getJSONObject(j);

                    userId = cj.getString("uid");
                    username = cj.getString("nickname");
                    smallHeader = cj.getString("smallHeader");
                    profileUrl = isRadio ? smallHeader.replaceFirst("http:", "https:") : "https:" + smallHeader;
                    content = cj.getString("content");
                    time = isRadio ? TimeUtil.msToPhrase(cj.getLong("createdAt")) : cj.getString("createAt");
                    likedCount = cj.getIntValue("likes");
                    score = cj.getIntValue("newAlbumScore", -1);

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetResourceSource.XM);
                    ci.setSub(true);
                    ci.setUserId(userId);
                    ci.setUsername(username);
                    ci.setProfileUrl(profileUrl);
                    ci.setContent(content);
                    ci.setTime(time);
                    ci.setLikedCount(likedCount);
                    ci.setScore(score);

                    String finalProfileUrl = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                        ci.setProfile(profile);
                    });

                    res.add(ci);
                }
            }
        }

        return new CommonResult<>(res, total);
    }
}
