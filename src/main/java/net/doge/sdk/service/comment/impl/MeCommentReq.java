package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeCommentReq {
    private static MeCommentReq instance;

    private MeCommentReq() {
    }

    public static MeCommentReq getInstance() {
        if (instance == null) instance = new MeCommentReq();
        return instance;
    }

    // 节目评论 API (猫耳)
    private final String COMMENTS_ME_API = "https://www.missevan.com/site/getcomment?type=%s&order=%s&eId=%s&p=%s&pagesize=%s";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = resource.getId();
        String typeStr = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) typeStr = "1";
        else if (resource instanceof NetPlaylistInfo) typeStr = "2";

        if (StringUtil.notEmpty(typeStr)) {
            String commentInfoBody = HttpRequest.get(String.format(COMMENTS_ME_API, typeStr, hotOnly ? 3 : 1, id, page, limit))
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("info").getJSONObject("comment");
            total = data.getJSONObject("pagination").getIntValue("count");
            JSONArray commentArray = data.getJSONArray("Datas");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);

                String userId = commentJson.getString("userid");
                String username = commentJson.getString("username");
                String profileUrl = commentJson.getString("icon");
                String content = commentJson.getString("comment_content");
                String time = TimeUtil.msToPhrase(commentJson.getLong("ctime") * 1000);
                Integer likedCount = commentJson.getIntValue("like_num");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetResourceSource.ME);
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

                JSONArray subComments = commentJson.getJSONArray("subcomments");
                for (int j = 0, s = subComments.size(); j < s; j++) {
                    JSONObject cj = subComments.getJSONObject(j);

                    userId = cj.getString("userid");
                    username = cj.getString("username");
                    profileUrl = cj.getString("icon");
                    content = cj.getString("comment_content");
                    time = TimeUtil.msToPhrase(cj.getLong("ctime") * 1000);
                    likedCount = cj.getIntValue("like_num");

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetResourceSource.ME);
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
