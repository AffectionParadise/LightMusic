package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
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

    // 评论 API (酷狗)
//    private final String COMMENTS_KG_API = "https://mcomment.kugou.com/index.php?r=commentsv2/getCommentWithLike&code=fc4be23b4e972707f36b8a828a93ba8a&extdata=%s&p=%s&pagesize=%s";
//    private final String AUDIO_INFO_KG_API = "/v3/album_audio/audio";
    private final String HOT_COMMENTS_KG_API = "http://m.comment.service.kugou.com/r/v1/rank/topliked";
    private final String NEW_COMMENTS_KG_API = "http://m.comment.service.kugou.com/r/v1/rank/newest";

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
        }

        if (resource instanceof NetMusicInfo) {
//            String commentInfoBody = HttpRequest.get(String.format(COMMENTS_KG_API, id, page, limit))
//                    // 注意此处必须加 header 才能请求到正确的数据！
//                    .header(Header.USER_AGENT, "Android712-AndroidPhone-8983-18-0-COMMENT-wifi")
//                    .executeAsync()
//                    .body();
//            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
//            JSONArray commentArray = hotOnly ? commentInfoJson.getJSONArray("weightList") : commentInfoJson.getJSONArray("list");
//            total = hotOnly ? (JsonUtil.notEmpty(commentArray) ? commentArray.size() : 0) : commentInfoJson.getIntValue("count");
//            if (JsonUtil.notEmpty(commentArray)) {
//                for (int i = 0, len = commentArray.size(); i < len; i++) {
//                    JSONObject commentJson = commentArray.getJSONObject(i);
//
//                    String username = commentJson.getString("user_name");
//                    String profileUrl = commentJson.getString("user_pic");
//                    String content = commentJson.getString("content");
//                    String time = TimeUtil.strToPhrase(commentJson.getString("addtime"));
//                    String location = commentJson.getString("location");
//                    Integer likedCount = commentJson.getJSONObject("like").getIntValue("likenum");
//
//                    NetCommentInfo commentInfo = new NetCommentInfo();
//                    commentInfo.setSource(NetMusicSource.KG);
//                    commentInfo.setUsername(username);
//                    commentInfo.setProfileUrl(profileUrl);
//                    commentInfo.setContent(content);
//                    commentInfo.setTime(time);
//                    commentInfo.setLocation(location);
//                    commentInfo.setLikedCount(likedCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
//                        commentInfo.setProfile(profile);
//                    });
//
//                    res.add(commentInfo);
//                }
//            }

//            // 获取 mixsongid
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(AUDIO_INFO_KG_API);
//            String ct = String.valueOf(System.currentTimeMillis() / 1000);
//            String dat = String.format("{\"area_code\":\"1\",\"show_privilege\":1,\"show_album_info\":\"1\",\"is_publish\":\"\"," +
//                            "\"clientver\":\"%s\",\"mid\":\"%s\",\"dfid\":\"-\",\"clienttime\":\"%s\",\"key\":\"%s\",\"appid\":\"%s\",\"data\":[{\"hash\":\"%s\"}]}",
//                    KugouReqBuilder.clientver, KugouReqBuilder.mid, ct, KugouReqBuilder.androidSignKey, KugouReqBuilder.appid, id);
//            String audioInfoBody = SdkCommon.kgRequest(null, dat, options)
//                    .header(Header.USER_AGENT, "Android712-AndroidPhone-11451-376-0-FeeCacheUpdate-wifi")
//                    .header("KG-THash", "13a3164")
//                    .header("KG-RC", "1")
//                    .header("KG-Fake", "0")
//                    .header("KG-RF", "00869891")
//                    .header("x-router", "kmr.service.kugou.com")
//                    .executeAsync()
//                    .body();
//            JSONObject audioInfoJson = JSONObject.parseObject(audioInfoBody);
//            JSONObject audioData = audioInfoJson.getJSONArray("data").getJSONArray(0).getJSONObject(0);
//            String mixSongId = audioData.getString("album_audio_id");

            // 获取评论
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
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(hotOnly ? HOT_COMMENTS_KG_API : NEW_COMMENTS_KG_API);
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
                    commentInfo.setSource(NetMusicSource.KG);
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
