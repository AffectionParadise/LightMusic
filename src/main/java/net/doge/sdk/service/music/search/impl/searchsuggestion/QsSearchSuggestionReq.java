package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class QsSearchSuggestionReq {
    private static QsSearchSuggestionReq instance;

    private QsSearchSuggestionReq() {
    }

    public static QsSearchSuggestionReq getInstance() {
        if (instance == null) instance = new QsSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (汽水)
    private final String SEARCH_SUGGESTION_QS_API = "https://api.qishui.com/luna/pc/sug";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        HashMap<String, Object> params = new HashMap<>();
        params.put("q", keyword);
        String searchSuggestionBody = SdkCommon.qsRequest(SEARCH_SUGGESTION_QS_API, params)
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONArray searchSuggestionArray = searchSuggestionJson.getJSONArray("sugs");
        for (int i = 0, len = searchSuggestionArray.size(); i < len; i++) {
            r.add(searchSuggestionArray.getJSONObject(i).getString("suggestion"));
        }
        return r;
    }
}
