package net.doge.sdk.service.album.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
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
import java.util.LinkedList;
import java.util.List;

public class LzNewAlbumReq {
    private static LzNewAlbumReq instance;

    private LzNewAlbumReq() {
    }

    public static LzNewAlbumReq getInstance() {
        if (instance == null) instance = new LzNewAlbumReq();
        return instance;
    }

    // 专辑 API (李志)
    private final String ALBUM_LZ_API = "https://www.lizhinb.com/gequ/";

    /**
     * 专辑
     */
    public CommonResult<NetAlbumInfo> getAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = HttpRequest.get(ALBUM_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements albums = doc.select(".wp-block-image");
        t = albums.size();
        for (int i = (page - 1) * limit, len = Math.min(page * limit, albums.size()); i < len; i++) {
            Element album = albums.get(i);
            Elements a = album.select("a");
            Elements cap = album.select(".wp-element-caption");
            Elements img = album.select("img");

            String albumId = RegexUtil.getGroup1("/(.*?)/", a.attr("href"));
            String albumName = cap.text();
            String artist = "李志";
            String coverImgThumbUrl = img.attr("srcset").split(" ")[0];
            if (StringUtil.isEmpty(coverImgThumbUrl)) coverImgThumbUrl = img.attr("data-src");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.LZ);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            String finalCoverImgThumbUrl = coverImgThumbUrl;
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(finalCoverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }
}
