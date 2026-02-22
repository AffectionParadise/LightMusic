package net.doge.sdk.service.album.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DbAlbumInfoReq {
    private static DbAlbumInfoReq instance;

    private DbAlbumInfoReq() {
    }

    public static DbAlbumInfoReq getInstance() {
        if (instance == null) instance = new DbAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (豆瓣)
    private final String ALBUM_DETAIL_DB_API = "https://music.douban.com/subject/%s/";

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        String info = HtmlUtil.getPrettyText(doc.select("#info").first()) + "\n";
        Element re = doc.select("#link-report").first();
        Elements span = re.select("span");
        String desc = HtmlUtil.getPrettyText(span.isEmpty() ? re : span.last()) + "\n";
        String tracks = HtmlUtil.getPrettyText(doc.select(".track-list div div").first());
        String coverImgUrl = doc.select("#mainpic img").attr("src");

        albumInfo.setDescription(info + desc + "\n曲目：\n" + tracks);
        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
    }
}
