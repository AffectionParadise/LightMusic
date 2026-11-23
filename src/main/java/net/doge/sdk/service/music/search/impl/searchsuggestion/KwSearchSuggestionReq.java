package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpResponse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwSearchSuggestionReq {
    private static KwSearchSuggestionReq instance;

    private KwSearchSuggestionReq() {
    }

    public static KwSearchSuggestionReq getInstance() {
        if (instance == null) instance = new KwSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (酷我)
    private final String SEARCH_SUGGESTION_KW_API = "https://kuwo.cn/openapi/v1/www/search/searchKey";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("key", keyword);
        params.put("httpsStatus", 1);
        HttpResponse resp = SdkCommon.kwRequest(SEARCH_SUGGESTION_KW_API)
                .queryParams(params)
                .execute();
        if (resp.isSuccessful()) {
            JSONObject searchSuggestionJson = JSONObject.parseObject(resp.body());
            JSONArray data = searchSuggestionJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                r.add(RegexUtil.getGroup1("RELWORD=(.*?)\r\n", data.getString(i)));
            }
        }
        return r;
    }
}
