package net.doge.sdk.service.mv.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.Map;

public class KgMvTagReq {
    private static KgMvTagReq instance;

    private KgMvTagReq() {
    }

    public static KgMvTagReq getInstance() {
        if (instance == null) instance = new KgMvTagReq();
        return instance;
    }

    // MV 标签 API (酷狗)
    private final String MV_TAG_KG_API = "http://mobileservice.kugou.com/api/v5/video/recommend_channel?version=9108&type=2";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";

    /**
     * MV 标签
     *
     * @return
     */
    public void initMvTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.get(MV_TAG_KG_API)
                .executeAsStr();
        JSONObject mvTagJson = JSONObject.parseObject(mvTagBody);
        JSONArray tags = mvTagJson.getJSONObject("data").getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tagJson = tags.getJSONObject(i);

            String name = tagJson.getString("name");
            String id = tagJson.getString("channel_id");

            if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
            Tags.mvTag.get(name)[2] = id;
        }
    }

    /**
     * 编辑精选标签
     *
     * @return
     */
    public void initIpTag() {
        int c = Tags.mvIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
        String tagBody = SdkCommon.kgRequest(null, null, options)
                .header("x-router", "yuekucategory.kugou.com")
                .executeAsStr();
        JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
            String name = tag.getString("name");

            if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
            Tags.mvTag.get(name)[3] = id;
        }
    }
}
