package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgCommentReq {
    private static KgCommentReq instance;

    private KgCommentReq() {
    }

    public static KgCommentReq getInstance() {
        if (instance == null) instance = new KgCommentReq();
        return instance;
    }

    // 歌曲评论 API (酷狗)
    private final String HOT_COMMENTS_KG_API = "http://m.comment.service.kugou.com/r/v1/rank/topliked";
    private final String NEW_COMMENTS_KG_API = "http://m.comment.service.kugou.com/r/v1/rank/newest";
    // 歌单评论 API (酷狗)
    private final String PLAYLIST_COMMENTS_KG_API = "/m.comment.service/v1/cmtlist";
    // 专辑评论 API (酷狗)
    private final String ALBUM_COMMENTS_KG_API = "/m.comment.service/v1/cmtlist";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = null, anotherId = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            id = musicInfo.getHash();
            anotherId = musicInfo.getId();
        } else if (resource instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) resource;
            id = playlistInfo.getId();
        } else if (resource instanceof NetAlbumInfo) {
            NetAlbumInfo albumInfo = (NetAlbumInfo) resource;
            id = albumInfo.getId();
        }

        // 歌曲评论
        if (resource instanceof NetMusicInfo) {
            Map<String, Object> params = new TreeMap<>();
            params.put("clienttoken", 0);
            params.put("code", "fc4be23b4e972707f36b8a828a93ba8a");
            params.put("kugouid", 0);
            params.put("extdata", id);
            params.put("mixsongid", anotherId);
            params.put("p", page);
            params.put("pagesize", limit);
            params.put("uuid", 0);
            params.put("ver", 10);
            Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidGet(hotOnly ? HOT_COMMENTS_KG_API : NEW_COMMENTS_KG_API);
            String commentInfoBody = SdkCommon.kgRequest(params, null, options)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONArray commentArray = commentInfoJson.getJSONArray("list");
            total = commentInfoJson.getIntValue("count");
            if (JsonUtil.notEmpty(commentArray)) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("user_name");
                    String profileUrl = commentJson.getString("user_pic");
                    String content = commentJson.getString("content");
                    String time = TimeUtil.strToPhrase(commentJson.getString("addtime"));
                    String location = commentJson.getString("location");
                    Integer likedCount = commentJson.getJSONObject("like").getIntValue("likenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetResourceSource.KG);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLocation(location);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            }
        }
        // 歌单评论
        else if (resource instanceof NetPlaylistInfo) {
            Map<String, Object> params = new TreeMap<>();
            params.put("childrenid", id);
            params.put("p", page);
            params.put("pagesize", limit);
            params.put("need_show_image", 1);
            params.put("show_classify", 1);
            params.put("show_hotword_list", 1);
            params.put("code", "ca53b96fe5a1d9c22d71c8f522ef7c4f");
            params.put("content_type", 0);
            params.put("tag", 5);
            Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(PLAYLIST_COMMENTS_KG_API);
            String commentInfoBody = SdkCommon.kgRequest(params, null, options)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONArray commentArray = commentInfoJson.getJSONArray(hotOnly ? "weightList" : "list");
            if (JsonUtil.notEmpty(commentArray)) {
                int size = commentArray.size();
                total = hotOnly ? size : commentInfoJson.getIntValue("count");
                for (int i = hotOnly ? (page - 1) * limit : 0, len = hotOnly ? Math.min(size, page * limit) : size; i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("user_name");
                    String profileUrl = commentJson.getString("user_pic");
                    String content = commentJson.getString("content");
                    String time = TimeUtil.strToPhrase(commentJson.getString("addtime"));
                    String location = commentJson.getString("location");
                    Integer likedCount = commentJson.getJSONObject("like").getIntValue("likenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetResourceSource.KG);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLocation(location);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            }
        }
        // 专辑评论
        else if (resource instanceof NetAlbumInfo) {
            Map<String, Object> params = new TreeMap<>();
            params.put("childrenid", id);
            params.put("p", page);
            params.put("pagesize", limit);
            params.put("need_show_image", 1);
            params.put("show_classify", 1);
            params.put("show_hotword_list", 1);
            params.put("code", "94f1792ced1df89aa68a7939eaf2efca");
            Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(ALBUM_COMMENTS_KG_API);
            String commentInfoBody = SdkCommon.kgRequest(params, null, options)
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONArray commentArray = commentInfoJson.getJSONArray(hotOnly ? "weightList" : "list");
            if (JsonUtil.notEmpty(commentArray)) {
                int size = commentArray.size();
                total = hotOnly ? size : commentInfoJson.getIntValue("count");
                for (int i = hotOnly ? (page - 1) * limit : 0, len = hotOnly ? Math.min(size, page * limit) : size; i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("user_name");
                    String profileUrl = commentJson.getString("user_pic");
                    String content = commentJson.getString("content");
                    String time = TimeUtil.strToPhrase(commentJson.getString("addtime"));
                    String location = commentJson.getString("location");
                    Integer likedCount = commentJson.getJSONObject("like").getIntValue("likenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetResourceSource.KG);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLocation(location);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            }
        }

        return new CommonResult<>(res, total);
    }
}
