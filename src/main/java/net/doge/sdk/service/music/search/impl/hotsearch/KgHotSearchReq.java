package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgHotSearchReq {
    private static KgHotSearchReq instance;

    private KgHotSearchReq() {
    }

    public static KgHotSearchReq getInstance() {
        if (instance == null) instance = new KgHotSearchReq();
        return instance;
    }

    // 热搜 API (酷狗)
    private final String HOT_SEARCH_KG_API = "http://gateway.kugou.com/api/v3/search/hot_tab";
    // 默认搜索词 API (酷狗)
    private final String DEFAULT_SEARCH_KG_API = "/searchnofocus/v1/search_no_focus_word";

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new TreeMap<>();
        params.put("signature", "ee44edb9d7155821412d220bcaf509dd");
        params.put("appid", 1005);
        params.put("clientver", 10026);
        params.put("plat", 0);
        String hotSearchBody = HttpRequest.get(HOT_SEARCH_KG_API)
                .queryParams(params)
                .userAgent("Android9-AndroidPhone-10020-130-0-searchrecommendprotocol-wifi")
                .header("dfid", "1ssiv93oVqMp27cirf2CvoF1")
                .header("mid", "156798703528610303473757548878786007104")
                .header("clienttime", "1584257267")
                .header("x-router", "msearch.kugou.com")
                .header("kg-rc", "1")
                .executeAsStr();
        JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("keywords");
        for (int i = 0, len = hotkeys.size(); i < len; i++) {
            JSONObject keywordJson = hotkeys.getJSONObject(i);
            r.add(keywordJson.getString("keyword"));
        }
        return r;
    }

    /**
     * 获取默认搜索词
     *
     * @return
     */
    public List<String> getDefaultSearch() {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new TreeMap<>();
        params.put("clientver", 12329);
        String dat = "{\"plat\":0,\"userid\":0,\"tags\":\"{}\",\"vip_type\":65530,\"m_type\":0,\"own_ads\":{},\"ability\":\"3\",\"sources\":[],\"bitmap\":2,\"mode\":\"normal\"}";
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(DEFAULT_SEARCH_KG_API);
        String hotSearchBody = SdkCommon.kgRequest(params, dat, options)
                .executeAsStr();
        JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("data").getJSONArray("ads");
        for (int i = 0, len = hotkeys.size(); i < len; i++) {
            JSONObject keywordJson = hotkeys.getJSONObject(i);
            r.add(keywordJson.getString("title"));
        }
        return r;
    }
}
