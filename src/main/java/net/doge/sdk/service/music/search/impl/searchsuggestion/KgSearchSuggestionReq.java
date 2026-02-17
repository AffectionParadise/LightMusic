package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class KgSearchSuggestionReq {
    private static KgSearchSuggestionReq instance;

    private KgSearchSuggestionReq() {
    }

    public static KgSearchSuggestionReq getInstance() {
        if (instance == null) instance = new KgSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (酷狗)
    private final String SEARCH_SUGGESTION_KG_API = "http://msearchcdn.kugou.com/new/app/i/search.php?cmd=302&keyword=%s";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_KG_API, encodedKeyword))
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONArray data = searchSuggestionJson.getJSONArray("data");
        for (int i = 0, len = data.size(); i < len; i++) {
            JSONObject keywordJson = data.getJSONObject(i);
            r.add(keywordJson.getString("keyword"));
        }
        return r;
    }
}
