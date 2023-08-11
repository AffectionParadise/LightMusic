package net.doge.sdk.entity.comment;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.*;
import net.doge.model.entity.base.NetResource;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.BvAvConverter;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommentReq {
    // 获取评论 API
    private final String GET_COMMENTS_API = "https://music.163.com/api/v2/resource/comments";
    // 获取评论 API (酷狗)
    private final String GET_COMMENTS_KG_API
            = "https://mcomment.kugou.com/index.php?r=commentsv2/getCommentWithLike&code=fc4be23b4e972707f36b8a828a93ba8a&extdata=%s&p=%s&pagesize=%s";
    // 获取评论 API (QQ)
    private final String GET_COMMENTS_QQ_API = "http://c.y.qq.com/base/fcgi-bin/fcg_global_comment_h5.fcg?biztype=%s&topid=%s&loginUin=0&cmd=%s&pagenum=%s&pagesize=%s";
    // 获取热门评论 API (酷我)
    private final String GET_HOT_COMMENTS_KW_API = "http://www.kuwo.cn/comment?digest=%s&sid=%s&&type=get_rec_comment&f=web&page=%s&rows=%s&uid=0&prod=newWeb&httpsStatus=1";
    // 获取最新评论 API (酷我)
    private final String GET_NEW_COMMENTS_KW_API = "http://www.kuwo.cn/comment?digest=%s&sid=%s&&type=get_comment&f=web&page=%s&rows=%s&uid=0&prod=newWeb&httpsStatus=1";
    // 获取热门评论 API (咪咕)
    private final String GET_HOT_COMMENTS_MG_API = "https://music.migu.cn/v3/api/comment/listTopComments?targetId=%s&pageNo=%s&pageSize=%s";
    // 获取最新评论 API (咪咕)
    private final String GET_NEW_COMMENTS_MG_API = "https://music.migu.cn/v3/api/comment/listComments?targetId=%s&pageNo=%s&pageSize=%s";
    // 获取电台热门评论 API (喜马拉雅)
    private final String GET_RADIO_HOT_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=content-score-desc&pageId=%s&pageSize=%s";
    // 获取电台最新评论 API (喜马拉雅)
    private final String GET_RADIO_NEW_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=time-desc&pageId=%s&pageSize=%s";
    // 获取节目评论 API (喜马拉雅)
    private final String GET_COMMENTS_XM_API = "https://www.ximalaya.com/revision/comment/queryComments?trackId=%s&page=%s&pageSize=%s";
    // 获取评论 API (音乐磁场)
    private final String GET_COMMENTS_HF_API = "https://www.hifini.com/thread-%s-%s.htm?sort=desc";
    // 获取评论 API (咕咕咕音乐)
    private final String GET_COMMENTS_GG_API = "http://www.gggmusic.com/thread-%s-%s.htm?sort=desc";
    // 获取评论 API (5sing)
    private final String GET_COMMENTS_FS_API = "http://service.5sing.kugou.com/%s/comments/list1?rootId=%s&page=%s&limit=%s";
    // 获取 MV 评论 API (5sing)
    private final String GET_MV_COMMENTS_FS_API = "http://service.5sing.kugou.com/mv/CommentList?mvId=%s&page=%s";
    // 获取节目评论 API (猫耳)
    private final String GET_COMMENTS_ME_API = "https://www.missevan.com/site/getcomment?type=%s&order=%s&eId=%s&p=%s&pagesize=%s";
    // 获取评论 API (好看)
    private final String GET_COMMENTS_HK_API = "https://haokan.baidu.com/videoui/api/commentget?url_key=%s&pn=%s&rn=%s&child_rn=1";
    // 获取电台评论 API (豆瓣)
    private final String GET_RADIO_COMMENTS_DB_API = "https://movie.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 获取图书电台评论 API (豆瓣)
    private final String GET_BOOK_RADIO_COMMENTS_DB_API = "https://book.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 获取游戏电台评论 API (豆瓣)
    private final String GET_GAME_RADIO_COMMENTS_DB_API = "https://www.douban.com/game/%s/comments?sort=%s&start=%s";
    // 获取专辑评论 API (豆瓣)
    private final String GET_ALBUM_COMMENTS_DB_API = "https://music.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 获取视频评论 API (哔哩哔哩)
    private final String GET_VIDEO_COMMENTS_BI_API = "https://api.bilibili.com/x/v2/reply?type=1&oid=%s&sort=%s&pn=%s&ps=%s";
    // 获取音频评论 API (哔哩哔哩)
    private final String GET_SONG_COMMENTS_BI_API = "https://api.bilibili.com/x/v2/reply?type=14&oid=%s&sort=%s&pn=%s&ps=%s";

    // mlog id 转视频 id API
    private final String MLOG_TO_VIDEO_API = "https://music.163.com/weapi/mlog/video/convert/id";
    // 专辑信息 API (QQ)
    private final String ALBUM_DETAIL_QQ_API = "https://c.y.qq.com/v8/fcg-bin/musicmall.fcg?_=1689937314930&cv=4747474&ct=24&format=json" +
            "&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&cmd=get_album_buy_page" +
            "&albummid=%s&albumid=0";
    // 歌曲信息 API (咪咕)
    private final String SINGLE_SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";

    /**
     * 获取 歌曲 / 歌单 / 专辑 / MV 评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int limit, int page, String cursor) {
        int total = 0;
        List<NetCommentInfo> res = new LinkedList<>();

        String id = null;
        String[] typeStr = null;
        Integer source = 0;
        boolean hotOnly = "热门评论".equals(type);

        boolean isRadio = false, isBook = false, isGame = false;

        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            // 网易云需要先判断是普通歌曲还是电台节目，酷狗歌曲获取评论需要 hash
            boolean hasProgramId = musicInfo.hasProgramId();
            boolean hasHash = musicInfo.hasHash();
            id = hasProgramId ? musicInfo.getProgramId() : hasHash ? musicInfo.getHash() : musicInfo.getId();
            source = musicInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{hasProgramId ? "A_DJ_1_" : "R_SO_4_", "1", "15", "1"};

            if (source == NetMusicSource.QQ) {
                // QQ 需要先通过 mid 获取 id
                String songInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                        .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                        .executeAsync()
                        .body();
                JSONObject trackInfo = JSONObject.parseObject(songInfoBody).getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info");
                id = trackInfo.getString("id");
            }
        } else if (resource instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) resource;
            id = playlistInfo.getId();
            source = playlistInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{"A_PL_0_", "3", "8", "2"};
        } else if (resource instanceof NetAlbumInfo) {
            NetAlbumInfo albumInfo = (NetAlbumInfo) resource;
            id = albumInfo.getId();
            source = albumInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{"R_AL_3_", "2", "", ""};

            if (source == NetMusicSource.QQ) {
                // QQ 需要先通过 mid 获取 id
                String songInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, id))
                        .executeAsync()
                        .body();
                id = JSONObject.parseObject(songInfoBody).getJSONObject("data").getString("album_id");
            }
        } else if (resource instanceof NetRadioInfo) {
            NetRadioInfo radioInfo = (NetRadioInfo) resource;
            id = radioInfo.getId();
            source = radioInfo.getSource();
            isRadio = true;
            isBook = radioInfo.isBook();
            isGame = radioInfo.isGame();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{"A_DR_14_", "", "", ""};
        } else if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            // 网易云需要判断是视频还是 MV 还是 Mlog
            boolean isVideo = mvInfo.isVideo();
            boolean isMlog = mvInfo.isMlog();

            source = mvInfo.getSource();
            // 哔哩哔哩获取视频的 bvid
            id = source == NetMusicSource.BI ? mvInfo.getBvid() : mvInfo.getId();

            // Mlog 需要先获取视频 id，并转为视频类型
            if (isMlog) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
                String body = SdkCommon.ncRequest(Method.POST, MLOG_TO_VIDEO_API, String.format("{\"mlogId\":\"%s\"}", id), options)
                        .executeAsync()
                        .body();
                id = JSONObject.parseObject(body).getString("data");
                mvInfo.setId(id);
                mvInfo.setType(MvInfoType.VIDEO);
            }
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{isVideo || isMlog ? "R_VI_62_" : "R_MV_5_", "5", "7"};
        } else if (resource instanceof NetRankingInfo) {
            NetRankingInfo rankingInfo = (NetRankingInfo) resource;
            id = rankingInfo.getId();
            source = rankingInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{"A_PL_0_", "4", "2", ""};
        }

        // 网易云
        if (source == NetMusicSource.NC && StringUtil.notEmpty(typeStr[0])) {
            String threadId = typeStr[0] + id;
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
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eApi("/api/v2/resource/comments");
            String commentInfoBody = SdkCommon.ncRequest(Method.POST, GET_COMMENTS_API,
                            String.format("{\"threadId\":\"%s\",\"showInner\":true,\"pageNo\":%s,\"pageSize\":%s,\"cursor\":\"%s\",\"sortType\":%s}",
                                    threadId, page, limit, cur, sortType), options)
                    .executeAsync()
                    .body();
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
                String content = commentJson.getString("content");
                String time = TimeUtil.msToPhrase(commentJson.getLong("time"));
                Integer likedCount = commentJson.getIntValue("likedCount");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
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

                    commentInfo = new NetCommentInfo();
                    commentInfo.setSub(true);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);

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

        // 酷狗
        else if (source == NetMusicSource.KG && resource instanceof NetMusicInfo) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_KG_API, id, page, limit))
                    // 注意此处必须加 header 才能请求到正确的数据！
                    .header(Header.USER_AGENT, "Android712-AndroidPhone-8983-18-0-COMMENT-wifi")
                    .executeAsync()
                    .body();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONArray commentArray = hotOnly ? commentInfoJson.getJSONArray("weightList") : commentInfoJson.getJSONArray("list");
            total = hotOnly ? (JsonUtil.notEmpty(commentArray) ? commentArray.size() : 0) : commentInfoJson.getIntValue("count");
            if (JsonUtil.notEmpty(commentArray)) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("user_name");
                    String profileUrl = commentJson.getString("user_pic");
                    String content = commentJson.getString("content");
                    String time = TimeUtil.strToPhrase(commentJson.getString("addtime"));
                    Integer likedCount = commentJson.getJSONObject("like").getIntValue("likenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.KG);
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
                }
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ && StringUtil.notEmpty(typeStr[1])) {
            int lim = hotOnly ? limit : Math.min(25, limit);
            Map<String, int[]> cmd = new HashMap<>();
            cmd.put("1", new int[]{8, 6});
            cmd.put("2", new int[]{8, 9});
            cmd.put("3", new int[]{8, 9});
            cmd.put("4", new int[]{8, 9});
            cmd.put("5", new int[]{8, 6});
            String bizType = typeStr[1];
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_QQ_API, bizType, id, cmd.get(bizType)[hotOnly ? 1 : 0], page - 1, lim))
                    .executeAsync()
                    .body();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject commentJson = commentInfoJson.getJSONObject("comment");
            int to = commentJson.getIntValue("commenttotal");
            total = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
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

        // 酷我
        else if (source == NetMusicSource.KW && StringUtil.notEmpty(typeStr[2])) {
            String ref = "";
            switch (Integer.parseInt(typeStr[2])) {
                case 15:
                    ref = "http://www.kuwo.cn/play_detail/" + StringUtil.urlEncodeAll(id);
                    break;
                case 7:
                    ref = "http://www.kuwo.cn/mvplay/" + StringUtil.urlEncodeAll(id);
                    break;
                case 8:
                    ref = "http://www.kuwo.cn/playlist_detail/" + StringUtil.urlEncodeAll(id);
                    break;
                case 2:
                    ref = "http://www.kuwo.cn/rankList/" + StringUtil.urlEncodeAll(id);
                    break;
            }
            String url = hotOnly ? GET_HOT_COMMENTS_KW_API : GET_NEW_COMMENTS_KW_API;
            // 最新评论
            String commentInfoBody = SdkCommon.kwRequest(String.format(url, typeStr[2], id, page, limit))
                    .header(Header.REFERER, ref)
                    .executeAsync()
                    .body();
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

        // 咪咕
        else if (source == NetMusicSource.MG && resource instanceof NetMusicInfo) {
            // 先根据 cid 获取 songId
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, id))
                    .executeAsync()
                    .body();
            id = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0).getString("songId");

            // 评论
            String url = hotOnly ? GET_HOT_COMMENTS_MG_API : GET_NEW_COMMENTS_MG_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, page, limit))
                    .header(Header.REFERER, "https://music.migu.cn")
                    .executeAsync()
                    .body();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                total = data.getIntValue("itemTotal");
                JSONArray commentArray = data.getJSONArray("items");
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);
                    JSONObject author = commentJson.getJSONObject("author");

                    String userId = author.getString("id");
                    String username = author.getString("name");
                    String profileUrl = "https:" + author.getString("avatar");
                    String content = commentJson.getString("body");
                    String time = TimeUtil.strToPhrase(commentJson.getString("createTime"));
                    Integer likedCount = commentJson.getIntValue("praiseCount");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.MG);
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
                    JSONArray replies = commentJson.getJSONArray("replyCommentList");
                    for (int j = 0, s = replies.size(); j < s; j++) {
                        JSONObject reply = replies.getJSONObject(j);
                        author = reply.getJSONObject("author");

                        userId = author.getString("id");
                        username = author.getString("name");
                        profileUrl = "https:" + author.getString("avatar");
                        content = reply.getString("body");
                        time = TimeUtil.strToPhrase(reply.getString("createTime"));
                        likedCount = reply.getIntValue("praiseCount");

                        NetCommentInfo rCommentInfo = new NetCommentInfo();
                        rCommentInfo.setSource(NetMusicSource.MG);
                        rCommentInfo.setSub(true);
                        rCommentInfo.setUserId(userId);
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
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            JSONArray commentArray;
            if (isRadio) {
                String url = hotOnly ? GET_RADIO_HOT_COMMENTS_XM_API : GET_RADIO_NEW_COMMENTS_XM_API;
                String commentInfoBody = HttpRequest.get(String.format(url, id, page, limit))
                        .executeAsync()
                        .body();
                JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
                JSONObject data = commentInfoJson.getJSONObject("data");
                JSONObject comments = data.getJSONObject("comments");
                total = comments.getIntValue("totalCount");
                commentArray = comments.getJSONArray("list");
            } else {
                String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_XM_API, id, page, limit))
                        .executeAsync()
                        .body();
                JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
                JSONObject data = commentInfoJson.getJSONObject("data");
                total = data.getIntValue("totalComment");
                commentArray = data.getJSONArray("comments");
            }
            if (JsonUtil.notEmpty(commentArray)) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String userId = commentJson.getString("uid");
                    String username = commentJson.getString("nickname");
                    String smallHeader = commentJson.getString("smallHeader");
                    String profileUrl = isRadio ? smallHeader.replaceFirst("http:", "https:") : "https:" + smallHeader;
                    String content = commentJson.getString("content");
                    String time = TimeUtil.msToPhrase(commentJson.getLong(isRadio ? "createdAt" : "commentTime"));
                    Integer likedCount = commentJson.getIntValue("likes");
                    Integer score = commentJson.getIntValue("newAlbumScore", -1);

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.XM);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    commentInfo.setScore(score);

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

                        userId = cj.getString("uid");
                        username = cj.getString("nickname");
                        smallHeader = cj.getString("smallHeader");
                        profileUrl = isRadio ? smallHeader.replaceFirst("http:", "https:") : "https:" + smallHeader;
                        content = cj.getString("content");
                        time = isRadio ? TimeUtil.msToPhrase(cj.getLong("createdAt")) : cj.getString("createAt");
                        likedCount = cj.getIntValue("likes");
                        score = cj.getIntValue("newAlbumScore", -1);

                        NetCommentInfo ci = new NetCommentInfo();
                        ci.setSource(NetMusicSource.XM);
                        ci.setSub(true);
                        ci.setUserId(userId);
                        ci.setUsername(username);
                        ci.setProfileUrl(profileUrl);
                        ci.setContent(content);
                        ci.setTime(time);
                        ci.setLikedCount(likedCount);
                        ci.setScore(score);

                        String finalProfileUrl = profileUrl;
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                            ci.setProfile(profile);
                        });

                        res.add(ci);
                    }
                }
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_HF_API, id, page))
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(commentInfoBody);
            Elements comments = doc.select("li.media.post");
            Elements ap = doc.select("a.page-link");
            String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
            if (StringUtil.isEmpty(ts))
                ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
            boolean hasTs = StringUtil.notEmpty(ts);
            if (hasTs) total = Integer.parseInt(ts) * limit;
            else total = comments.size();
            for (int i = 0, len = comments.size(); i < len; i++) {
                Element comment = comments.get(i);

                Element msg = comment.select(".message.mt-1.break-all").first();
                if (msg == null) continue;

                String username = comment.select(".username").text();
                String userId = RegexUtil.getGroup1("user-(\\d+)\\.htm", comment.select(".username a").attr("href"));
                String profileUrl = "https://www.hifini.com/" + comment.select("img").attr("src");
                String content = msg.text();
                if (StringUtil.isEmpty(content)) content = msg.text().trim();
                String time = TimeUtil.strToPhrase(comment.select(".date.text-grey.ml-2").text());

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.HF);
                commentInfo.setUsername(username);
                commentInfo.setUserId(userId);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                String finalProfileUrl1 = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                    commentInfo.setProfile(profile);
                });

                res.add(commentInfo);

                // 被回复的评论
                Elements bq = msg.select("blockquote");
                if (bq.isEmpty()) continue;

                username = bq.select("a").text().trim();
                userId = RegexUtil.getGroup1("user-(\\d+)\\.htm", comment.select("a").attr("href"));
                profileUrl = "https://www.hifini.com/" + bq.select("img").attr("src");
                content = bq.first().ownText();

                NetCommentInfo ci = new NetCommentInfo();
                ci.setSource(NetMusicSource.HF);
                ci.setSub(true);
                ci.setUsername(username);
                ci.setUserId(userId);
                ci.setProfileUrl(profileUrl);
                ci.setContent(content);
                String finalProfileUrl = profileUrl;

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                    ci.setProfile(profile);
                });

                res.add(ci);
            }
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_GG_API, id, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(commentInfoBody);
            Elements comments = doc.select("li.media.post");
            Elements ap = doc.select("a.page-link");
            String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
            if (StringUtil.isEmpty(ts))
                ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
            boolean hasTs = StringUtil.notEmpty(ts);
            if (hasTs) total = Integer.parseInt(ts) * limit;
            else total = comments.size();
            for (int i = 0, len = comments.size(); i < len; i++) {
                Element comment = comments.get(i);

                Element msg = comment.select(".message.mt-1.break-all").first();
                if (msg == null) continue;

                String username = comment.select(".username").text();
                String userId = RegexUtil.getGroup1("user-(\\d+)\\.htm", comment.select(".username a").attr("href"));
                String profileUrl = "http://www.gggmusic.com/" + comment.select("img").attr("src");
                String content = msg.text();
                String time = TimeUtil.strToPhrase(comment.select(".date.text-grey.ml-2").text());

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.GG);
                commentInfo.setUsername(username);
                commentInfo.setUserId(userId);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                String finalProfileUrl1 = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl1);
                    commentInfo.setProfile(profile);
                });

                res.add(commentInfo);

                // 被回复的评论
                Elements bq = msg.select("blockquote");
                if (bq.isEmpty()) continue;

                username = bq.select("a").text().trim();
                userId = RegexUtil.getGroup1("user-(\\d+)\\.htm", comment.select("a").attr("href"));
                profileUrl = "http://www.gggmusic.com/" + bq.select("img").attr("src");
                content = bq.first().ownText();

                NetCommentInfo ci = new NetCommentInfo();
                ci.setSource(NetMusicSource.GG);
                ci.setSub(true);
                ci.setUsername(username);
                ci.setUserId(userId);
                ci.setProfileUrl(profileUrl);
                ci.setContent(content);
                String finalProfileUrl = profileUrl;

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                    ci.setProfile(profile);
                });

                res.add(ci);
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            boolean isMv = resource instanceof NetMvInfo;
            String url = "";
            if (resource instanceof NetMusicInfo) {
                String[] sp = id.split("_");
                url = String.format(GET_COMMENTS_FS_API, sp[0], sp[1], page, limit);
            } else if (resource instanceof NetPlaylistInfo) {
                url = String.format(GET_COMMENTS_FS_API, "dynamicSongList", id, page, limit);
            } else if (isMv) {
                url = String.format(GET_MV_COMMENTS_FS_API, id, page);
            }
            String commentInfoBody = HttpRequest.get(url)
                    .executeAsync()
                    .body();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            JSONArray commentArray = data.getJSONArray(hotOnly && isMv ? "hotList" : "comments");
            if (isMv) {
                if (hotOnly) total = commentArray.size();
                else {
                    int count = data.getIntValue("count"), lim = 10;
                    total = (count % lim == 0 ? count / lim : count / lim + 1) * limit;
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
        }

        // 猫耳
        else if (source == NetMusicSource.ME && StringUtil.notEmpty(typeStr[3])) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_ME_API, typeStr[3], hotOnly ? 3 : 1, id, page, limit))
                    .executeAsync()
                    .body();
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
                commentInfo.setSource(NetMusicSource.ME);
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
                    ci.setSource(NetMusicSource.ME);
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

        // 好看
        else if (source == NetMusicSource.HK) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_HK_API, id, page, limit))
                    .executeAsync()
                    .body();
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            total = data.getIntValue("comment_count");
            JSONArray commentArray = data.getJSONArray("list");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);

                String userId = commentJson.getString("third_id");
                String username = commentJson.getString("uname");
                String profileUrl = commentJson.getString("avatar");
                String content = commentJson.getString("content");
                String time = TimeUtil.msToPhrase(commentJson.getLong("create_time") * 1000);
                Integer likedCount = commentJson.getIntValue("like_count");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.HK);
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
                JSONArray replies = commentJson.getJSONArray("reply_list");
                for (int j = 0, s = replies.size(); j < s; j++) {
                    JSONObject cj = replies.getJSONObject(j);

                    userId = cj.getString("third_id");
                    username = cj.getString("uname");
                    profileUrl = cj.getString("avatar");
                    content = cj.getString("content");
                    time = TimeUtil.msToPhrase(cj.getLong("create_time") * 1000);
                    likedCount = cj.getIntValue("like_count");

                    NetCommentInfo ci = new NetCommentInfo();
                    ci.setSource(NetMusicSource.HK);
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

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            if (isGame) {
                String url = GET_GAME_RADIO_COMMENTS_DB_API;
                String commentInfoBody = HttpRequest.get(String.format(url, id, hotOnly ? "score" : "time", (page - 1) * limit, limit))
                        .setFollowRedirects(true)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(commentInfoBody);
                Elements comments = doc.select("li.comment-item");
                String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("div#content h1").text());
                total = StringUtil.notEmpty(ts) ? Integer.parseInt(ts) : comments.size();
                for (int i = 0, len = comments.size(); i < len; i++) {
                    Element comment = comments.get(i);
                    Element a = comment.select("div.user-info a").first();
                    Element sht = comment.select("span.short").first();
                    Element t = comment.select("span.pubtime").first();
                    Element d = comment.select("span.digg span").first();
                    Elements rating = comment.select("div.user-info span");

                    String userId = RegexUtil.getGroup1("/people/(.*?)/", a.attr("href"));
                    String username = a.text();
                    String content = sht.text();
                    String time = TimeUtil.strToPhrase(t.text().replaceAll("年|月", "-").replace("日", ""));
                    Integer likedCount = Integer.parseInt(d.text());
                    String r = RegexUtil.getGroup1("allstar(\\d+)", rating.size() > 2 ? rating.get(2).className() : "");
                    Integer score = StringUtil.isEmpty(r) ? -1 : Integer.parseInt(r) / 10 * 2;
                    String profileUrl = "";

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.DB);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    commentInfo.setScore(score);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            } else {
                String url;
                if (isRadio) {
                    if (isBook) url = GET_BOOK_RADIO_COMMENTS_DB_API;
                    else url = GET_RADIO_COMMENTS_DB_API;
                } else url = GET_ALBUM_COMMENTS_DB_API;
                String commentInfoBody = HttpRequest.get(String.format(url, id, hotOnly ? "new_score" : "time", (page - 1) * limit, limit))
                        .setFollowRedirects(true)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(commentInfoBody);
                Elements comments = doc.select(isRadio && !isBook ? "div.comment-item" : "li.comment-item");
                String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("li.is-active").text());
                total = StringUtil.notEmpty(ts) ? Integer.parseInt(ts) : comments.size();
                for (int i = 0, len = comments.size(); i < len; i++) {
                    Element comment = comments.get(i);
                    Element a = comment.select("span.comment-info a").first();
                    Element img = comment.select("div.avatar img").first();
                    Element cnt = comment.select("p.comment-content").first();
                    Element t = comment.select(isBook ? "a.comment-time" : "span.comment-time").first();
                    Element v = comment.select("span.vote-count").first();
                    Element rating = comment.select("span.comment-info span").get(isRadio && !isBook ? 1 : 0);

                    String userId = RegexUtil.getGroup1("/people/(.*?)/", a.attr("href"));
                    String username = a.text();
                    String src = img.attr("src");
                    String profileUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst(isRadio ? "/u" : "/up", "/ul");
                    String content = cnt.text();
                    String time = TimeUtil.strToPhrase(t.text().trim());
                    Integer likedCount = Integer.parseInt(v.text());
                    String r = RegexUtil.getGroup1("(\\d+) ", rating.className());
                    Integer score = StringUtil.isEmpty(r) ? -1 : Integer.parseInt(r) / 10 * 2;

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.DB);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    commentInfo.setScore(score);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = SdkUtil.extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    res.add(commentInfo);
                }
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            int lim = Math.min(20, limit);
            String url = resource instanceof NetMvInfo ? String.format(GET_VIDEO_COMMENTS_BI_API, BvAvConverter.convertBv2Av(id), hotOnly ? 1 : 0, page, lim)
                    : String.format(GET_SONG_COMMENTS_BI_API, id, hotOnly ? 1 : 0, page, lim);
            String commentInfoBody = HttpRequest.get(url)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
//                    // 貌似解析不了，触及到什么特殊字符了？
//                    .replaceAll("\"\\[\\d+.*?\\]\"", "\"\"");
            JSONObject commentInfoJson = JSONObject.parseObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            int count = data.getJSONObject("page").getIntValue("count");
            total = (count % lim == 0 ? count / lim : count / lim + 1) * limit;
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
                    commentInfo.setSource(NetMusicSource.BI);
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
                        ci.setSource(NetMusicSource.BI);
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
        }

        return new CommonResult<>(res, total, cursor);
    }
}
