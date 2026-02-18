package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class XmCommentReq {
    private static XmCommentReq instance;

    private XmCommentReq() {
    }

    public static XmCommentReq getInstance() {
        if (instance == null) instance = new XmCommentReq();
        return instance;
    }

    // 电台热门评论 API (喜马拉雅)
    private final String RADIO_HOT_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=content-score-desc&pageId=%s&pageSize=%s";
    // 电台最新评论 API (喜马拉雅)
    private final String RADIO_NEW_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=time-desc&pageId=%s&pageSize=%s";
    // 节目评论 API (喜马拉雅)
    private final String COMMENTS_XM_API = "https://www.ximalaya.com/revision/comment/queryComments?trackId=%s&page=%s&pageSize=%s";

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
            String url = hotOnly ? RADIO_HOT_COMMENTS_XM_API : RADIO_NEW_COMMENTS_XM_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, page, limit))
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            JSONObject comments = data.getJSONObject("comments");
            total = comments.getIntValue("totalCount");
            commentArray = comments.getJSONArray("list");
        } else {
            String commentInfoBody = HttpRequest.get(String.format(COMMENTS_XM_API, id, page, limit))
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
                commentInfo.setSource(NetMusicSource.XM);
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
                    ci.setSource(NetMusicSource.XM);
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
