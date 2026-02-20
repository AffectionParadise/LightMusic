package net.doge.sdk.service.comment.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.NetRadioInfo;
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

public class DbCommentReq {
    private static DbCommentReq instance;

    private DbCommentReq() {
    }

    public static DbCommentReq getInstance() {
        if (instance == null) instance = new DbCommentReq();
        return instance;
    }

    // 电台评论 API (豆瓣)
    private final String RADIO_COMMENTS_DB_API = "https://movie.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 图书电台评论 API (豆瓣)
    private final String BOOK_RADIO_COMMENTS_DB_API = "https://book.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 游戏电台评论 API (豆瓣)
    private final String GAME_RADIO_COMMENTS_DB_API = "https://www.douban.com/game/%s/comments?sort=%s&start=%s";
    // 专辑评论 API (豆瓣)
    private final String ALBUM_COMMENTS_DB_API = "https://music.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit) {
        List<NetCommentInfo> res = new LinkedList<>();
        int total;

        String id = resource.getId();
        boolean hotOnly = I18n.getText("hotComment").equals(type);
        boolean isRadio = false, isBook = false, isGame = false;
        if (resource instanceof NetRadioInfo) {
            NetRadioInfo radioInfo = (NetRadioInfo) resource;
            isRadio = true;
            isBook = radioInfo.isBook();
            isGame = radioInfo.isGame();
        }

        if (isGame) {
            String url = GAME_RADIO_COMMENTS_DB_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, hotOnly ? "score" : "time", (page - 1) * limit, limit))
                    .executeAsStr();
            Document doc = Jsoup.parse(commentInfoBody);
            Elements comments = doc.select("li.comment-item");
            String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#content h1").text());
            total = StringUtil.notEmpty(ts) ? Integer.parseInt(ts) : comments.size();
            for (int i = 0, len = comments.size(); i < len; i++) {
                Element comment = comments.get(i);
                Element a = comment.select(".user-info a").first();
                Element sht = comment.select("span.short").first();
                Element t = comment.select("span.pubtime").first();
                Element d = comment.select("span.digg span").first();
                Elements rating = comment.select(".user-info span");

                String userId = RegexUtil.getGroup1("/people/(.*?)/", a.attr("href"));
                String username = a.text();
                String content = sht.text();
                String time = TimeUtil.strToPhrase(t.text().replaceAll("年|月", "-").replace("日", ""));
                Integer likedCount = Integer.parseInt(d.text());
                String r = RegexUtil.getGroup1("allstar(\\d+)", rating.size() > 2 ? rating.get(2).className() : "");
                Integer score = StringUtil.isEmpty(r) ? -1 : Integer.parseInt(r) / 10 * 2;
                String profileUrl = "";

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetResourceSource.DB);
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
                if (isBook) url = BOOK_RADIO_COMMENTS_DB_API;
                else url = RADIO_COMMENTS_DB_API;
            } else url = ALBUM_COMMENTS_DB_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, hotOnly ? "new_score" : "time", (page - 1) * limit, limit))
                    .executeAsStr();
            Document doc = Jsoup.parse(commentInfoBody);
            Elements comments = doc.select(isRadio && !isBook ? ".comment-item" : "li.comment-item");
            String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("li.is-active").text());
            total = StringUtil.notEmpty(ts) ? Integer.parseInt(ts) : comments.size();
            for (int i = 0, len = comments.size(); i < len; i++) {
                Element comment = comments.get(i);
                Element a = comment.select("span.comment-info a").first();
                Element img = comment.select(".avatar img").first();
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
                commentInfo.setSource(NetResourceSource.DB);
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

        return new CommonResult<>(res, total);
    }
}
