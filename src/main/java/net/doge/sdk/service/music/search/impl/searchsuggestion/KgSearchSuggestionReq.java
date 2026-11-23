package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.*;

public class KgSearchSuggestionReq {
    private static KgSearchSuggestionReq instance;

    private KgSearchSuggestionReq() {
    }

    public static KgSearchSuggestionReq getInstance() {
        if (instance == null) instance = new KgSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (酷狗)
    private final String SEARCH_SUGGESTION_KG_API = "http://msearchcdn.kugou.com/new/app/i/search.php";
    // 搜索建议 V2 API (酷狗)
    private final String SEARCH_SUGGESTION_KG_API_V2 = "/v2/getSearchTip";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("cmd", 302);
        params.put("keyword", keyword);
        String searchSuggestionBody = HttpRequest.get(SEARCH_SUGGESTION_KG_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONArray data = searchSuggestionJson.getJSONArray("data");
        for (int i = 0, len = data.size(); i < len; i++) {
            JSONObject keywordJson = data.getJSONObject(i);
            r.add(keywordJson.getString("keyword"));
        }
        return r;
    }

    /**
     * 获取搜索建议(V2)
     *
     * @return
     */
    public List<String> getSearchSuggestionV2(String keyword) {
        List<String> r = new LinkedList<>();

        Map<String, Object> params = new TreeMap<>();
        params.put("keyword", keyword);
        params.put("AlbumTipCount", 10);
        params.put("CorrectTipCount", 10);
        params.put("MVTipCount", 10);
        params.put("MusicTipCount", 10);
        params.put("radiotip", 1);
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidGet(SEARCH_SUGGESTION_KG_API_V2);
        String searchSuggestionBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "searchtip.kugou.com")
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONArray data = searchSuggestionJson.getJSONArray("data");
        JSONObject songKeywordJson = JsonUtil.findObject(data, "LableName", "");
        if (JsonUtil.notEmpty(songKeywordJson)) {
            JSONArray keywordArray = songKeywordJson.getJSONArray("RecordDatas");
            for (int i = 0, len = keywordArray.size(); i < len; i++) {
                JSONObject keywordJson = keywordArray.getJSONObject(i);
                r.add(keywordJson.getString("HintInfo"));
            }
        }
        return r;
    }
}
