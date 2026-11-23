package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwHotSearchReq {
    private static KwHotSearchReq instance;

    private KwHotSearchReq() {
    }

    public static KwHotSearchReq getInstance() {
        if (instance == null) instance = new KwHotSearchReq();
        return instance;
    }

    // 热搜 API (酷我)
    private final String HOT_SEARCH_KW_API = "http://hotword.kuwo.cn/hotword.s";
    private final String HOT_SEARCH_KW_WEB_API = "https://kuwo.cn/api/www/search/searchKey";

    /**
     * 桌面端热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("prod", "kwplayer_ar_9.3.0.1");
        params.put("corp", "kuwo");
        params.put("newver", 2);
        params.put("vipver", "9.3.0.1");
        params.put("source", "kwplayer_ar_9.3.0.1_40.apk");
        params.put("p2p", 1);
        params.put("notrace", 0);
        params.put("uid", 0);
        params.put("plat", "kwplayer_ar");
        params.put("rformat", "json");
        params.put("encoding", "utf8");
        params.put("tabid", 1);
        HttpResponse resp = HttpRequest.get(HOT_SEARCH_KW_API)
                .queryParams(params)
                .execute();
        if (resp.isSuccessful()) {
            JSONArray hotkeys = JSONObject.parseObject(resp.body()).getJSONArray("tagvalue");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.getJSONObject(i).getString("key"));
            }
        }
        return r;
    }

    /**
     * Web 端热搜
     *
     * @return
     */
    public List<String> getHotSearchWeb() {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("key", "");
        params.put("httpsStatus", 1);
        HttpResponse resp = SdkCommon.kwRequest(HOT_SEARCH_KW_WEB_API)
                .queryParams(params)
                .execute();
        if (resp.isSuccessful()) {
            JSONArray hotkeys = JSONObject.parseObject(resp.body()).getJSONArray("data");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.getString(i));
            }
        }
        return r;
    }
}
