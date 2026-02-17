package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcHotSearchReq {
    private static NcHotSearchReq instance;

    private NcHotSearchReq() {
    }

    public static NcHotSearchReq getInstance() {
        if (instance == null) instance = new NcHotSearchReq();
        return instance;
    }

    // 热搜 API (网易云)
    private final String HOT_SEARCH_NC_API = "https://music.163.com/api/search/chart/detail";

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/search/chart/detail");
        String hotSearchBody = SdkCommon.ncRequest(Method.POST, HOT_SEARCH_NC_API, "{\"id\":\"HOT_SEARCH_SONG#@#\"}", options)
                .executeAsStr();
        JSONObject hotSearchJson = JSONObject.parseObject(hotSearchBody);
        JSONObject data = hotSearchJson.getJSONObject("data");
        JSONArray hotSearchArray = data.getJSONArray("itemList");
        for (int i = 0, len = hotSearchArray.size(); i < len; i++) {
            JSONObject keywordJson = hotSearchArray.getJSONObject(i);
            r.add(keywordJson.getString("searchWord"));
        }
        return r;
    }
}
