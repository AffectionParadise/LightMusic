package net.doge.sdk.service.album.tag.impl;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DbNewAlbumTagReq {
    private static DbNewAlbumTagReq instance;

    private DbNewAlbumTagReq() {
    }

    public static DbNewAlbumTagReq getInstance() {
        if (instance == null) instance = new DbNewAlbumTagReq();
        return instance;
    }

    // 专辑标签 API (豆瓣)
    private final String ALBUM_TAG_DB_API = "https://music.douban.com/tag/";

    /**
     * 分类专辑标签
     */
    public void initAlbumTag() {
        int c = Tags.newAlbumIndices.length;
        String albumTagBody = HttpRequest.get(ALBUM_TAG_DB_API)
                .executeAsStr();
        Document doc = Jsoup.parse(albumTagBody);
        Elements tags = doc.select("tbody tr td a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = tag.text();

            if (!Tags.newAlbumTags.containsKey(name)) Tags.newAlbumTags.put(name, new String[c]);
            Tags.newAlbumTags.get(name)[6] = id;
        }
    }
}
