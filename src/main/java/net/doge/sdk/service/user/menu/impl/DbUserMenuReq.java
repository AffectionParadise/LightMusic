package net.doge.sdk.service.user.menu.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.RadioType;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetRadioInfo;
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

public class DbUserMenuReq {
    private static DbUserMenuReq instance;

    private DbUserMenuReq() {
    }

    public static DbUserMenuReq getInstance() {
        if (instance == null) instance = new DbUserMenuReq();
        return instance;
    }

    // 用户专辑 API (豆瓣)
    private final String USER_ALBUM_DB_API = "https://music.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户电台 API (豆瓣)
    private final String USER_RADIO_DB_API = "https://movie.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户图书电台 API (豆瓣)
    private final String USER_BOOK_RADIO_DB_API = "https://book.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        final int rn = 15;
        String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, id, (page - 1) * rn))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements rs = doc.select(".item");
        String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
        total = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
        total += total / rn * 5;
        for (int i = 0, len = rs.size(); i < len; i++) {
            Element radio = rs.get(i);
            Element a = radio.select("li.title a").first();
            Element intro = radio.select("li.intro").first();
            Element img = radio.select(".pic img").first();

            String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
            String radioName = a.text();
            String coverImgThumbUrl = img.attr("src");
            String[] sp = intro.text().split(" / ");
            String artist = sp[0];
            String pubTime = sp[1];

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.DB);
            albumInfo.setId(radioId);
            albumInfo.setName(radioName);
            albumInfo.setArtist(artist);
            albumInfo.setPublishTime(pubTime);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(albumInfo);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        String id = userInfo.getId();
        Callable<CommonResult<NetRadioInfo>> getRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            int t;

            final int rn = 15;
            String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_DB_API, id, (page - 1) * rn))
                    .executeAsStr();
            Document doc = Jsoup.parse(radioInfoBody);
            Elements rs = doc.select(".item");
            String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select(".pic img").first();

                String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                String radioName = a.text();
                String dj = StringUtil.shorten(intro.text(), 100);
                String coverImgThumbUrl = img.attr("src");
                String category = "电影";

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 图书电台
        Callable<CommonResult<NetRadioInfo>> getBookRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            int t;

            final int rn = 15;
            String radioInfoBody = HttpRequest.get(String.format(USER_BOOK_RADIO_DB_API, id, (page - 1) * rn))
                    .executeAsStr();
            Document doc = Jsoup.parse(radioInfoBody);
            Elements rs = doc.select("li.subject-item");
            String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select(".info a").first();
                Element pub = radio.select(".pub").first();
                Element img = radio.select(".pic img").first();

                String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                String radioName = a.text();
                String dj = pub.text().trim();
                String coverImgThumbUrl = img.attr("src");
                String category = "书籍";

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setType(RadioType.BOOK);
                radioInfo.setSource(NetResourceSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };

        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getRadios);
        executor.submit(getBookRadios);
        return executor.getResult();
    }
}
