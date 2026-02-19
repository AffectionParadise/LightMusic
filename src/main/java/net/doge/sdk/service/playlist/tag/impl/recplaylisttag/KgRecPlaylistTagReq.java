package net.doge.sdk.service.playlist.tag.impl.recplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

public class KgRecPlaylistTagReq {
    private static KgRecPlaylistTagReq instance;

    private KgRecPlaylistTagReq() {
    }

    public static KgRecPlaylistTagReq getInstance() {
        if (instance == null) instance = new KgRecPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";

    /**
     * 推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        int c = Tags.recPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KG_API)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
        final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
        for (int i = 0, len = cats.length; i < len; i++) {
            JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.recPlaylistTags.containsKey(name)) Tags.recPlaylistTags.put(name, new String[c]);
                Tags.recPlaylistTags.get(name)[1] = id;
            }
        }
    }
}
