package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class MgSearchSuggestionReq {
    private static MgSearchSuggestionReq instance;

    private MgSearchSuggestionReq() {
    }

    public static MgSearchSuggestionReq getInstance() {
        if (instance == null) instance = new MgSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (咪咕)
    private final String SEARCH_SUGGESTION_MG_API = "https://app.u.nf.migu.cn/pc/resource/content/tone_search_suggest/v1.0?text=%s";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_SUGGESTION_MG_API, encodedKeyword)).execute();
        if (resp.isSuccessful()) {
            JSONObject searchSuggestionJson = JSONObject.parseObject(resp.body());
            JSONArray data = searchSuggestionJson.getJSONObject("data").getJSONArray("songList");
            for (int i = 0, len = data.size(); i < len; i++) {
                r.add(data.getJSONObject(i).getString("songName"));
            }
        }
        return r;
    }
}
