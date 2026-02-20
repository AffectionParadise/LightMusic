package net.doge.sdk.service.music.tag.impl.hotsongtag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.util.core.RegexUtil;

import java.util.Map;

public class KgHotSongTagReq {
    private static KgHotSongTagReq instance;

    private KgHotSongTagReq() {
    }

    public static KgHotSongTagReq getInstance() {
        if (instance == null) instance = new KgHotSongTagReq();
        return instance;
    }

    // 主题音乐标签 API (酷狗)
    private final String THEME_SONG_TAG_KG_API = "/everydayrec.service/v1/mul_theme_category_recommend";
    // 频道标签 API (酷狗)
    private final String FM_TAG_KG_API = "/v1/class_fm_song";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";

    /**
     * 主题音乐标签
     *
     * @return
     */
    public void initThemeSongTag() {
        int c = Tags.hotSongIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(THEME_SONG_TAG_KG_API);
        long ct = System.currentTimeMillis() / 1000;
        String dat = String.format("{\"platform\":\"android\",\"clienttime\":%s,\"userid\":0,\"module_id\":508}", ct);
        String tagBody = SdkCommon.kgRequest(null, dat, options)
                .executeAsStr();
        JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("all_theme_detail_list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String name = tag.getString("title");
            String id = tag.getString("id");

            if (!Tags.hotSongTags.containsKey(name)) Tags.hotSongTags.put(name, new String[c]);
            Tags.hotSongTags.get(name)[TagType.THEME_SONG_KG] = id;
        }
    }

    /**
     * 频道标签
     *
     * @return
     */
    public void initFmTag() {
        int c = Tags.hotSongIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(FM_TAG_KG_API);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        String dat = String.format("{\"kguid\":0,\"clienttime\":%s,\"mid\":\"%s\",\"platform\":\"android\",\"clientver\":%s," +
                        "\"uid\":%s,\"get_tracker\":1,\"key\":\"%s\",\"appid\":%s}",
                ct, KugouReqBuilder.mid, KugouReqBuilder.clientver, KugouReqBuilder.userid, KugouReqBuilder.signParamsKey(ct), KugouReqBuilder.appid);
        String tagBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "fm.service.kugou.com")
                .executeAsStr();
        JSONArray classList = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("class_list");
        for (int i = 0, len = classList.size(); i < len; i++) {
            JSONArray fmList = classList.getJSONObject(i).getJSONArray("fmlist");
            for (int j = 0, s = fmList.size(); j < s; j++) {
                JSONObject tag = fmList.getJSONObject(j);

                String fmid = tag.getString("fmid");
                String fmtype = tag.getString("fmtype");
                String name = tag.getString("fmname");

                if (!Tags.hotSongTags.containsKey(name)) Tags.hotSongTags.put(name, new String[c]);
                Tags.hotSongTags.get(name)[TagType.FM_SONG_KG] = fmid + " " + fmtype;
            }
        }
    }

    /**
     * 编辑精选标签
     *
     * @return
     */
    public void initIpTag() {
        int c = Tags.hotSongIndices.length;
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
        String tagBody = SdkCommon.kgRequest(null, null, options)
                .header("x-router", "yuekucategory.kugou.com")
                .executeAsStr();
        JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject tag = tags.getJSONObject(i);

            String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
            String name = tag.getString("name");

            if (!Tags.hotSongTags.containsKey(name)) Tags.hotSongTags.put(name, new String[c]);
            Tags.hotSongTags.get(name)[TagType.IP_SONG_KG] = id;
        }
    }
}
