package net.doge.sdk.service.artist.menu.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetRadioInfo;
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

public class DbArtistMenuReq {
    private static DbArtistMenuReq instance;

    private DbArtistMenuReq() {
    }

    public static DbArtistMenuReq getInstance() {
        if (instance == null) instance = new DbArtistMenuReq();
        return instance;
    }

    // 歌手粉丝 API (豆瓣)
    private final String ARTIST_FANS_DB_API = "https://movie.douban.com/celebrity/%s/fans?start=%s";
    // 歌手合作人 API (豆瓣)
    private final String ARTIST_BUDDY_DB_API = "https://movie.douban.com/celebrity/%s/partners?start=%s";
    // 歌手电台 API (豆瓣)
    private final String ARTIST_RADIO_DB_API = "https://movie.douban.com/celebrity/%s/movies?start=%s&format=pic&sortby=time";
    // 获取歌手照片 API (豆瓣)
    private final String GET_ARTISTS_IMG_DB_API = "https://movie.douban.com/celebrity/%s/photos/?type=C&start=%s&sortby=like&size=a&subtype=a";

    /**
     * 获取歌手照片链接
     */
    public CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        List<String> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        final int limit = 30;
        String imgInfoBody = HttpRequest.get(String.format(GET_ARTISTS_IMG_DB_API, id, (page - 1) * limit))
                .executeAsStr();
        Document doc = Jsoup.parse(imgInfoBody);
        Elements imgs = doc.select("ul.poster-col3.clearfix .cover img");
        String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
        total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
        for (int i = 0, len = imgs.size(); i < len; i++) {
            Element img = imgs.get(i);
            String url = img.attr("src").replaceFirst("/m/", "/l/");
            res.add(url);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page) {
        List<NetUserInfo> res = new LinkedList<>();
        int t;

        String id = artistInfo.getId();
        final int rn = 35;
        String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_DB_API, id, (page - 1) * rn))
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);
        String ts = RegexUtil.getGroup1("（(\\d+)）", doc.select("#content > h1").text());
        int tn = Integer.parseInt(ts);
        t = tn - (tn / rn * 15);
        Elements us = doc.select("dl.obu");
        for (int i = 0, len = us.size(); i < len; i++) {
            Element user = us.get(i);
            Elements a = user.select("dd a");
            Elements img = user.select("img");

            String userId = RegexUtil.getGroup1("/people/(.*?)/", a.attr("href"));
            String userName = a.text();
            String gender = "保密";
            String src = img.attr("src");
            String avatarThumbUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst("/u", "/ul");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.DB);
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

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page, int limit) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t;

        String id = netArtistInfo.getId();
        final int dbLimit = 10;
        String artistInfoBody = HttpRequest.get(String.format(ARTIST_BUDDY_DB_API, id, (page - 1) * dbLimit))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        Elements cs = doc.select(".partners.item");
        String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
        t = StringUtil.isEmpty(ts) ? cs.size() : Integer.parseInt(ts);
        t += t / limit * 10;
        for (int i = 0, len = cs.size(); i < len; i++) {
            Element artist = cs.get(i);
            Element a = artist.select(".info a").first();
            Element img = artist.select(".pic img").first();

            String artistId = RegexUtil.getGroup1("celebrity/(\\d+)/", a.attr("href"));
            String artistName = a.text();
            String coverImgThumbUrl = img.attr("src");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetMusicSource.DB);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(artistInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        List<NetRadioInfo> res = new LinkedList<>();
        int t;

        String id = artistInfo.getId();
        final int dbLimit = 10;
        String artistInfoBody = HttpRequest.get(String.format(ARTIST_RADIO_DB_API, id, (page - 1) * dbLimit))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        Elements rs = doc.select(".grid_view > ul > li > dl");
        String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
        t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
        t += t / limit * 10;
        for (int i = 0, len = rs.size(); i < len; i++) {
            Element radio = rs.get(i);
            Element a = radio.select("h6 a").first();
            Element span = radio.select("h6 span").first();
            Element img = radio.select("img").first();
            Elements dl = radio.select("dl > dd > dl");

            String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
            String radioName = a.text();
            String dj = dl.text().trim();
            String coverImgThumbUrl = img.attr("src");
            String category = RegexUtil.getGroup1("(\\d+)", span.text());

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.DB);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setCategory(category);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(radioInfo);
        }

        return new CommonResult<>(res, t);
    }
}
