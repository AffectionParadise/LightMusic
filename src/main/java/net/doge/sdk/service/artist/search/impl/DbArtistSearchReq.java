package net.doge.sdk.service.artist.search.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbArtistSearchReq {
    private static DbArtistSearchReq instance;

    private DbArtistSearchReq() {
    }

    public static DbArtistSearchReq getInstance() {
        if (instance == null) instance = new DbArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (豆瓣)
    private final String SEARCH_ARTIST_DB_API = "https://movie.douban.com/celebrities/search?search_text=%s&start=%s";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_DB_API, encodedKeyword, (page - 1) * 15))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        t = Integer.parseInt(doc.select(".rr").first().text().split("共")[1]);
        t += t / 15 * 5;
        Elements result = doc.select(".result");
        for (int i = 0, len = result.size(); i < len; i++) {
            Element artist = result.get(i);
            Element a = artist.select(".content a").first();
            Element img = artist.select(".pic img").first();

            String artistId = RegexUtil.getGroup1("celebrity/(\\d+)/", a.attr("href"));
            String artistName = a.text();
            String coverImgThumbUrl = img.attr("src");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetResourceSource.DB);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
            r.add(artistInfo);
        }
        return new CommonResult<>(r, t);
    }
}
