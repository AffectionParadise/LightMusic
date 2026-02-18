package net.doge.sdk.service.user.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbUserSearchReq {
    private static DbUserSearchReq instance;

    private DbUserSearchReq() {
    }

    public static DbUserSearchReq getInstance() {
        if (instance == null) instance = new DbUserSearchReq();
        return instance;
    }

    // 关键词搜索用户 API (豆瓣)
    private final String SEARCH_USER_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1005";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(String keyword, int page, int limit) {
        List<NetUserInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        final int lim = Math.min(20, limit);
        String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_DB_API, encodedKeyword, (page - 1) * lim))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONArray userArray = userInfoJson.getJSONArray("items");
        if (JsonUtil.notEmpty(userArray)) {
            int to = userInfoJson.getIntValue("total");
            t = PageUtil.totalPage(to, lim) * limit;
            for (int i = 0, len = userArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(userArray.getString(i));
                Elements result = doc.select(".result");
                Elements a = result.select("h3 a");
                Elements info = result.select(".title .info");
                Elements img = result.select(".pic img");

                String userId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                String userName = a.text().trim();
                String gender = "保密";
                String sr = img.attr("src");
                String avatarThumbUrl = sr.contains("/user") ? sr.replaceFirst("normal", "large") : sr.replaceFirst("/up", "/ul");
                Integer fan = Integer.parseInt(RegexUtil.getGroup1("(\\d+)人关注", info.text()));

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DB);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFan(fan);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });

                r.add(userInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
