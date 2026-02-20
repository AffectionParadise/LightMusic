package net.doge.sdk.service.user.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
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
import java.util.concurrent.Callable;

public class FsUserInfoReq {
    private static FsUserInfoReq instance;

    private FsUserInfoReq() {
    }

    public static FsUserInfoReq getInstance() {
        if (instance == null) instance = new FsUserInfoReq();
        return instance;
    }

    // 用户信息 API (5sing)
    private final String USER_DETAIL_FS_API = "http://5sing.kugou.com/%s/default.html";
    // 用户歌曲(原唱) API (5sing)
    private final String USER_YC_SONGS_FS_API = "http://5sing.kugou.com/%s/yc/%s.html";
    // 用户歌曲(翻唱) API (5sing)
    private final String USER_FC_SONGS_FS_API = "http://5sing.kugou.com/%s/fc/%s.html";
    // 用户歌曲(伴奏) API (5sing)
    private final String USER_BZ_SONGS_FS_API = "http://5sing.kugou.com/%s/bz/%s.html";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_FS_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        // 个人信息面板 ui 不同，需要分情况
        // 签名
        Elements intro = doc.select(".home_fullTxt");
        if (intro.isEmpty()) intro = doc.select(".rig.intro p");
        if (intro.isEmpty()) intro = doc.select(".simpleTxt");
        if (intro.isEmpty()) intro = doc.select(".resurm");
        if (intro.isEmpty()) intro = doc.select(".intr_box");
        // 头像
        Elements img = doc.select(".m_about.lt dt img");
        if (img.isEmpty()) img = doc.select(".user_pic img");
        if (img.isEmpty()) img = doc.select(".photo img");
        if (img.isEmpty()) img = doc.select(".b_con.c_wap dt img");
        if (img.isEmpty()) img = doc.select(".my_pic img");
        if (img.isEmpty()) img = doc.select(".p_abs img");
        // 关注数
        Elements followElem = doc.select("#totalfriend a");
        if (followElem.isEmpty()) followElem = doc.select(".lt.w_20 a");
        // 粉丝数
        Elements fanElem = doc.select("#totalfans a");
        // 背景图
        Elements bgImgElem = doc.select("html > body");

        String sign = intro.first().ownText();
        String avatarUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
        Integer follow = Integer.parseInt(followElem.text());
        Integer fan = Integer.parseInt(fanElem.text());

        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
        userInfo.setSign(sign);
        if (!userInfo.hasFollow()) userInfo.setFollow(follow);
        if (!userInfo.hasFan()) userInfo.setFan(fan);

        String bgImgUrl = RegexUtil.getGroup1("background-image:url\\((.*?)\\)", bgImgElem.attr("style"));
        if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(NetUserInfo userInfo, int page, int limit) {
        String id = userInfo.getId();
        // 原唱
        Callable<CommonResult<NetMusicInfo>> getYc = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            int t = 0;

            String userInfoBody = HttpRequest.get(String.format(USER_YC_SONGS_FS_API, id, page))
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
                    t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                    break;
                case 2:
                    pageElem = doc.select(".page_list span");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 3:
                    pageElem = doc.select(".page_message a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 4:
                case 6:
                    pageElem = doc.select("span.page_list a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 5:
                    pageElem = doc.select(".page span");
                    pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
            }

            // 主页 ui 不同
            Elements songArray;
            if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
            else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
            else if (pageType == 3) songArray = doc.select(".song_tb_a a");
            else if (pageType == 4) songArray = doc.select(".lt.list_name a");
            else if (pageType == 5) songArray = doc.select(".per_td a");
            else songArray = doc.select(".s_title.list_name.lt a");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                Element song = songArray.get(i);

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                String name = song.text();
                String artist = userInfo.getName();
                String artistId = id;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetResourceSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }

            return new CommonResult<>(r, t);
        };
        // 翻唱
        Callable<CommonResult<NetMusicInfo>> getFc = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            int t = 0;

