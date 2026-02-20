package net.doge.sdk.service.playlist.tag.impl.recplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.util.Map;

public class NcRecPlaylistTagReq {
    private static NcRecPlaylistTagReq instance;

    private NcRecPlaylistTagReq() {
    }

    public static NcRecPlaylistTagReq getInstance() {
        if (instance == null) instance = new NcRecPlaylistTagReq();
        return instance;
    }

    // 曲风 API (网易云)
    private final String STYLE_NC_API = "https://music.163.com/api/tag/list/get";

    /**
     * 网易云曲风
     *
     * @return
     */
    public void initRecPlaylistTag() {
        int c = Tags.recPlaylistIndices.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_NC_API, "{}", options)
                .executeAsStr();
        JSONObject tagJson = JSONObject.parseObject(tagBody);
        JSONArray tags = tagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String name = tag.getString("tagName");
            String id = tag.getString("tagId");

            if (!Tags.recPlaylistTags.containsKey(name)) Tags.recPlaylistTags.put(name, new String[c]);
            Tags.recPlaylistTags.get(name)[TagType.STYLE_PLAYLIST_NC] = id;
            // 子标签
            JSONArray subTags = tag.getJSONArray("childrenTags");
            if (JsonUtil.isEmpty(subTags)) continue;
            for (int j = 0, s = subTags.size(); j < s; j++) {
                JSONObject subTag = subTags.getJSONObject(j);

                String subName = subTag.getString("tagName");
                String subId = subTag.getString("tagId");

                if (!Tags.recPlaylistTags.containsKey(subName)) Tags.recPlaylistTags.put(subName, new String[c]);
                Tags.recPlaylistTags.get(subName)[TagType.STYLE_PLAYLIST_NC] = subId;
                // 孙子标签
                JSONArray ssTags = subTag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(ssTags)) continue;
                for (int k = 0, l = ssTags.size(); k < l; k++) {
                    JSONObject ssTag = ssTags.getJSONObject(k);

                    String ssName = ssTag.getString("tagName");
                    String ssId = ssTag.getString("tagId");

                    if (!Tags.recPlaylistTags.containsKey(ssName)) Tags.recPlaylistTags.put(ssName, new String[c]);
                    Tags.recPlaylistTags.get(ssName)[TagType.STYLE_PLAYLIST_NC] = ssId;
                }
            }
        }
    }
}
