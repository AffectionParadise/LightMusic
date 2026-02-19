package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

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
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList?cmd=rcm_keyword_playlist&user=0" +
            "&prod=kwplayer_pc_9.0.5.0&vipver=9.0.5.0&source=kwplayer_pc_9.0.5.0&loginUid=0&loginSid=0&appUid=76039576";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KW_API)
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
                Tags.hotPlaylistTags.get(name)[5] = id;
            }
        }
    }
}
