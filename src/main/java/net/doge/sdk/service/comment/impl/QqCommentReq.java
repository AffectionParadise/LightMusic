package net.doge.sdk.service.comment.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.*;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QqCommentReq {
    private static QqCommentReq instance;

    private QqCommentReq() {
    }

    public static QqCommentReq getInstance() {
        if (instance == null) instance = new QqCommentReq();
        return instance;
    }

    // 评论 API (QQ)
    private final String COMMENTS_QQ_API = "http://c.y.qq.com/base/fcgi-bin/fcg_global_comment_h5.fcg?biztype=%s&topid=%s&loginUin=0&cmd=%s&pagenum=%s&pagesize=%s";
    // 专辑信息 API (QQ)
    private final String ALBUM_DETAIL_QQ_API = "https://c.y.qq.com/v8/fcg-bin/musicmall.fcg?_=1689937314930&cv=4747474&ct=24&format=json" +
            "&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&cmd=get_album_buy_page" +
            "&albummid=%s&albumid=0";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total = 0;

        String id = resource.getId();
        String typeStr = null;
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        if (resource instanceof NetMusicInfo) {
            typeStr = "1";

            // QQ 需要先通过 mid 获取 id
            String songInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                    .executeAsStr();
            JSONObject trackInfo = JSONObject.parseObject(songInfoBody).getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info");
            id = trackInfo.getString("id");
        } else if (resource instanceof NetPlaylistInfo) typeStr = "3";
        else if (resource instanceof NetAlbumInfo) {
            typeStr = "2";

            // QQ 需要先通过 mid 获取 id
            String songInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, id))
                    .executeAsStr();
            id = JSONObject.parseObject(songInfoBody).getJSONObject("data").getString("album_id");
        } else if (resource instanceof NetMvInfo) typeStr = "5";
        else if (resource instanceof NetRankingInfo) typeStr = "4";

        if (StringUtil.notEmpty(typeStr)) {
            int lim = hotOnly ? limit : Math.min(25, limit);
            Map<String, int[]> cmd = new HashMap<>();
            cmd.put("1", new int[]{8, 6});
            cmd.put("2", new int[]{8, 9});
            cmd.put("3", new int[]{8, 9});
            cmd.put("4", new int[]{8, 9});
            cmd.put("5", new int[]{8, 6});
            String bizType = typeStr;
            String commentInfoBody = HttpRequest.get(String.format(COMMENTS_QQ_API, bizType, id, cmd.get(bizType)[hotOnly ? 1 : 0], page - 1, lim))
                    .executeAsStr();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject commentJson = commentInfoJson.getJSONObject("comment");
            int to = commentJson.getIntValue("commenttotal");
            total = PageUtil.totalPage(to, lim) * limit;
            JSONArray commentArray = commentJson.getJSONArray("commentlist");
            if (JsonUtil.notEmpty(commentArray)) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject cj = commentArray.getJSONObject(i);

                    String userId = cj.getString("encrypt_uin");
                    String username = cj.getString("nick");
                    String profileUrl = cj.getString("avatarurl").replaceFirst("http:", "https:");
                    JSONArray middleCommentContent = cj.getJSONArray("middlecommentcontent");
                    String content;
                    JSONObject cj2 = null;
                    if (JsonUtil.notEmpty(middleCommentContent)) {
                        cj2 = middleCommentContent.getJSONObject(0);
                        String sc = cj2.getString("subcommentcontent");
                        content = StringUtil.notEmpty(sc) ? sc.replace("\\n", "\n") : "";
                    } else {
                        String rc = cj.getString("rootcommentcontent");
                        content = StringUtil.notEmpty(rc) ? rc.replace("\\n", "\n") : "";
                    }
                    // 评论可能已被删除
                    if (StringUtil.isEmpty(content)) content = "该评论已被删除";
                    String time = TimeUtil.msToPhrase(cj.getLong("time") * 1000);
                    Integer likedCount = cj.getIntValue("praisenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.QQ);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);

                    // 被回复的评论
                    if (JsonUtil.isEmpty(middleCommentContent)) continue;
                    String uid = cj2.getString("encrypt_replyeduin");
                    String uname = cj.getString("rootcommentnick");
                    String rc = cj.getString("rootcommentcontent");
                    String cnt = StringUtil.notEmpty(rc) ? rc.replace("\\n", "\n") : "";
                    String pu = "";

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetMusicSource.QQ);
                    ci.setSub(true);
                    ci.setUserId(uid);
                    ci.setUsername(StringUtil.isEmpty(uname) ? "null" : uname.substring(1));
                    ci.setContent(StringUtil.isEmpty(cnt) ? "该评论已被删除" : cnt);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(pu);
                        ci.setProfile(profile);
                    });

                    res.add(ci);
                }
            }
        }

        return new CommonResult<>(res, total);
    }
}
