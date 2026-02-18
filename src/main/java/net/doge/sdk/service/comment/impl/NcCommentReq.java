package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.MvInfoType;
import net.doge.entity.service.*;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcCommentReq {
    private static NcCommentReq instance;

    private NcCommentReq() {
    }

    public static NcCommentReq getInstance() {
        if (instance == null) instance = new NcCommentReq();
        return instance;
    }

    // 评论 API (网易云)
    private final String COMMENTS_NC_API = "https://music.163.com/api/v2/resource/comments";
    //    private final String COMMENTS_NC_API = "https://music.163.com/weapi/comment/resource/comments/get";
    // mlog id 转视频 id API (网易云)
    private final String MLOG_TO_VIDEO_NC_API = "https://music.163.com/weapi/mlog/video/convert/id";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit, String cursor) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = null;
        String typeStr = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            // 网易云需要先判断是普通歌曲还是电台节目，酷狗歌曲获取评论需要 hash
            boolean hasProgramId = musicInfo.hasProgramId();
            id = hasProgramId ? musicInfo.getProgramId() : musicInfo.getId();
            typeStr = hasProgramId ? "A_DJ_1_" : "R_SO_4_";
        } else if (resource instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) resource;
            id = playlistInfo.getId();
            typeStr = "A_PL_0_";
        } else if (resource instanceof NetAlbumInfo) {
            NetAlbumInfo albumInfo = (NetAlbumInfo) resource;
            id = albumInfo.getId();
            typeStr = "R_AL_3_";
        } else if (resource instanceof NetRadioInfo) {
            NetRadioInfo radioInfo = (NetRadioInfo) resource;
            id = radioInfo.getId();
            typeStr = "A_DR_14_";
        } else if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            // 网易云需要判断是视频还是 MV 还是 Mlog
            boolean isVideo = mvInfo.isVideo();
            boolean isMlog = mvInfo.isMlog();
            id = mvInfo.getId();

            // Mlog 需要先获取视频 id，并转为视频类型
            if (isMlog) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String body = SdkCommon.ncRequest(Method.POST, MLOG_TO_VIDEO_NC_API, String.format("{\"mlogId\":\"%s\"}", id), options)
                        .executeAsStr();
                id = JSONObject.parseObject(body).getString("data");
                mvInfo.setId(id);
                mvInfo.setType(MvInfoType.VIDEO);
            }
            typeStr = isVideo || isMlog ? "R_VI_62_" : "R_MV_5_";
        } else if (resource instanceof NetRankingInfo) {
            NetRankingInfo rankingInfo = (NetRankingInfo) resource;
            id = rankingInfo.getId();
            // 网易 QQ 酷我 猫耳
            typeStr = "A_PL_0_";
        }

        if (StringUtil.notEmpty(typeStr)) {
            String threadId = typeStr + id;
            int sortType = hotOnly ? 2 : 3;
            String cur = "";
            switch (sortType) {
                case 2:
                    cur = "normalHot#" + (page - 1) * limit;
                    break;
                case 3:
                    cur = StringUtil.isEmpty(cursor) ? "0" : cursor;
                    break;
            }
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/v2/resource/comments");
            String commentInfoBody = SdkCommon.ncRequest(Method.POST, COMMENTS_NC_API,
                            String.format("{\"threadId\":\"%s\",\"showInner\":true,\"pageNo\":%s,\"pageSize\":%s,\"cursor\":\"%s\",\"sortType\":%s}",
                                    threadId, page, limit, cur, sortType), options)
                    .executeAsStr();
//            String threadId = typeStr + id;
//            int sortType = hotOnly ? 2 : 3;
//            String cur = "";
//            switch (sortType) {
//                case 2:
//                    cur = "" + (page - 1) * limit;
//                    break;
//                case 3:
//                    cur = StringUtil.isEmpty(cursor) ? "-1" : cursor;
//                    break;
//            }
//            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
//            String commentInfoBody = SdkCommon.ncRequest(Method.POST, COMMENTS_NC_API,
//                            String.format("{\"threadId\":\"%s\",\"rid\":\"%s\",\"offset\":%s,\"pageNo\":%s,\"pageSize\":%s,\"cursor\":%s,\"orderType\":%s}",
//                                    threadId, threadId, (page - 1) * limit, page, limit, cur, sortType), options)
//                    .executeAsync()
//                    .body();
            JSONObject data = JSONObject.parseObject(commentInfoBody).getJSONObject("data");
            total = data.getIntValue("totalCount");
            // 按时间排序时才需要 cursor ！
            if (!hotOnly) cursor = data.getString("cursor");
            JSONArray commentArray = data.getJSONArray("comments");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject user = commentJson.getJSONObject("user");

                String userId = user.getString("userId");
                String username = user.getString("nickname");
                String profileUrl = user.getString("avatarUrl");
                String content = StringUtil.trimStringWith(commentJson.getString("content"), '\n');
                String time = TimeUtil.msToPhrase(commentJson.getLong("time"));
                String location = commentJson.getJSONObject("ipLocation").getString("location");
                Integer likedCount = commentJson.getIntValue("likedCount");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLocation(location);
                commentInfo.setLikedCount(likedCount);

                NetCommentInfo finalCommentInfo = commentInfo;
                String finalProfileUrl = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                    finalCommentInfo.setProfile(profile);
                });

                res.add(commentInfo);

                // 被回复的评论
                JSONArray beReplied = commentJson.getJSONArray("beReplied");
                if (JsonUtil.isEmpty(beReplied)) continue;
                for (int j = 0, l = beReplied.size(); j < l; j++) {
                    commentJson = beReplied.getJSONObject(j);

                    user = commentJson.getJSONObject("user");
                    userId = user.getString("userId");
                    username = user.getString("nickname");
                    profileUrl = user.getString("avatarUrl");
                    content = commentJson.getString("content");
                    location = commentJson.getJSONObject("ipLocation").getString("location");

                    commentInfo = new NetCommentInfo();
                    commentInfo.setSub(true);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setLocation(location);

                    NetCommentInfo finalCommentInfo1 = commentInfo;
                    String finalProfileUrl1 = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                        finalCommentInfo1.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            }
        }

        return new CommonResult<>(res, total, cursor);
    }
}
