package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.*;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwCommentReq {
    private static KwCommentReq instance;

    private KwCommentReq() {
    }

    public static KwCommentReq getInstance() {
        if (instance == null) instance = new KwCommentReq();
        return instance;
    }

    // 评论 API (酷我)
    private final String COMMENTS_KW_API = "https://comment.kuwo.cn/com.s";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = resource.getId();
        String typeStr = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) typeStr = "15";
        else if (resource instanceof NetPlaylistInfo) typeStr = "8";
        else if (resource instanceof NetMvInfo) typeStr = "7";
        else if (resource instanceof NetRankInfo) typeStr = "2";

        if (StringUtil.notEmpty(typeStr)) {
            String ref = "";
            switch (Integer.parseInt(typeStr)) {
                case 15:
                    ref = "https://kuwo.cn/play_detail/" + UrlUtil.encodeAll(id);
                    break;
                case 7:
                    ref = "https://kuwo.cn/mvplay/" + UrlUtil.encodeAll(id);
                    break;
                case 8:
                    ref = "https://kuwo.cn/playlist_detail/" + UrlUtil.encodeAll(id);
                    break;
                case 2:
                    ref = "https://kuwo.cn/rankList/" + UrlUtil.encodeAll(id);
                    break;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("digest", typeStr);
            params.put("sid", id);
            params.put("type", hotOnly ? "get_rec_comment" : "get_comment");
            params.put("f", "web");
            params.put("page", page);
            params.put("rows", limit);
            params.put("uid", 0);
            params.put("prod", "newWeb");
            params.put("httpsStatus", 1);
            String commentInfoBody = HttpRequest.get(COMMENTS_KW_API)
                    .queryParams(params)
                    .referer(ref)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONArray commentArray = null;
            if (!commentInfoJson.containsKey("data")) {
                total = commentInfoJson.getIntValue("total");
                commentArray = commentInfoJson.getJSONArray("rows");
            }
            if (JsonUtil.notEmpty(commentArray)) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("u_name");
                    String profileUrl = commentJson.getString("u_pic");
                    String content = commentJson.getString("msg");
                    String time = TimeUtil.strToPhrase(commentJson.getString("time"));
                    Integer likedCount = commentJson.getIntValue("like_num");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetResourceSource.KW);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    String finalProfileUrl = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);

                    // 被回复的评论
                    JSONObject reply = commentJson.getJSONObject("reply");
                    if (JsonUtil.isEmpty(reply)) continue;
                    username = reply.getString("u_name");
                    profileUrl = reply.getString("u_pic");
                    content = reply.getString("msg");
                    time = TimeUtil.strToPhrase(reply.getString("time"));
                    likedCount = reply.getIntValue("like_num");

                    NetCommentInfo rCommentInfo = new NetCommentInfo();
                    rCommentInfo.setSource(NetResourceSource.KW);
                    rCommentInfo.setSub(true);
                    rCommentInfo.setUsername(username);
                    rCommentInfo.setProfileUrl(profileUrl);
                    rCommentInfo.setContent(content);
                    rCommentInfo.setTime(time);
                    rCommentInfo.setLikedCount(likedCount);
                    String finalProfileUrl1 = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                        rCommentInfo.setProfile(profile);
                    });

                    res.add(rCommentInfo);
                }
            }
        }

        return new CommonResult<>(res, total);
    }
}
