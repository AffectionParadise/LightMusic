package net.doge.sdk.entity.album.menu;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetAlbumInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.util.SdkUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class AlbumMenuReq {
    // 相似专辑 API (豆瓣)
    private final String SIMILAR_ALBUM_DB_API = "https://music.douban.com/subject/%s/";

    /**
     * 获取相似专辑
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getSimilarAlbums(NetAlbumInfo albumInfo) {
        int source = albumInfo.getSource();
        String id = albumInfo.getId();

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 0;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String albumInfoBody = HttpRequest.get(String.format(SIMILAR_ALBUM_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("dl.subject-rec-list");
            t = rs.size();
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element album = rs.get(i);
                Element a = album.select("dd a").first();
                Element img = album.select("img").first();

                String albumId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String albumName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetAlbumInfo ai = new NetAlbumInfo();
                ai.setSource(NetMusicSource.DB);
                ai.setId(albumId);
                ai.setName(albumName);
                ai.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    ai.setCoverImgThumb(coverImgThumb);
                });
                res.add(ai);
            }
        }

        return new CommonResult<>(res, t);
    }
}
