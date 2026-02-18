package net.doge.sdk.service.playlist.tag.impl.recplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

public class MeRecPlaylistTagReq {
    private static MeRecPlaylistTagReq instance;

    private MeRecPlaylistTagReq() {
    }

    public static MeRecPlaylistTagReq getInstance() {
        if (instance == null) instance = new MeRecPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API = "https://www.missevan.com/malbum/recommand";

    /**
     * 推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        int c = Tags.recPlaylistMap.length;
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

                if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                Tags.recPlaylistTag.get(name)[3] = id;
            }
        }
    }
}
