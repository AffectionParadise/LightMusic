package net.doge.sdk.service.music.tag.impl.newsongtag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KgReqBuilder;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;

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
    // 主题歌单标签 API (酷狗)
    private final String THEME_LIST_TAG_KG_API = "/v2/getthemelist";

    /**
     * 风格标签
     *
     * @return
     */
    public void initNewSongTag() {
        int c = Tags.newSongIndices.length;
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(STYLE_KG_API);
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
                Tags.newSongTags.get(name)[TagModule.STYLE_SONG_KG] = id;
            }
        }
    }

    /**
     * 主题歌单标签
     *
     * @return
     */
    public void initThemePlaylistTag() {
        int c = Tags.newSongIndices.length;
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(THEME_LIST_TAG_KG_API);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        String dat = String.format("{\"platform\":\"android\",\"clientver\":%s,\"clienttime\":%s,\"area_code\":1,\"module_id\":1,\"userid\":%s}",
                KgReqBuilder.clientver, ct, KgReqBuilder.userid);
        String tagBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "everydayrec.service.kugou.com")
                .executeAsStr();
        JSONObject tagJson = JSONObject.parseObject(tagBody);
        JSONArray tagArray = tagJson.getJSONObject("data").getJSONArray("theme_list");
        for (int i = 0, s = tagArray.size(); i < s; i++) {
            JSONObject tag = tagArray.getJSONObject(i);

            String name = tag.getString("title");
            String id = tag.getString("id");

            if (!Tags.newSongTags.containsKey(name)) Tags.newSongTags.put(name, new String[c]);
            Tags.newSongTags.get(name)[TagModule.THEME_PLAYLIST_SONG_KG] = id;
        }
    }
}
