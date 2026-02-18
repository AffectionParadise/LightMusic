package net.doge.sdk.service.radio.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;

import java.util.Map;

public class NcHotRadioTagReq {
    private static NcHotRadioTagReq instance;

    private NcHotRadioTagReq() {
    }

    public static NcHotRadioTagReq getInstance() {
        if (instance == null) instance = new NcHotRadioTagReq();
        return instance;
    }

    // 分类热门电台标签 API (网易云)
    private final String HOT_RADIO_TAG_NC_API = "https://music.163.com/weapi/djradio/home/category/recommend";
    // 分类推荐电台标签 API (网易云)
    private final String RECOMMEND_RADIO_TAG_NC_API = "https://music.163.com/weapi/djradio/category/get";

    /**
     * 分类热门电台标签
     *
     * @return
     */
    public void initHotRadioTag() {
        int c = Tags.radioMap.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioTagBody = SdkCommon.ncRequest(Method.POST, HOT_RADIO_TAG_NC_API, "{}", options)
                .executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONArray tags = radioTagJson.getJSONArray("data");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("categoryName");
            String id = tagJson.getString("categoryId");

            if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
            Tags.radioTag.get(name)[0] = id;
        }
    }

    /**
     * 分类推荐电台标签
     *
     * @return
     */
    public void initRecRadioTag() {
        int c = Tags.radioMap.length;
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioTagBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_RADIO_TAG_NC_API, "{}", options)
                .executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONArray tags = radioTagJson.getJSONArray("categories");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("name");
            String id = tagJson.getString("id");

            if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
            Tags.radioTag.get(name)[1] = id;
        }
    }
}
