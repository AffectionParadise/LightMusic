package net.doge.sdk.service.playlist.tag.impl.recplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.SdkCommon;

public class QqRecPlaylistTagReq {
    private static QqRecPlaylistTagReq instance;

    private QqRecPlaylistTagReq() {
    }

    public static QqRecPlaylistTagReq getInstance() {
        if (instance == null) instance = new QqRecPlaylistTagReq();
        return instance;
    }

    /**
     * 推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        int c = Tags.recPlaylistIndices.length;
        String data = "{\"tags\":{\"method\":\"get_all_categories\",\"param\":{\"qq\":\"\"},\"module\":\"playlist.PlaylistAllCategoriesServer\"}}";
        String playlistTagBody = SdkCommon.qqPlaylistRequest(data)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONObject("tags").getJSONObject("data").getJSONArray("v_group");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONArray tagArray = tags.getJSONObject(i).getJSONArray("v_item");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.recPlaylistTags.containsKey(name)) Tags.recPlaylistTags.put(name, new String[c]);
                Tags.recPlaylistTags.get(name)[TagModule.NEW_PLAYLIST_QQ] = id;
            }
        }
    }
}
