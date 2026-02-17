package net.doge.sdk.service.artist.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class DbArtistInfoReq {
    private static DbArtistInfoReq instance;

    private DbArtistInfoReq() {
    }

    public static DbArtistInfoReq getInstance() {
        if (instance == null) instance = new DbArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (豆瓣)
    private final String ARTIST_DETAIL_DB_API = "https://movie.douban.com/celebrity/%s/";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        //                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, id))
//                        .executeAsync()
//                        .body();
//                Document doc = Jsoup.parse(artistInfoBody);
//                Element h1 = doc.select("#content > h1").first();
//                Element img = doc.select(".nbg img").first();
//
//                String name = h1.text();
//                String coverImgThumbUrl = img.attr("src");
//
//                NetArtistInfo artistInfo = new NetArtistInfo();
//                artistInfo.setSource(NetMusicSource.DB);
//                artistInfo.setId(id);
//                artistInfo.setName(name);
//                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.SdkUtil.extractCover(coverImgThumbUrl);
//                    artistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(artistInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        String info = HtmlUtil.getPrettyText(doc.select("#headline .info").first()) + "\n";
        Element bd = doc.select("#intro .bd").first();
        Elements span = bd.select("span");
        String desc = HtmlUtil.getPrettyText(span.isEmpty() ? bd : span.last());
        String coverImgUrl = doc.select(".nbg img").attr("src");

        artistInfo.setDescription(info + desc);
        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
    }
}
