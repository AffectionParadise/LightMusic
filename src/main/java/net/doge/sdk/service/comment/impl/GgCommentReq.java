package net.doge.sdk.service.comment.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class GgCommentReq {
    private static GgCommentReq instance;

    private GgCommentReq() {
    }

    public static GgCommentReq getInstance() {
        if (instance == null) instance = new GgCommentReq();
        return instance;
    }

    // 评论 API (咕咕咕音乐)
    private final String COMMENTS_GG_API = "http://www.gggmusic.com/thread-%s-%s.htm?sort=desc";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = resource.getId();

        String commentInfoBody = HttpRequest.get(String.format(COMMENTS_GG_API, id, page))
                .executeAsStr();
        Document doc = Jsoup.parse(commentInfoBody);
        Elements comments = doc.select("li.media.post");
        Elements ap = doc.select("a.page-link");
        String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
        if (StringUtil.isEmpty(ts))
            ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
        boolean hasTs = StringUtil.notEmpty(ts);
        if (hasTs) total = Integer.parseInt(ts) * limit;
        else total = limit;
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

        return new CommonResult<>(res, total);
    }
}
