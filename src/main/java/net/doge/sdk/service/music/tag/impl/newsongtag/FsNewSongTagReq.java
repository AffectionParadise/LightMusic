package net.doge.sdk.service.music.tag.impl.newsongtag;

import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FsNewSongTagReq {
    private static FsNewSongTagReq instance;

    private FsNewSongTagReq() {
    }

    public static FsNewSongTagReq getInstance() {
        if (instance == null) instance = new FsNewSongTagReq();
        return instance;
    }

    // 歌曲标签 API (5sing)
    private final String SONG_TAG_API_FS = "http://5sing.kugou.com/yc/list";

    /**
     * 歌曲标签
     *
     * @return
     */
    public void initNewSongTag() {
        int c = Tags.newSongIndices.length;
        String tagBody = HttpRequest.get(SONG_TAG_API_FS)
                .executeAsStr();
        Document doc = Jsoup.parse(tagBody);
        Elements dds = doc.select("dl.song_sort dd");
        // 语种
        Elements tags = dds.first().select("a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element a = tags.get(i);

            String name = a.text();
            if ("全部".equals(name)) continue;
            String id = RegexUtil.getGroup1("&l=(.*)", a.attr("href"));

            if (!Tags.newSongTags.containsKey(name)) Tags.newSongTags.put(name, new String[c]);
            Tags.newSongTags.get(name)[TagType.RECOMMEND_NEW_SONG_FS] = " " + id;
        }
        // 曲风
        tags = dds.last().select("a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element a = tags.get(i);

            String name = a.text();
            if ("全部".equals(name)) continue;
            String id = RegexUtil.getGroup1("s=(.*?)&l=", a.attr("href"));

            if (!Tags.newSongTags.containsKey(name)) Tags.newSongTags.put(name, new String[c]);
            Tags.newSongTags.get(name)[TagType.RECOMMEND_NEW_SONG_FS] = id + " ";
        }
    }
}
