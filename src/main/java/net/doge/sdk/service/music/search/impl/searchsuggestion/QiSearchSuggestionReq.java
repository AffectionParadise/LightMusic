package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QiSearchSuggestionReq {
    private static QiSearchSuggestionReq instance;

    private QiSearchSuggestionReq() {
    }

    public static QiSearchSuggestionReq getInstance() {
        if (instance == null) instance = new QiSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (千千)
    private final String SEARCH_SUGGESTION_QI_API = "https://music.91q.com/v1/search/sug";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new TreeMap<>();
        params.put("type", "");
        params.put("word", keyword);
        HttpResponse resp = SdkCommon.qiRequest(SEARCH_SUGGESTION_QI_API, params).execute();
        if (resp.isSuccessful()) {
            JSONObject searchSuggestionJson = JSONObject.parseObject(resp.body());
            JSONArray data = searchSuggestionJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                r.add(data.getString(i));
            }
        }
        return r;
    }
}
