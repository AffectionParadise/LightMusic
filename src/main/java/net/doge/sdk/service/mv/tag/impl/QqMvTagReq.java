package net.doge.sdk.service.mv.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;

public class QqMvTagReq {
    private static QqMvTagReq instance;

    private QqMvTagReq() {
    }

    public static QqMvTagReq getInstance() {
        if (instance == null) instance = new QqMvTagReq();
        return instance;
    }

    /**
     * MV 标签
     *
     * @return
     */
    public void initMvTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody("{\"comm\":{\"ct\":24},\"mv_tag\":{\"module\":\"MvService.MvInfoProServer\",\"method\":\"GetAllocTag\",\"param\":{}}}")
                .executeAsStr();
        JSONObject mvTagJson = JSONObject.parseObject(mvTagBody);
        JSONArray tags = mvTagJson.getJSONObject("mv_tag").getJSONObject("data").getJSONArray("version");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("name");
            if ("全部".equals(name)) continue;
            String id = tagJson.getString("id");

            if (!Tags.mvTags.containsKey(name)) Tags.mvTags.put(name, new String[c]);
            Tags.mvTags.get(name)[4] = "15";
            Tags.mvTags.get(name)[5] = id;
        }
    }
}
