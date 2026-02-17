package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
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

public class DbAlbumSearchReq {
    private static DbAlbumSearchReq instance;

    private DbAlbumSearchReq() {
    }

    public static DbAlbumSearchReq getInstance() {
        if (instance == null) instance = new DbAlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API (豆瓣)
    private final String SEARCH_ALBUM_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1003";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        final int lim = Math.min(20, limit);
        String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_DB_API, encodedKeyword, (page - 1) * lim))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray albumArray = albumInfoJson.getJSONArray("items");
        if (JsonUtil.notEmpty(albumArray)) {
            int to = albumInfoJson.getIntValue("total");
            t = PageUtil.totalPage(to, lim) * limit;
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(albumArray.getString(i));
                Elements result = doc.select(".result");
                Elements a = result.select("h3 a");

                String albumId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                String albumName = a.text().trim();
                String artist = result.select("span.subject-cast").text();
                String coverImgThumbUrl = result.select(".pic img").attr("src");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(albumInfo);
            }
        }
//            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_DB_API, encodedKeyword, (page - 1) * lim))
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(albumInfoBody);
//            t = 4000 / lim * limit;
//            Elements result = doc.select(".sc-bZQynM.hrvolz.sc-bxivhb.hvEfwz");
//            for (int i = 0, len = result.size(); i < len; i++) {
//                Element album = result.get(i);
//                Element a = album.select(".title a").first();
//                Element img = album.select(".item-root img").first();
//
//                String albumId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
//                String albumName = a.text();
//                String artist = album.select(".meta.abstract").text();
//                String coverImgThumbUrl = img.attr("src");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.DB);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(albumInfo);
//            }
        return new CommonResult<>(r, t);
    }
}
