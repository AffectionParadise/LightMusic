package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.util.LinkedList;
import java.util.List;

public class MgHotSearchReq {
    private static MgHotSearchReq instance;

    private MgHotSearchReq() {
    }

    public static MgHotSearchReq getInstance() {
        if (instance == null) instance = new MgHotSearchReq();
        return instance;
    }

    // 热搜 API (咪咕)
    private final String HOT_SEARCH_MG_API = "http://jadeite.migu.cn:7090/music_search/v3/search/hotword";

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        HttpResponse resp = HttpRequest.get(HOT_SEARCH_MG_API).execute();
        if (resp.isSuccessful()) {
            JSONObject data = JSONObject.parseObject(resp.body()).getJSONObject("data");
            JSONArray hotkeys = data.getJSONArray("hotwords").getJSONObject(0).getJSONArray("hotwordList");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.getJSONObject(i).getString("word"));
            }
            hotkeys = data.getJSONArray("discovery");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.getJSONObject(i).getString("word"));
            }
        }
        return r;
    }
}
