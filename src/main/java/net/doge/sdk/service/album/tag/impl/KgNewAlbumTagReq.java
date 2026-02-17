package net.doge.sdk.service.album.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.util.core.RegexUtil;

import java.util.Map;

public class KgNewAlbumTagReq {
    private static KgNewAlbumTagReq instance;

    private KgNewAlbumTagReq() {
    }

    public static KgNewAlbumTagReq getInstance() {
        if (instance == null) instance = new KgNewAlbumTagReq();
        return instance;
    }

    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";

    /**
     * 编辑精选标签
     */
    public void initIpTag() {
        int c = Tags.newAlbumMap.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
        String tagBody = SdkCommon.kgRequest(null, null, options)
                .header("x-router", "yuekucategory.kugou.com")
                .executeAsStr();
        JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
            String name = tag.getString("name");

            if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
            Tags.newAlbumTag.get(name)[4] = id;
        }
    }
}
