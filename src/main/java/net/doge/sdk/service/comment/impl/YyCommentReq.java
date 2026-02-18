//package net.doge.sdk.service.comment.impl;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.service.NetMusicSource;
//import net.doge.entity.service.NetCommentInfo;
//import net.doge.entity.service.NetMvInfo;
//import net.doge.entity.service.base.NetResource;
//import net.doge.sdk.common.entity.CommonResult;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.time.TimeUtil;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class YyCommentReq {
//    private static YyCommentReq instance;
//
//    private YyCommentReq() {
//    }
//
//    public static YyCommentReq getInstance() {
//        if (instance == null) instance = new YyCommentReq();
//        return instance;
//    }
//
//    // MV 评论 API (音悦台)
//    private final String MV_COMMENTS_YY_API = "https://comment-api.yinyuetai.com/comment/comment/list.json?scene=MV&sceneId=%s&level=first&sinceId=%s&size=%s";
//
//    /**
//     * 获取评论
//     */
//    public CommonResult<NetCommentInfo> getComments(NetResource resource, int page, int limit, String cursor) {
//        List<NetCommentInfo> res = new LinkedList<>();
//        int total;
//
//        String id = resource.getId();
//
//        // 加密参数未知
//        String url = String.format(MV_COMMENTS_YY_API, id, cursor, limit);
//        String commentInfoBody = HttpRequest.get(url)
//                .header("Pp", "e1540bdab81a8381849260958e345aa07be16041")
//                .header("St", "1702559399")
//                .header("Wua", "YYT/1.0.0 (WEB;web;11;zh-CN;DIgkB8jN1YCXWiwssvucj)")
//                .header("Vi", "1.0.0;11;101")
//                .executeAsStr();
//        JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
//        JSONArray commentArray = commentInfoJson.getJSONArray("data");
//        total = page * limit + 1;
//        for (int i = 0, len = commentArray.size(); i < len; i++) {
//            JSONObject commentJson = commentArray.getJSONObject(i);
//
//            String userId = commentJson.getString("userId");
//            String username = commentJson.getString("nickname");
//            String content = commentJson.getString("content");
//            String time = TimeUtil.msToPhrase(commentJson.getLong("createTime") * 1000);
//            Integer likedCount = commentJson.getIntValue("favorites");
//
//            NetCommentInfo commentInfo = new NetCommentInfo();
//            commentInfo.setSource(NetMusicSource.YY);
//            commentInfo.setUserId(userId);
//            commentInfo.setUsername(username);
//            commentInfo.setContent(content);
//            commentInfo.setTime(time);
//            commentInfo.setLikedCount(likedCount);
//
//            res.add(commentInfo);
//        }
//
//        return new CommonResult<>(res, total, cursor);
//    }
//}
