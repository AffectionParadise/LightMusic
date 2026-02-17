package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcSearchSuggestionReq {
    private static NcSearchSuggestionReq instance;

    private NcSearchSuggestionReq() {
    }

    public static NcSearchSuggestionReq getInstance() {
        if (instance == null) instance = new NcSearchSuggestionReq();
        return instance;
    }

    // 搜索建议(简单) API (网易云)
    private final String SIMPLE_SEARCH_SUGGESTION_NC_API = "https://music.163.com/weapi/search/suggest/keyword";
    // 搜索建议 API (网易云)
    private final String SEARCH_SUGGESTION_NC_API = "https://music.163.com/weapi/search/suggest/web";

    /**
     * 获取搜索建议(简单)
     *
     * @return
     */
    public List<String> getSimpleSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String searchSuggestionBody = SdkCommon.ncRequest(Method.POST, SIMPLE_SEARCH_SUGGESTION_NC_API, String.format("{\"s\":\"%s\"}", keyword), options)
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONObject result = searchSuggestionJson.getJSONObject("result");
        if (JsonUtil.notEmpty(result)) {
            JSONArray searchSuggestionArray = result.getJSONArray("allMatch");
            if (JsonUtil.notEmpty(searchSuggestionArray)) {
                for (int i = 0, len = searchSuggestionArray.size(); i < len; i++) {
                    JSONObject keywordJson = searchSuggestionArray.getJSONObject(i);
                    r.add(keywordJson.getString("keyword"));
                }
            }
        }
        return r;
    }

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String searchSuggestionBody = SdkCommon.ncRequest(Method.POST, SEARCH_SUGGESTION_NC_API, String.format("{\"s\":\"%s\"}", keyword), options)
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONObject result = searchSuggestionJson.getJSONObject("result");
        if (JsonUtil.notEmpty(result)) {
            JSONArray songArray = result.getJSONArray("songs");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    r.add(songArray.getJSONObject(i).getString("name"));
                }
            }
            JSONArray artistArray = result.getJSONArray("artists");
            if (JsonUtil.notEmpty(artistArray)) {
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    r.add(artistArray.getJSONObject(i).getString("name"));
                }
            }
            JSONArray albumArray = result.getJSONArray("albums");
            if (JsonUtil.notEmpty(albumArray)) {
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    r.add(albumArray.getJSONObject(i).getString("name"));
                }
            }
        }
        return r;
    }
}
