package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class KwHotPlaylistTagReq {
    private static KwHotPlaylistTagReq instance;

    private KwHotPlaylistTagReq() {
    }

    public static KwHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new KwHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (酷我)
    private final String PLAYLIST_TAG_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        Map<String, Object> params = new HashMap<>();
        params.put("cmd", "rcm_keyword_playlist");
        params.put("user", 0);
        params.put("prod", "kwplayer_pc_9.0.5.0");
        params.put("vipver", "9.0.5.0");
        params.put("source", "kwplayer_pc_9.0.5.0");
        params.put("loginUid", 0);
        params.put("loginSid", 0);
        params.put("appUid", 76039576);
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KW_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONArray tagArray = tags.getJSONObject(i).getJSONArray("data");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("name");
                String id = String.format("%s %s", tagJson.getString("id"), tagJson.getString("digest"));

                if (!Tags.hotPlaylistTags.containsKey(name)) Tags.hotPlaylistTags.put(name, new String[c]);
                Tags.hotPlaylistTags.get(name)[TagModule.CAT_PLAYLIST_KW] = id;
            }
        }
    }
}
