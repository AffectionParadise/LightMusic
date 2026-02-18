package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.*;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwCommentReq {
    private static KwCommentReq instance;

    private KwCommentReq() {
    }

    public static KwCommentReq getInstance() {
        if (instance == null) instance = new KwCommentReq();
        return instance;
    }

    // 热门评论 API (酷我)
    private final String HOT_COMMENTS_KW_API = "https://comment.kuwo.cn/com.s?digest=%s&sid=%s&type=get_rec_comment&f=web&page=%s&rows=%s&uid=0&prod=newWeb&httpsStatus=1";
    // 最新评论 API (酷我)
    private final String NEW_COMMENTS_KW_API = "https://comment.kuwo.cn/com.s?digest=%s&sid=%s&type=get_comment&f=web&page=%s&rows=%s&uid=0&prod=newWeb&httpsStatus=1";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = null;
        String typeStr = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            id = musicInfo.getId();
            typeStr = "15";
        } else if (resource instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) resource;
            id = playlistInfo.getId();
            typeStr = "8";
        } else if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            id = mvInfo.getId();
            typeStr = "7";
        } else if (resource instanceof NetRankingInfo) {
            NetRankingInfo rankingInfo = (NetRankingInfo) resource;
            id = rankingInfo.getId();
            typeStr = "2";
        }

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
            String url = hotOnly ? HOT_COMMENTS_KW_API : NEW_COMMENTS_KW_API;
            // 最新评论
            String commentInfoBody = HttpRequest.get(String.format(url, typeStr, id, page, limit))
                    .header(Header.REFERER, ref)
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
                    commentInfo.setSource(NetMusicSource.KW);
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
                    rCommentInfo.setSource(NetMusicSource.KW);
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
