package net.doge.sdk.service.radio.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

public class MeHotRadioTagReq {
    private static MeHotRadioTagReq instance;

    private MeHotRadioTagReq() {
    }

    public static MeHotRadioTagReq getInstance() {
        if (instance == null) instance = new MeHotRadioTagReq();
        return instance;
    }

    // 广播剧标签 API (猫耳)
    private final String RADIO_TAG_ME_API = "https://www.missevan.com/dramaapi/tag";

    /**
     * 广播剧标签
     *
     * @return
     */
    public void initRadioTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(RADIO_TAG_ME_API)
                .executeAsStr();
        JSONObject radioTagJson = JSONObject.parseObject(radioTagBody);
        JSONObject tags = radioTagJson.getJSONObject("info");
        final String[] cats = {"integrity", "age", "tags"};
        for (int i = 0, len = cats.length; i < len; i++) {
            JSONArray tagArray = tags.getJSONArray(cats[i]);
            if (JsonUtil.isEmpty(tagArray)) continue;
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
                if (i == 0) id = String.format("%s 0 0", id);
                else if (i == 1) id = String.format("0 %s 0", id);
                else id = String.format("0 0 %s", id);
                Tags.radioTags.get(name)[5] = id;
            }
        }
    }
}
