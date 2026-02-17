package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class KwSearchSuggestionReq {
    private static KwSearchSuggestionReq instance;

    private KwSearchSuggestionReq() {
    }

    public static KwSearchSuggestionReq getInstance() {
        if (instance == null) instance = new KwSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (酷我)
    private final String SEARCH_SUGGESTION_KW_API = "https://kuwo.cn/openapi/v1/www/search/searchKey?key=%s&httpsStatus=1";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_SUGGESTION_KW_API, encodedKeyword)).execute();
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
