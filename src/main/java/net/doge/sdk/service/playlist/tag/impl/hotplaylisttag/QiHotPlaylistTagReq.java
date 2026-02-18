package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;

public class QiHotPlaylistTagReq {
    private static QiHotPlaylistTagReq instance;

    private QiHotPlaylistTagReq() {
    }

    public static QiHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new QiHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (千千)
    private final String PLAYLIST_TAG_QI_API = "https://music.91q.com/v1/tracklist/category?appid=16073360&timestamp=%s";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistMap.length;
        String playlistTagBody = SdkCommon.qiRequest(String.format(PLAYLIST_TAG_QI_API, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONArray tagArray = tags.getJSONObject(i).getJSONArray("subCate");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("categoryName");
                String id = tagJson.getString("id");

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[7] = id;
            }
        }
    }
}
