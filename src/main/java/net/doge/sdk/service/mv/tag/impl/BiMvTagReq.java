package net.doge.sdk.service.mv.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.util.core.http.HttpRequest;

public class BiMvTagReq {
    private static BiMvTagReq instance;

    private BiMvTagReq() {
    }

    public static BiMvTagReq getInstance() {
        if (instance == null) instance = new BiMvTagReq();
        return instance;
    }

    // 每周必看视频标签 API (酷狗)
    private final String POP_SERIES_TAG_BI_API = "https://api.bilibili.com/x/web-interface/popular/series/list";

    /**
     * 每周必看视频标签
     *
     * @return
     */
    public void initPopSeriesVideoTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.get(POP_SERIES_TAG_BI_API)
                .executeAsStr();
        JSONObject mvTagJson = JSONObject.parseObject(mvTagBody);
        JSONObject data = mvTagJson.getJSONObject("data");
        JSONArray tags = data.getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("subject");
            String id = tagJson.getString("number");

            if (!Tags.mvTags.containsKey(name)) Tags.mvTags.put(name, new String[c]);
            Tags.mvTags.get(name)[TagType.POP_SERIES_VIDEO_BI] = id;
        }
    }
}
