package net.doge.sdk.service.user.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.core.time.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbUserInfoReq {
    private static DbUserInfoReq instance;

    private DbUserInfoReq() {
    }

    public static DbUserInfoReq getInstance() {
        if (instance == null) instance = new DbUserInfoReq();
        return instance;
    }

    // 用户信息 API (豆瓣)
    private final String USER_DETAIL_DB_API = "https://www.douban.com/people/%s/";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        if (!userInfo.hasAccAge()) {
            String dt = RegexUtil.getGroup1("(\\d+\\-\\d+\\-\\d+)加入", doc.select(".pl").text());
            userInfo.setAccAge(TimeUtil.getAccAge(TimeUtil.dateToMs(dt)));
        }
        if (!userInfo.hasGender()) userInfo.setGender("保密");
        if (!userInfo.hasArea()) userInfo.setArea(doc.select(".user-info a").text());
        if (!userInfo.hasSign())
            userInfo.setSign(HtmlUtil.getPrettyText(doc.select("span#intro_display").first()));
//            if (!userInfo.hasFollow())
//                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
//            if (!userInfo.hasFan())
//                userInfo.setFan(Integer.parseInt(doc.select(".home-fans span").first().text()));

        String avatarUrl = doc.select(".basic-info img").attr("src");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl("")));
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id) {
        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        Element h1 = doc.select(".info > h1").first();
        Elements img = doc.select(".basic-info img");

        if (h1 != null) {
            String userName = h1.ownText();
            if (StringUtil.notEmpty(userName)) {
                String gender = "保密";
                String avatarThumbUrl = img.attr("src");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetResourceSource.DB);
                userInfo.setId(id);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