            String userInfoBody = HttpRequest.get(String.format(USER_FC_SONGS_FS_API, id, page))
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
                    t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                    break;
                case 2:
                    pageElem = doc.select(".page_list span");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 3:
                    pageElem = doc.select(".page_message a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 4:
                case 6:
                    pageElem = doc.select("span.page_list a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 5:
                    pageElem = doc.select(".page span");
                    pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
            }

            // 主页 ui 不同
            Elements songArray;
            if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
            else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
            else if (pageType == 3) songArray = doc.select(".song_tb_a a");
            else if (pageType == 4) songArray = doc.select(".lt.list_name a");
            else if (pageType == 5) songArray = doc.select(".per_td a");
            else songArray = doc.select(".s_title.list_name.lt a");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                Element song = songArray.get(i);

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                String name = song.text();
                String artist = userInfo.getName();
                String artistId = id;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetResourceSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }

            return new CommonResult<>(r, t);
        };
        // 伴奏
        Callable<CommonResult<NetMusicInfo>> getBz = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            int t = 0;

            String userInfoBody = HttpRequest.get(String.format(USER_BZ_SONGS_FS_API, id, page))
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
                    t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                    break;
                case 2:
                    pageElem = doc.select(".page_list span");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 3:
                    pageElem = doc.select(".page_message a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 4:
                case 6:
                    pageElem = doc.select("span.page_list a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 5:
                    pageElem = doc.select(".page span");
                    pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
            }

            // 主页 ui 不同
            Elements songArray;
            if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
            else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
            else if (pageType == 3) songArray = doc.select(".song_tb_a a");
            else if (pageType == 4) songArray = doc.select(".lt.list_name a");
            else if (pageType == 5) songArray = doc.select(".per_td a");
            else songArray = doc.select(".s_title.list_name.lt a");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                Element song = songArray.get(i);

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                String name = song.text();
                String artist = userInfo.getName();
                String artistId = id;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetResourceSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }

            return new CommonResult<>(r, t);
        };

        MultiCommonResultCallableExecutor<NetMusicInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getYc);
        executor.submit(getFc);
        executor.submit(getBz);
        return executor.getResult();
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id) {
        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_FS_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        // 个人信息面板 ui 不同，需要分情况
        // 用户名
        Elements nameElem = doc.select("h1.lt");
        if (nameElem.isEmpty()) nameElem = doc.select(".user_info h2");
        if (nameElem.isEmpty()) nameElem = doc.select(".right h1");
        if (nameElem.isEmpty()) nameElem = doc.select(".rank_ry.c_wap h1");
        if (nameElem.isEmpty()) nameElem = doc.select(".per_info h1");
        if (nameElem.isEmpty()) nameElem = doc.select(".user_name h3");
        // 头像
        Elements img = doc.select(".m_about.lt dt img");
        if (img.isEmpty()) img = doc.select(".user_pic img");
        if (img.isEmpty()) img = doc.select(".photo img");
        if (img.isEmpty()) img = doc.select(".b_con.c_wap dt img");
        if (img.isEmpty()) img = doc.select(".my_pic img");
        if (img.isEmpty()) img = doc.select(".p_abs img");
        // 关注数
        Elements followElem = doc.select("#totalfriend a");
        if (followElem.isEmpty()) followElem = doc.select(".lt.w_20 a");
        // 粉丝数
        Elements fanElem = doc.select("#totalfans a");

        String name = nameElem.text().replaceFirst("音乐人：", "");
        String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
        Integer follow = Integer.parseInt(followElem.text());
        Integer fan = Integer.parseInt(fanElem.text());

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(NetResourceSource.FS);
        userInfo.setId(id);
        userInfo.setName(name);
        userInfo.setAvatarThumbUrl(avatarThumbUrl);
        userInfo.setFollow(follow);
        userInfo.setFan(fan);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
            userInfo.setAvatarThumb(coverImgThumb);
        });

        res.add(userInfo);

        return new CommonResult<>(res, t);
    }
}
