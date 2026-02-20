package net.doge.sdk.service.playlist.tag.impl.recplaylisttag;

import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FsRecPlaylistTagReq {
    private static FsRecPlaylistTagReq instance;

    private FsRecPlaylistTagReq() {
    }

    public static FsRecPlaylistTagReq getInstance() {
        if (instance == null) instance = new FsRecPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (5sing)
    private final String PLAYLIST_TAG_FS_API = "http://5sing.kugou.com/gd/gdList";

    /**
     * 推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        int c = Tags.recPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_FS_API)
                .executeAsStr();
        Document doc = Jsoup.parse(playlistTagBody);
        Elements tags = doc.select("ul.flx li a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = tag.text();

            if (!Tags.recPlaylistTags.containsKey(name)) Tags.recPlaylistTags.put(name, new String[c]);
            Tags.recPlaylistTags.get(name)[TagType.NEW_PLAYLIST_FS] = id;
        }
    }
}
