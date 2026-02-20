package net.doge.sdk.service.radio.menu.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.RadioType;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbRadioMenuReq {
    private static DbRadioMenuReq instance;

    private DbRadioMenuReq() {
    }

    public static DbRadioMenuReq getInstance() {
        if (instance == null) instance = new DbRadioMenuReq();
        return instance;
    }

    // 电台演职员 API (豆瓣)
    private final String RADIO_ARTISTS_DB_API = "https://movie.douban.com/subject/%s/celebrities";
    // 相似电台 API (豆瓣)
    private final String SIMILAR_RADIO_DB_API = "https://movie.douban.com/subject/%s/";
    // 相似图书电台 API (豆瓣)
    private final String SIMILAR_BOOK_RADIO_DB_API = "https://book.douban.com/subject/%s/";
    // 相似游戏电台 API (豆瓣)
    private final String SIMILAR_GAME_RADIO_DB_API = "https://www.douban.com/game/%s/";

    /**
     * 获取相似电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        List<NetRadioInfo> res = new LinkedList<>();
        int t;

        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();
        String artistInfoBody = HttpRequest.get(String.format(isBook ? SIMILAR_BOOK_RADIO_DB_API
                        : isGame ? SIMILAR_GAME_RADIO_DB_API : SIMILAR_RADIO_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        Elements rs = doc.select(isBook ? "#db-rec-section dl:not(.clear)"
                : isGame ? ".list.fav-list li" : ".recommendations-bd dl");
        t = rs.size();
        for (int i = 0, len = rs.size(); i < len; i++) {
            Element radio = rs.get(i);
            Element a = radio.select(isGame ? ".text a" : "dd a").first();
            Element img = radio.select("img").first();

            String radioId = RegexUtil.getGroup1(isGame ? "game/(\\d+)/" : "subject/(\\d+)/", a.attr("href"));
            String radioName = a.text().trim();
            String coverImgThumbUrl = img.attr("src");

            NetRadioInfo ri = new NetRadioInfo();
            ri.setType(isBook ? RadioType.BOOK : isGame ? RadioType.GAME : RadioType.RADIO);
            ri.setSource(NetResourceSource.DB);
            ri.setId(radioId);
            ri.setName(radioName);
            ri.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                ri.setCoverImgThumb(coverImgThumb);
            });
            res.add(ri);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo radioInfo) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t = 0;

        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        if (!isBook) {
            String artistInfoBody = HttpRequest.get(String.format(RADIO_ARTISTS_DB_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select("li.celebrity");
            t = cs.size();
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select("span.name a").first();
                Element img = artist.select(".avatar").first();

                String artistId = RegexUtil.getGroup1("celebrity/(\\d+)/", a.attr("href"));
                String artistName = a.text();
                String coverImgThumbUrl = RegexUtil.getGroup1("url\\((.*?)\\)", img.attr("style"));

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
