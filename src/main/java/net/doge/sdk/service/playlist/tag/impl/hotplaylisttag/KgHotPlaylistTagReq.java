package net.doge.sdk.service.playlist.tag.impl.hotplaylisttag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.Map;

public class KgHotPlaylistTagReq {
    private static KgHotPlaylistTagReq instance;

    private KgHotPlaylistTagReq() {
    }

    public static KgHotPlaylistTagReq getInstance() {
        if (instance == null) instance = new KgHotPlaylistTagReq();
        return instance;
    }

    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";

    /**
     * 歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        int c = Tags.hotPlaylistIndices.length;
        String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KG_API)
                .executeAsStr();
        JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
        JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
        final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
        for (int i = 0, len = cats.length; i < len; i++) {
            JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
            for (int j = 0, s = tagArray.size(); j < s; j++) {
                JSONObject tagJson = tagArray.getJSONObject(j);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.hotPlaylistTags.containsKey(name)) Tags.hotPlaylistTags.put(name, new String[c]);
                Tags.hotPlaylistTags.get(name)[2] = id;
            }
        }
    }

    /**
     * 编辑精选标签
     *
     * @return
     */
    public void initIpTag() {
        int c = Tags.hotPlaylistIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
        String tagBody = SdkCommon.kgRequest(null, null, options)
                .header("x-router", "yuekucategory.kugou.com")
                .executeAsStr();
        JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
            String name = tag.getString("name");

            if (!Tags.hotPlaylistTags.containsKey(name)) Tags.hotPlaylistTags.put(name, new String[c]);
            Tags.hotPlaylistTags.get(name)[3] = id;
        }
    }
}
