package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.util.LinkedList;
import java.util.List;

public class KwHotSearchReq {
    private static KwHotSearchReq instance;

    private KwHotSearchReq() {
    }

    public static KwHotSearchReq getInstance() {
        if (instance == null) instance = new KwHotSearchReq();
        return instance;
    }

    // 热搜 API (酷我)
    private final String HOT_SEARCH_KW_API
            = "http://hotword.kuwo.cn/hotword.s?prod=kwplayer_ar_9.3.0.1&corp=kuwo&newver=2&vipver=9.3.0.1&source=kwplayer_ar_9.3.0.1_40.apk" +
            "&p2p=1&notrace=0&uid=0&plat=kwplayer_ar&rformat=json&encoding=utf8&tabid=1";
    private final String HOT_SEARCH_KW_WEB_API = "https://kuwo.cn/api/www/search/searchKey?key=&httpsStatus=1";

    /**
     * 桌面端热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        HttpResponse resp = HttpRequest.get(HOT_SEARCH_KW_API).execute();
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

        HttpResponse resp = SdkCommon.kwRequest(HOT_SEARCH_KW_WEB_API).execute();
        if (resp.isSuccessful()) {
            JSONArray hotkeys = JSONObject.parseObject(resp.body()).getJSONArray("data");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.getString(i));
            }
        }
        return r;
    }
}
