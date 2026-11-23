package net.doge.sdk.service.album.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetAlbumInfo;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbNewAlbumReq {
    private static DbNewAlbumReq instance;

    private DbNewAlbumReq() {
    }

    public static DbNewAlbumReq getInstance() {
        if (instance == null) instance = new DbNewAlbumReq();
        return instance;
    }

    // Top 250 专辑 API (豆瓣)
    private final String TOP_ALBUM_DB_API = "https://music.douban.com/top250";
    // 分类专辑 API (豆瓣)
    private final String CAT_ALBUM_DB_API = "https://music.douban.com/tag/%s";

    /**
     * Top 250
     */
    public CommonResult<NetAlbumInfo> getTopAlbums(int page) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        final int rn = 25;

        Map<String, Object> params = new HashMap<>();
        params.put("start", (page - 1) * rn);
        String albumInfoBody = HttpRequest.get(TOP_ALBUM_DB_API)
                .queryParams(params)
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements as = doc.select("tr.item");
        t -= 250 / rn * 5;
        for (int i = 0, len = as.size(); i < len; i++) {
            Element album = as.get(i);
            Elements a = album.select(".pl2 a");
            Elements pl = album.select(".pl2 p.pl");
            Elements img = album.select("td img");

            String albumId = RegexUtil.getGroup1("/subject/(\\d+)/", a.attr("href"));
            String albumName = a.text().trim();
            String[] sp = pl.text().split(" / ");
            String artist = sp[0];
            String pubTime = sp[1];
            String coverImgThumbUrl = img.attr("src");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.DB);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setPublishTime(pubTime);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类专辑
     */
    public CommonResult<NetAlbumInfo> getCatAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTags.get(tag);

        String param = s[TagModule.CAT_ALBUM_DB];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("start", (page - 1) * limit);
            params.put("type", "T");
            String albumInfoBody = HttpRequest.get(String.format(CAT_ALBUM_DB_API, param))
                    .queryParams(params)
                    .executeAsStr();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements as = doc.select("tr.item");
            Element te = doc.select(".paginator > a").last();
            String ts = te == null ? "" : te.text();
            t = StringUtil.notEmpty(ts) ? Integer.parseInt(ts) * limit : limit;
            for (int i = 0, len = as.size(); i < len; i++) {
                Element album = as.get(i);
                Elements a = album.select(".pl2 a");
                Elements pl = album.select(".pl2 p.pl");
                Elements img = album.select("td img");

                String albumId = RegexUtil.getGroup1("/subject/(\\d+)/", a.attr("href"));
                String albumName = a.text().trim();
                String[] sp = pl.text().split(" / ");
                String artist = sp[0];
                String pubTime = sp[1];
                String coverImgThumbUrl = img.attr("src");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetResourceSource.DB);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setPublishTime(pubTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(albumInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
