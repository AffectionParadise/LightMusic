package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.BvAvConverter;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiCommentReq {
    private static BiCommentReq instance;

    private BiCommentReq() {
    }

    public static BiCommentReq getInstance() {
        if (instance == null) instance = new BiCommentReq();
        return instance;
    }

    // 视频评论 API (哔哩哔哩)
    private final String VIDEO_COMMENTS_BI_API = "https://api.bilibili.com/x/v2/reply?type=1&oid=%s&sort=%s&pn=%s&ps=%s";
    // 音频评论 API (哔哩哔哩)
    private final String SONG_COMMENTS_BI_API = "https://api.bilibili.com/x/v2/reply?type=14&oid=%s&sort=%s&pn=%s&ps=%s";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = resource.getId();
        boolean hotOnly = I18n.getText("hotComment").equals(type);

        int lim = Math.min(20, limit);
        String url = resource instanceof NetMvInfo ? String.format(VIDEO_COMMENTS_BI_API, BvAvConverter.getInstance().convertBv2Av(id), hotOnly ? 1 : 0, page, lim)
                : String.format(SONG_COMMENTS_BI_API, id, hotOnly ? 1 : 0, page, lim);
        String commentInfoBody = HttpRequest.get(url)
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
//                    // 貌似解析不了，触及到什么特殊字符了？
//                    .replaceAll("\"\\[\\d+.*?\\]\"", "\"\"");
        JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
        JSONObject data = commentInfoJson.getJSONObject("data");
        int count = data.getJSONObject("page").getIntValue("count");
        total = PageUtil.totalPage(count, lim) * limit;
        JSONArray commentArray = data.getJSONArray("replies");
        if (JsonUtil.notEmpty(commentArray)) {
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject member = commentJson.getJSONObject("member");

                String userId = commentJson.getString("mid");
                String username = member.getString("uname");
                String profileUrl = member.getString("avatar");
                String content = commentJson.getJSONObject("content").getString("message");
                String time = TimeUtil.msToPhrase(commentJson.getLong("ctime") * 1000);
                Integer likedCount = commentJson.getIntValue("like");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetResourceSource.BI);
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
                JSONArray replies = commentJson.getJSONArray("replies");
                if (JsonUtil.isEmpty(replies)) continue;
                for (int j = 0, s = replies.size(); j < s; j++) {
                    JSONObject cj = replies.getJSONObject(j);
                    JSONObject mem = cj.getJSONObject("member");

                    userId = cj.getString("mid");
                    username = mem.getString("uname");
                    profileUrl = mem.getString("avatar");
                    content = cj.getJSONObject("content").getString("message");
                    time = TimeUtil.msToPhrase(cj.getLong("ctime") * 1000);
                    likedCount = cj.getIntValue("like");

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetResourceSource.BI);
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
        }

        return new CommonResult<>(res, total);
    }
}
