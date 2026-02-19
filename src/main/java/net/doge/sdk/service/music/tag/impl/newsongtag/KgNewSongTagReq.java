package net.doge.sdk.service.music.tag.impl.newsongtag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;

import java.util.Map;
import java.util.TreeMap;

public class KgNewSongTagReq {
    private static KgNewSongTagReq instance;

    private KgNewSongTagReq() {
    }

    public static KgNewSongTagReq getInstance() {
        if (instance == null) instance = new KgNewSongTagReq();
        return instance;
    }

    // 风格标签 API (酷狗)
    private final String STYLE_KG_API = "/everydayrec.service/everyday_style_recommend";

    /**
     * 风格标签
     *
     * @return
     */
    public void initNewSongTag() {
        int c = Tags.newSongIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(STYLE_KG_API);
        Map<String, Object> params = new TreeMap<>();
        params.put("tagids", "");
        String tagBody = SdkCommon.kgRequest(params, "{}", options)
                .executeAsStr();
        JSONObject tagJson = JSONObject.parseObject(tagBody);
        JSONArray tagArray = tagJson.getJSONObject("data").getJSONArray("tag_info");
        for (int i = 0, s = tagArray.size(); i < s; i++) {
            JSONArray child = tagArray.getJSONObject(i).getJSONArray("child");
            for (int j = 0, l = child.size(); j < l; j++) {
                JSONObject tag = child.getJSONObject(j);

                String name = tag.getString("name");
                String id = tag.getString("id");

                if (!Tags.newSongTags.containsKey(name)) Tags.newSongTags.put(name, new String[c]);
                Tags.newSongTags.get(name)[3] = id;
            }
        }
    }
}
