package net.doge.sdk.service.radio.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

public class MgHotRadioTagReq {
    private static MgHotRadioTagReq instance;

    private MgHotRadioTagReq() {
    }

    public static MgHotRadioTagReq getInstance() {
        if (instance == null) instance = new MgHotRadioTagReq();
        return instance;
    }

    // 分类电台标签 API (咪咕)
    private final String HOT_RADIO_TAG_MG_API = "https://app.c.nf.migu.cn/pc/bmw/page-data/music-radio/v1.0?templateVersion=1";

    /**
     * 分类电台标签
     *
     * @return
     */
    public void initHotRadioTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(HOT_RADIO_TAG_MG_API).executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONObject data = radioTagJson.getJSONObject("data");
        JSONArray contents = data.getJSONArray("contents");
        JSONArray subContents = SdkUtil.findFeatureObj(contents, "title", "分类电台").getJSONArray("contents");
        for (int i = 0, len = subContents.size(); i < len; i++) {
            JSONObject tagJson = subContents.getJSONObject(i);
            if (!tagJson.containsKey("columnId")) continue;
            JSONObject tag = tagJson.getJSONArray("contents").getJSONObject(0);

            String name = tag.getString("txt");
            String id = tag.getString("txt2");

            if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
            Tags.radioTag.get(name)[8] = id;
        }
    }
}
