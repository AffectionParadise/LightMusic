package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MeHotPlaylistTagReq {
    private static MeHotPlaylistTagReq instance;

    private MeHotPlaylistTagReq() {
    }

    public static MeHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new MeHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API = "https://www.missevan.com/malbum/recommand";
    // 探索歌单标签 API (猫耳)
    private final String EXP_PLAYLIST_TAG_ME_API = "https://www.missevan.com/explore";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_ME_API)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONObject tags = playlistTagJson.getJSONObject("info");
        final String[] cats = new String[]{"主题", "场景", "情感"};
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONArray tagArray = tags.getJSONArray(cats[i]);
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONArray tagJsonArray = tagArray.getJSONArray(j);

                String name = tagJsonArray.getString(1);
                String id = tagJsonArray.getString(0);

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[8] = id;
            }
        }
    }

    /**
     * 猫耳探索
     *
     * @return
     */
    public void initExpPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(EXP_PLAYLIST_TAG_ME_API)
                .executeAsStr();
        Document doc = Jsoup.parse(playlistTagBody);

        Elements tags = doc.select(".explore-tag");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element t = tags.get(i);

            String id = t.attr("data-tagid");
            String name = t.getElementsByTag("a").text().trim();

            if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
            Tags.hotPlaylistTag.get(name)[9] = id;
        }
    }
}
