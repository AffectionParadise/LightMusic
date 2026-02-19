package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FsHotPlaylistTagReq {
    private static FsHotPlaylistTagReq instance;

    private FsHotPlaylistTagReq() {
    }

    public static FsHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new FsHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (5sing)
    private final String PLAYLIST_TAG_FS_API = "http://5sing.kugou.com/gd/gdList";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_FS_API)
                .executeAsStr();
        Document doc = Jsoup.parse(playlistTagBody);
        Elements tags = doc.select("ul.flx li a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = tag.text();

            if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
            Tags.hotPlaylistTag.get(name)[10] = id;
        }
    }
}
