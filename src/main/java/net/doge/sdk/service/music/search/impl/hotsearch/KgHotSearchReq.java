package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class KgHotSearchReq {
    private static KgHotSearchReq instance;

    private KgHotSearchReq() {
    }

    public static KgHotSearchReq getInstance() {
        if (instance == null) instance = new KgHotSearchReq();
        return instance;
    }

    // 热搜 API (酷狗)
    private final String HOT_SEARCH_KG_API = "http://gateway.kugou.com/api/v3/search/hot_tab?signature=ee44edb9d7155821412d220bcaf509dd&appid=1005&clientver=10026&plat=0";

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        String hotSearchBody = HttpRequest.get(HOT_SEARCH_KG_API)
                .header("dfid", "1ssiv93oVqMp27cirf2CvoF1")
                .header("mid", "156798703528610303473757548878786007104")
                .header("clienttime", "1584257267")
                .header("x-router", "msearch.kugou.com")
                .header("user-agent", "Android9-AndroidPhone-10020-130-0-searchrecommendprotocol-wifi")
                .header("kg-rc", "1")
                .executeAsStr();
        JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("keywords");
        for (int i = 0, len = hotkeys.size(); i < len; i++) {
            JSONObject keywordJson = hotkeys.getJSONObject(i);
            r.add(keywordJson.getString("keyword"));
        }
        return r;
    }
}
