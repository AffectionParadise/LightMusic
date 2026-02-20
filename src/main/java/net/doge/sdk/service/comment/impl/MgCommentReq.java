package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgCommentReq {
    private static MgCommentReq instance;

    private MgCommentReq() {
    }

    public static MgCommentReq getInstance() {
        if (instance == null) instance = new MgCommentReq();
        return instance;
    }

    // 热门评论 API (咪咕)
//    private final String HOT_COMMENTS_MG_API = "https://music.migu.cn/v3/api/comment/listTopComments?targetId=%s&pageNo=%s&pageSize=%s";
    private final String HOT_COMMENTS_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/user/comment/stack/v1.0?queryType=1&resourceId=%s&resourceType=2&pageSize=%s&commentId=%s";
    // 最新评论 API (咪咕)
//    private final String NEW_COMMENTS_MG_API = "https://music.migu.cn/v3/api/comment/listComments?targetId=%s&pageNo=%s&pageSize=%s";
    private final String NEW_COMMENTS_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/user/comment/stack/v1.0?queryType=1&resourceId=%s&resourceType=2&pageSize=%s&commentId=%s";
    // 歌曲信息 API (咪咕)
    private final String SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit, String cursor) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = resource.getId();
        boolean hotOnly = I18n.getText("hotComment").equals(type);

        // 咪咕
        if (resource instanceof NetMusicInfo) {
            // 先根据 cid 获取 songId
            String songBody = HttpRequest.get(String.format(SONG_DETAIL_MG_API, id))
                    .executeAsStr();
            id = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0).getString("songId");
            // 评论
//            String url = hotOnly ? HOT_COMMENTS_MG_API : NEW_COMMENTS_MG_API;
//            String commentInfoBody = HttpRequest.get(String.format(url, id, page, limit))
//                    .header(Header.REFERER, "https://music.migu.cn")
//                    .executeAsStr();
//            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
//            JSONObject data = commentInfoJson.getJSONObject("data");
//            if (JsonUtil.notEmpty(data)) {
//                total = data.getIntValue("itemTotal");
//                JSONArray commentArray = data.getJSONArray("items");
//                for (int i = 0, len = commentArray.size(); i < len; i++) {
//                    JSONObject commentJson = commentArray.getJSONObject(i);
//                    JSONObject author = commentJson.getJSONObject("author");
//
//                    String userId = author.getString("id");
//                    String username = author.getString("name");
//                    String profileUrl = "https:" + author.getString("avatar");
//                    String content = commentJson.getString("body");
//                    String time = TimeUtil.strToPhrase(commentJson.getString("createTime"));
//                    Integer likedCount = commentJson.getIntValue("praiseCount");
//
//                    NetCommentInfo commentInfo = new NetCommentInfo();
//                    commentInfo.setSource(NetMusicSource.MG);
//                    commentInfo.setUserId(userId);
//                    commentInfo.setUsername(username);
//                    commentInfo.setProfileUrl(profileUrl);
//                    commentInfo.setContent(content);
//                    commentInfo.setTime(time);
//                    commentInfo.setLikedCount(likedCount);
//                    String finalProfileUrl = profileUrl;
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
//                        commentInfo.setProfile(profile);
//                    });
//
//                    res.add(commentInfo);
//
//                    // 回复
//                    JSONArray replies = commentJson.getJSONArray("replyCommentList");
//                    for (int j = 0, s = replies.size(); j < s; j++) {
//                        JSONObject reply = replies.getJSONObject(j);
//                        author = reply.getJSONObject("author");
//
//                        userId = author.getString("id");
//                        username = author.getString("name");
//                        profileUrl = "https:" + author.getString("avatar");
//                        content = reply.getString("body");
//                        time = TimeUtil.strToPhrase(reply.getString("createTime"));
//                        likedCount = reply.getIntValue("praiseCount");
//
//                        NetCommentInfo rCommentInfo = new NetCommentInfo();
//                        rCommentInfo.setSource(NetMusicSource.MG);
//                        rCommentInfo.setSub(true);
//                        rCommentInfo.setUserId(userId);
//                        rCommentInfo.setUsername(username);
//                        rCommentInfo.setProfileUrl(profileUrl);
//                        rCommentInfo.setContent(content);
//                        rCommentInfo.setTime(time);
//                        rCommentInfo.setLikedCount(likedCount);
//                        String finalProfileUrl1 = profileUrl;
//                        GlobalExecutors.imageExecutor.execute(() -> {
//                            BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
//                            rCommentInfo.setProfile(profile);
//                        });
//
//                        res.add(rCommentInfo);
//                    }
//                }
//            }

            String url = hotOnly ? HOT_COMMENTS_MG_API : NEW_COMMENTS_MG_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, limit, cursor))
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                JSONArray commentArray;
                if (hotOnly) {
                    commentArray = data.getJSONArray("hotComments");
                    total = commentArray.size();
                } else {
                    commentArray = data.getJSONArray("comments");
                    total = data.getIntValue("commentNums");
                }
                for (int i = hotOnly ? (page - 1) * limit : 0, len = hotOnly ? Math.min(commentArray.size(), page * limit) : commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);
                    JSONObject user = commentJson.getJSONObject("user");

                    if (i == len - 1) cursor = commentJson.getString("commentId");
                    String userId = user.getString("userId");
                    String username = user.getString("nickName");
                    String profileUrl = user.getString("bigIcon");
                    String content = commentJson.getString("commentInfo");
                    String time = TimeUtil.strToPhrase(commentJson.getString("commentTime"));
                    Integer likedCount = commentJson.getJSONObject("opNumItem").getIntValue("thumbNum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetResourceSource.MG);
                    commentInfo.setUserId(userId);
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

                    // 回复
                    JSONArray replies = commentJson.getJSONArray("replyComments");
                    for (int j = 0, s = replies.size(); j < s; j++) {
                        JSONObject reply = replies.getJSONObject(j);
                        user = reply.getJSONObject("user");

                        userId = user.getString("userId");
                        username = user.getString("nickName");
                        profileUrl = user.getString("bigIcon");
                        content = reply.getString("replyInfo");
                        time = TimeUtil.strToPhrase(reply.getString("replyTime"));
//                        likedCount = reply.getJSONObject("opNumItem").getIntValue("thumbNum");

                        NetCommentInfo rCommentInfo = new NetCommentInfo();
                        rCommentInfo.setSource(NetResourceSource.MG);
                        rCommentInfo.setSub(true);
                        rCommentInfo.setUserId(userId);
                        rCommentInfo.setUsername(username);
                        rCommentInfo.setProfileUrl(profileUrl);
                        rCommentInfo.setContent(content);
                        rCommentInfo.setTime(time);
//                        rCommentInfo.setLikedCount(likedCount);
                        String finalProfileUrl1 = profileUrl;
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                            rCommentInfo.setProfile(profile);
                        });

                        res.add(rCommentInfo);
                    }
                }
            }
        }

        return new CommonResult<>(res, total, cursor);
    }
}
