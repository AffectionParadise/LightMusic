package net.doge.sdk.service.user.menu.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class FsUserMenuReq {
    private static FsUserMenuReq instance;

    private FsUserMenuReq() {
    }

    public static FsUserMenuReq getInstance() {
        if (instance == null) instance = new FsUserMenuReq();
        return instance;
    }

    // 用户关注 API (5sing)
    private final String USER_FOLLOWS_FS_API = "http://5sing.kugou.com/%s/friend/%s.html";
    // 用户粉丝 API (5sing)
    private final String USER_FANS_FS_API = "http://5sing.kugou.com/%s/fans/%s.html";

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFollows(NetUserInfo netUserInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t = 0;

        String id = netUserInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_FS_API, id, page))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        // 判断主页 ui 类型
        int pageType = 1;
        if (!doc.select(".mainnav").isEmpty()) pageType = 2;
        else if (!doc.select(".right h1").isEmpty()) pageType = 3;
        else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
        else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
        else if (!doc.select(".user_name").isEmpty()) pageType = 6;

        switch (pageType) {
            case 1:
                Elements pageElem = doc.select(".page_number em");
                String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 42 + 1) * limit + 1 : limit;
                break;
            case 2:
                pageElem = doc.select(".page_list span");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 3:
                pageElem = doc.select("a.page_t_next");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.attr("href"));
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 4:
            case 6:
                pageElem = doc.select(".msg_page_list a");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 2).attr("href"));
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 5:
                pageElem = doc.select(".page span");
                pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
        }

        // 主页 ui 不同
        Elements userArray;
        if (pageType == 1) userArray = doc.select(".follow_list li a");
        else if (pageType == 2) userArray = doc.select(".mid.friend_list li a");
        else if (pageType == 3) userArray = doc.select(".fans_list li a");
        else if (pageType == 4) userArray = doc.select("li dl.c_wap");
        else if (pageType == 5) userArray = doc.select(".single_care li");
        else userArray = doc.select("dl.c_ft");
        if (pageType == 4 || pageType == 6) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Element a = user.select(pageType == 6 ? "h2.c_ft a" : "h2.c_wap a").first();
                Elements img = user.select("dt.lt img");
                Elements la = user.select("label a");

                String userId = RegexUtil.getGroup1("/(\\d+)", a.attr("href"));
                String userName = a.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
                Integer follow = Integer.parseInt(la.first().text());
                Integer fan = Integer.parseInt(la.last().text());

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        } else if (pageType == 5) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Elements a = user.select("a");
                Elements img = user.select("img");

                String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", a.attr("href"));
                String userName = a.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        } else {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Elements img = user.select("img");

                String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", user.attr("href"));
                String userName = user.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFans(NetUserInfo netUserInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t = 0;

        String id = netUserInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_FANS_FS_API, id, page))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        // 判断主页 ui 类型
        int pageType = 1;
        if (!doc.select(".mainnav").isEmpty()) pageType = 2;
        else if (!doc.select(".right h1").isEmpty()) pageType = 3;
        else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
        else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
        else if (!doc.select(".user_name").isEmpty()) pageType = 6;

        switch (pageType) {
            case 1:
                Elements pageElem = doc.select(".page_number em");
                String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 42 + 1) * limit + 1 : limit;
                break;
            case 2:
                pageElem = doc.select(".page_list span");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 3:
                pageElem = doc.select("a.page_t_next");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.attr("href"));
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 4:
            case 6:
                pageElem = doc.select(".msg_page_list a");
                pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 2).attr("href"));
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
            case 5:
                pageElem = doc.select(".page span");
                pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                break;
        }

        // 主页 ui 不同
        Elements userArray;
        if (pageType == 1) userArray = doc.select(".follow_list li a");
        else if (pageType == 2) userArray = doc.select(".mid.friend_list li a");
        else if (pageType == 3) userArray = doc.select(".fans_list li a");
        else if (pageType == 4) userArray = doc.select("li dl.c_wap");
        else if (pageType == 5) userArray = doc.select(".single_care li");
        else userArray = doc.select("dl.c_ft");
        if (pageType == 4 || pageType == 6) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Element a = user.select(pageType == 6 ? "h2.c_ft a" : "h2.c_wap a").first();
                Elements img = user.select("dt.lt img");
                Elements la = user.select("label a");

                String userId = RegexUtil.getGroup1("/(\\d+)", a.attr("href"));
                String userName = a.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
                Integer follow = Integer.parseInt(la.first().text());
                Integer fan = Integer.parseInt(la.last().text());

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        } else if (pageType == 5) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Elements a = user.select("a");
                Elements img = user.select("img");

                String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", a.attr("href"));
                String userName = a.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        } else {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Element user = userArray.get(i);
                Elements img = user.select("img");

                String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", user.attr("href"));
                String userName = user.attr("title");
                String gender = "保密";
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
