package net.doge.sdk.service.comment.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LzCommentReq {
    private static LzCommentReq instance;

    private LzCommentReq() {
    }

    public static LzCommentReq getInstance() {
        if (instance == null) instance = new LzCommentReq();
        return instance;
    }

    // 评论 API (李志)
    private final String COMMENTS_LZ_API = "https://www.lizhinb.com/wp-admin/admin-ajax.php";
    // 专辑信息 API (李志)
    private final String ALBUM_DETAIL_LZ_API = "https://www.lizhinb.com/%s/";
    // 视频信息 API (李志)
    private final String VIDEO_DETAIL_LZ_API = "https://www.lizhinb.com/live/%s/";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, int page, int limit, String cursor) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = null;
        boolean isVideo = false;
        if (resource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            id = musicInfo.getId();
        } else if (resource instanceof NetAlbumInfo) {
            NetAlbumInfo albumInfo = (NetAlbumInfo) resource;
            id = albumInfo.getId();
        } else if (resource instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            id = mvInfo.getId();
            isVideo = true;
        }

        // 专辑和歌曲使用同一参数
        // 获取 post-id
        String albumInfoBody = HttpRequest.get(String.format(isVideo ? VIDEO_DETAIL_LZ_API : ALBUM_DETAIL_LZ_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements ct = doc.select(".comments.tabbar");
        String pid = ct.attr("data-post_id");

        Map<String, Object> formMap = new HashMap<>();
        formMap.put("action", "zaxu_ajax_comment_rc");
        formMap.put("post_id", pid);
        formMap.put("page", page);
        String commentInfoBody = HttpRequest.post(COMMENTS_LZ_API)
                .form(formMap)
                .executeAsStr();
        doc = Jsoup.parse(commentInfoBody);
        Elements commentArray = doc.select("#comments-list li");
        // 获取评论数量
        total = limit;
//            formMap.remove("page");
//            formMap.put("action", "zaxu_ajax_get_comment_count_rc");
//            String commentCountBody = HttpRequest.post(COMMENTS_LZ_API)
//                    .form(formMap)
//                    .executeAsync()
//                    .body();
//            total = Integer.parseInt(commentCountBody);
        for (int i = 0, len = commentArray.size(); i < len; i++) {
            Element comment = commentArray.get(i);
            Element title = comment.select(".comment-title").first();
            Element img = comment.select(".comment-avatar img").first();
            Element p = comment.select(".comment-text p").first();
            Element date = comment.select(".comment-date").first();

            String username = title.text().trim();
            String profileUrl = img.attr("srcset").split(" ")[0];
            if (StringUtil.isEmpty(profileUrl)) profileUrl = img.attr("src");
            String content = p.text().trim();
            String time = TimeUtil.strToPhrase(date.text().trim());

            NetCommentInfo commentInfo = new NetCommentInfo();
            commentInfo.setSource(NetMusicSource.LZ);
            commentInfo.setSub(!comment.hasClass("depth-1"));
            commentInfo.setUsername(username);
            commentInfo.setProfileUrl(profileUrl);
            commentInfo.setContent(content);
            commentInfo.setTime(time);

            String finalProfileUrl = profileUrl;
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage profile = SdkUtil.extractProfile(finalProfileUrl);
                commentInfo.setProfile(profile);
            });

            res.add(commentInfo);
        }

        return new CommonResult<>(res, total, cursor);
    }
}
