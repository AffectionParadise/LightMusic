package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.fs.FiveSingReqOptEnum;
import net.doge.sdk.common.opt.fs.FiveSingReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FsCommentReq {
    private static FsCommentReq instance;

    private FsCommentReq() {
    }

    public static FsCommentReq getInstance() {
        if (instance == null) instance = new FsCommentReq();
        return instance;
    }

    // 评论 API (5sing)
    private final String COMMENTS_FS_API = "http://service.5sing.kugou.com/%s/comments/list1";
    //  MV 评论 API (5sing)
    private final String MV_COMMENTS_FS_API = "http://service.5sing.kugou.com/mv/CommentList";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        boolean isMv = false;
        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            id = musicInfo.getId();
        } else if (resource instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) resource;
            id = playlistInfo.getId();
        } else if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            id = mvInfo.getId();
            isMv = true;
        }

        String url = "";
        Map<String, Object> params = new TreeMap<>();
        if (resource instanceof NetMusicInfo) {
            String[] sp = id.split("_");
            url = String.format(COMMENTS_FS_API, sp[0]);
            params.put("rootId", sp[1]);
            params.put("page", page);
            params.put("limit", limit);
        } else if (resource instanceof NetPlaylistInfo) {
            url = String.format(COMMENTS_FS_API, "dynamicSongList");
            params.put("rootId", id);
            params.put("page", page);
            params.put("limit", limit);
        } else if (isMv) {
            url = MV_COMMENTS_FS_API;
            params.put("mvId", id);
            params.put("page", page);
        }
        Map<FiveSingReqOptEnum, Object> options = FiveSingReqOptsBuilder.get(url);
        String commentInfoBody = SdkCommon.fsRequest(params, null, options)
                .executeAsStr();
        JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
        JSONObject data = commentInfoJson.getJSONObject("data");
        JSONArray commentArray = data.getJSONArray(hotOnly && isMv ? "hotList" : "comments");
        if (isMv) {
            if (hotOnly) total = commentArray.size();
            else {
                int count = data.getIntValue("count"), lim = 10;
                total = PageUtil.totalPage(count, lim) * limit;
            }
        } else total = data.getJSONObject("page").getIntValue("totalCount");
        if (isMv) {
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject userJson = commentJson.getJSONObject("user");

                String userId = userJson.getString("ID");
                String username = userJson.getString("NN");
                String profileUrl = userJson.getString("I");
                String content = commentJson.getString("content");
                Integer likeCount = commentJson.getIntValue("like");
                String time = TimeUtil.strToPhrase(commentJson.getString("createTime"));

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.FS);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setLikedCount(likeCount);
                commentInfo.setTime(time);

                String finalProfileUrl1 = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                    commentInfo.setProfile(profile);
                });

                res.add(commentInfo);

                // 回复
                JSONArray replies = commentJson.getJSONArray("replys");
                for (int j = 0, s = replies.size(); j < s; j++) {
                    JSONObject cj = replies.getJSONObject(j);
                    JSONObject uj = cj.getJSONObject("user");

                    userId = uj.getString("ID");
                    username = uj.getString("NN");
                    profileUrl = uj.getString("I");
                    content = cj.getString("content");
                    time = TimeUtil.strToPhrase(cj.getString("createTime"));

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetMusicSource.FS);
                    ci.setSub(true);
                    ci.setUserId(userId);
                    ci.setUsername(username);
                    ci.setProfileUrl(profileUrl);
                    ci.setContent(content);
                    ci.setTime(time);

                    String finalProfileUrl = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                        ci.setProfile(profile);
                    });

                    res.add(ci);
                }
            }
        } else {
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject userJson = commentJson.getJSONObject("user");

                String userId = userJson.getString("id");
                String username = userJson.getString("nickname");
                String profileUrl = userJson.getString("img");
                String content = commentJson.getString("content").trim();
                String time = TimeUtil.strToPhrase(commentJson.getString("createTime"));

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.FS);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);

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
