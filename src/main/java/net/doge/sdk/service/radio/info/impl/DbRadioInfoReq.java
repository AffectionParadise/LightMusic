package net.doge.sdk.service.radio.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class DbRadioInfoReq {
    private static DbRadioInfoReq instance;

    private DbRadioInfoReq() {
    }

    public static DbRadioInfoReq getInstance() {
        if (instance == null) instance = new DbRadioInfoReq();
        return instance;
    }

    // 电台信息 API (豆瓣)
    private final String RADIO_DETAIL_DB_API = "https://movie.douban.com/subject/%s/";
    // 图书电台信息 API (豆瓣)
    private final String BOOK_RADIO_DETAIL_DB_API = "https://book.douban.com/subject/%s/";
    // 游戏电台信息 API (豆瓣)
    private final String GAME_RADIO_DETAIL_DB_API = "https://www.douban.com/game/%s/";
    // 获取电台照片 API (豆瓣)
    private final String GET_RADIO_IMG_DB_API = "https://movie.douban.com/subject/%s/photos?type=S&start=%s&sortby=like&size=a&subtype=a";
    // 获取电台海报 API (豆瓣)
    private final String GET_RADIO_POSTER_DB_API = "https://movie.douban.com/subject/%s/photos?type=R&start=%s&sortby=like&size=a&subtype=a";
    // 获取游戏电台照片 API (豆瓣)
    private final String GET_GAME_RADIO_IMG_DB_API = "https://www.douban.com/game/%s/photos/?type=all&start=%s&sortby=hot";

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();
        if (isBook) {
            String radioInfoBody = HttpRequest.get(String.format(BOOK_RADIO_DETAIL_DB_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(radioInfoBody);
            String info = HtmlUtil.getPrettyText(doc.select("#info").first()) + "\n";
            Elements re = doc.select("#link-report");
            Elements span = re.select("span");
            Element intro = doc.select(".intro").last();
            Element cata = doc.select(String.format("#dir_%s_full", id)).first();
            Element tr = doc.select(".subject_show.block5:not(#rec-ebook-section) div").first();

            String desc = HtmlUtil.getPrettyText(span.isEmpty() ? re.first() : span.last()) + "\n";
            String authorIntro = HtmlUtil.getPrettyText(intro) + "\n";
            String catalog = HtmlUtil.getPrettyText(cata) + "\n\n";
            String trace = HtmlUtil.getPrettyText(tr);
            String coverImgUrl = doc.select("#mainpic img").attr("src");

            radioInfo.setDescription(info + desc + "作者简介：\n" + authorIntro + "目录：\n" + catalog + "丛书信息：\n" + trace);
            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        } else if (isGame) {
            String radioInfoBody = HttpRequest.get(String.format(GAME_RADIO_DETAIL_DB_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(radioInfoBody);
            String info = HtmlUtil.getPrettyText(doc.select("dl.game-attr").first()) + "\n";
            Element p = doc.select("#link-report p").first();

            String desc = HtmlUtil.getPrettyText(p) + "\n";
            String coverImgUrl = doc.select(".pic img").attr("src");

            radioInfo.setDescription(info + desc);
            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        } else {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_DB_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(radioInfoBody);
            String info = HtmlUtil.getPrettyText(doc.select("#info").first()) + "\n";
            Elements re = doc.select("#link-report");
            Elements span = re.select("span");

            String desc = HtmlUtil.getPrettyText(span.isEmpty() ? re.first() : span.last()) + "\n";
            String coverImgUrl = doc.select("#mainpic img").attr("src");

            radioInfo.setDescription(info + desc);
            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        }
    }

    /**
     * 获取电台照片链接
     */
    public CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        List<String> res = new LinkedList<>();
        int total = 0;

        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();
        final int limit = isGame ? 24 : 30;
        if (isGame) {
            String imgInfoBody = HttpRequest.get(String.format(GET_GAME_RADIO_IMG_DB_API, id, (page - 1) * limit))
                    .executeAsStr();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select(".pholist ul img");
            String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
            total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/thumb/", "/photo/");
                res.add(url);
            }
        } else if (!isBook) {
            String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_IMG_DB_API, id, (page - 1) * limit))
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
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取电台海报链接
     */
    public CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        List<String> imgUrls = new LinkedList<>();
        int total = 0;

        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        final int limit = 30;
        if (!isBook) {
            String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_POSTER_DB_API, id, (page - 1) * limit))
                    .executeAsStr();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix .cover img");
            String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
            total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                imgUrls.add(url);
            }
        }

        return new CommonResult<>(imgUrls, total);
    }
}
