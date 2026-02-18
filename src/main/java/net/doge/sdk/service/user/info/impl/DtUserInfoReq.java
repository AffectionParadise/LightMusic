package net.doge.sdk.service.user.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DtUserInfoReq {
    private static DtUserInfoReq instance;

    private DtUserInfoReq() {
    }

    public static DtUserInfoReq getInstance() {
        if (instance == null) instance = new DtUserInfoReq();
        return instance;
    }

    // 用户信息 API (堆糖)
    private final String USER_DETAIL_DT_API = "https://www.duitang.com/people/?id=%s";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DT_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);
        Elements a = doc.select(".people-funs a");

        if (!userInfo.hasGender()) userInfo.setGender("保密");
        if (!userInfo.hasSign())
            userInfo.setSign(doc.select(".people-desc").text().trim());
        if (!userInfo.hasProgramCount()) {
            String s = doc.select("ul.people-nav li a").get(1).select("u").text().trim();
            if (StringUtil.notEmpty(s)) userInfo.setProgramCount(Integer.parseInt(s));
        }
        if (!userInfo.hasFollow())
            userInfo.setFollow(Integer.parseInt(RegexUtil.getGroup1("(\\d+)", a.first().text())));
        if (!userInfo.hasFan())
            userInfo.setFan(Integer.parseInt(RegexUtil.getGroup1("(\\d+)", a.last().text())));

        String avatarUrl = doc.select("a.people-avatar img").attr("src").replaceFirst("\\.thumb\\.\\d+_\\d+_\\w+", "");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

        String bgImgUrl = doc.select("img.header-bg").attr("src");
        if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
    }
}
