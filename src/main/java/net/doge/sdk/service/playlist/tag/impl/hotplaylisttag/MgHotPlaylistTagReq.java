package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

public class MgHotPlaylistTagReq {
    private static MgHotPlaylistTagReq instance;

    private MgHotPlaylistTagReq() {
    }

    public static MgHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new MgHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (咪咕)
    private final String PLAYLIST_TAG_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-taglist/release";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_MG_API)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONArray tagArray = tags.getJSONObject(i).getJSONArray("content");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONArray tagJsonArray = tagArray.getJSONObject(j).getJSONArray("texts");

                String name = tagJsonArray.getString(0);
                String id = tagJsonArray.getString(1);

                if (!Tags.hotPlaylistTags.containsKey(name)) Tags.hotPlaylistTags.put(name, new String[c]);
                Tags.hotPlaylistTags.get(name)[6] = id;
            }
        }
    }
}
