package net.doge.sdk.service.radio.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

public class XmHotRadioTagReq {
    private static XmHotRadioTagReq instance;

    private XmHotRadioTagReq() {
    }

    public static XmHotRadioTagReq getInstance() {
        if (instance == null) instance = new XmHotRadioTagReq();
        return instance;
    }

    // 电台分类标签 API (喜马拉雅)
    private final String RADIO_TAG_XM_API = "https://www.ximalaya.com/revision/category/allCategoryInfo";
    // 排行榜标签 API (喜马拉雅)
    private final String RADIO_RANK_TAG_XM_API = "https://www.ximalaya.com/revision/rank/v3/cluster";

    /**
     * 电台分类标签
     *
     * @return
     */
    public void initRadioTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(RADIO_TAG_XM_API)
                .executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONArray fTags = radioTagJson.getJSONArray("data");
        for (int i = 0, len = fTags.size(); i < len; i++) {
            JSONArray sTags = fTags.getJSONObject(i).getJSONArray("categories");
            // 大标签
            for (int j = 0, size = sTags.size(); j < size; j++) {
                JSONObject tagJson = sTags.getJSONObject(j);

                String name = tagJson.getString("displayName");
                String pinyin = tagJson.getString("pinyin");

                if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
                Tags.radioTags.get(name)[3] = pinyin + " ";

                // 子标签
                JSONArray ssTags = tagJson.getJSONArray("subcategories");
                for (int k = 0, s = ssTags.size(); k < s; k++) {
                    JSONObject ssTagJson = ssTags.getJSONObject(k);

                    String ssName = name + " - " + ssTagJson.getString("displayValue");
                    String ssId = String.format("%s %s", pinyin, ssTagJson.getString("code"));

                    if (!Tags.radioTags.containsKey(ssName)) Tags.radioTags.put(ssName, new String[c]);
                    Tags.radioTags.get(ssName)[3] = ssId;
                }
            }
        }
    }

    /**
     * 排行榜标签
     *
     * @return
     */
    public void initRankTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(RADIO_RANK_TAG_XM_API)
                .executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONArray fTags = radioTagJson.getJSONObject("data").getJSONArray("clusterType");
        for (int i = 0, len = fTags.size(); i < len; i++) {
            JSONObject sJson = fTags.getJSONObject(i);
            if (sJson.getIntValue("rankType") != 2) continue;
            String n = sJson.getString("rankClusterTypeTitle") + " - ";
            String t = sJson.getString("rankClusterTypeId") + " ";
            JSONArray sTags = sJson.getJSONArray("rankClusterCategories");
            for (int j = 0, s = sTags.size(); j < s; j++) {
                JSONObject tagJson = sTags.getJSONObject(j);

                String name = n + tagJson.getString("rankClusterTitle");
                String id = t + tagJson.getString("rankClusterId");

                if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
                Tags.radioTags.get(name)[2] = id;
            }
        }
    }
}
