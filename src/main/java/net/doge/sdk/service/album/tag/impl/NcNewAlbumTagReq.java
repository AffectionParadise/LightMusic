package net.doge.sdk.service.album.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.util.Map;

public class NcNewAlbumTagReq {
    private static NcNewAlbumTagReq instance;

    private NcNewAlbumTagReq() {
    }

    public static NcNewAlbumTagReq getInstance() {
        if (instance == null) instance = new NcNewAlbumTagReq();
        return instance;
    }

    // 曲风 API (网易云)
    private final String STYLE_NC_API = "https://music.163.com/api/tag/list/get";

    /**
     * 网易云曲风标签
     */
    public void initAlbumTag() {
        int c = Tags.newAlbumIndices.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_NC_API, "{}", options)
                .executeAsStr();
        JSONObject tagJson = JSONObject.parseObject(tagBody);
        JSONArray tags = tagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String name = tag.getString("tagName");
            String id = tag.getString("tagId");

            if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
            Tags.newAlbumTag.get(name)[2] = id;
            // 子标签
            JSONArray subTags = tag.getJSONArray("childrenTags");
            if (JsonUtil.isEmpty(subTags)) continue;
            for (int j = 0, s = subTags.size(); j < s; j++) {
                JSONObject subTag = subTags.getJSONObject(j);

                String subName = subTag.getString("tagName");
                String subId = subTag.getString("tagId");

                if (!Tags.newAlbumTag.containsKey(subName)) Tags.newAlbumTag.put(subName, new String[c]);
                Tags.newAlbumTag.get(subName)[2] = subId;
                // 孙子标签
                JSONArray ssTags = subTag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(ssTags)) continue;
                for (int k = 0, l = ssTags.size(); k < l; k++) {
                    JSONObject ssTag = ssTags.getJSONObject(k);

                    String ssName = ssTag.getString("tagName");
                    String ssId = ssTag.getString("tagId");

                    if (!Tags.newAlbumTag.containsKey(ssName)) Tags.newAlbumTag.put(ssName, new String[c]);
                    Tags.newAlbumTag.get(ssName)[2] = ssId;
                }
            }
        }
    }
}
