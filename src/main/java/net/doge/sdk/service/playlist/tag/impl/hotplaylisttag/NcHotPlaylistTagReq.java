package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;

import java.util.Map;

public class NcHotPlaylistTagReq {
    private static NcHotPlaylistTagReq instance;

    private NcHotPlaylistTagReq() {
    }

    public static NcHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new NcHotPlaylistTagReq();
        return instance;
    }

    // 精品歌单标签 API (网易云)
    private final String HIGH_QUALITY_PLAYLIST_TAG_NC_API = "https://music.163.com/api/playlist/highquality/tags";
    // 网友精选碟标签 API (网易云)
    private final String PICKED_PLAYLIST_TAG_NC_API = "https://music.163.com/weapi/playlist/catalogue";

    /**
     * 精品歌单标签
     *
     * @return
     */
    public void initHighQualityPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistTagBody = SdkCommon.ncRequest(Method.POST, HIGH_QUALITY_PLAYLIST_TAG_NC_API, "{}", options)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONArray("tags");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("name");

            if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
            Tags.hotPlaylistTag.get(name)[0] = name;
        }
    }

    /**
     * 网友精选碟标签
     *
     * @return
     */
    public void initPickedPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistTagBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_TAG_NC_API, "{}", options)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONArray tags = playlistTagJson.getJSONArray("sub");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("name");

            if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
            Tags.hotPlaylistTag.get(name)[1] = name;
        }
    }
}
