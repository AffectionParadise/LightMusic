package net.doge.sdk.service.music.search.impl.searchsuggestion;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class QqSearchSuggestionReq {
    private static QqSearchSuggestionReq instance;

    private QqSearchSuggestionReq() {
    }

    public static QqSearchSuggestionReq getInstance() {
        if (instance == null) instance = new QqSearchSuggestionReq();
        return instance;
    }

    // 搜索建议 API (QQ)
    private final String SEARCH_SUGGESTION_QQ_API = "https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?is_xml=0&format=json&key=%s" +
            "&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public List<String> getSearchSuggestion(String keyword) {
        List<String> r = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_QQ_API, encodedKeyword))
                .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                .executeAsStr();
        JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
        JSONObject data = searchSuggestionJson.getJSONObject("data");
        if (JsonUtil.notEmpty(data)) {
            JSONArray songArray = data.getJSONObject("song").getJSONArray("itemlist");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                r.add(songArray.getJSONObject(i).getString("name"));
            }
            JSONArray artistArray = data.getJSONObject("singer").getJSONArray("itemlist");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                r.add(artistArray.getJSONObject(i).getString("name"));
            }
            JSONArray albumArray = data.getJSONObject("album").getJSONArray("itemlist");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                r.add(albumArray.getJSONObject(i).getString("name"));
            }
            JSONArray mvArray = data.getJSONObject("mv").getJSONArray("itemlist");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                r.add(mvArray.getJSONObject(i).getString("name"));
            }
        }
        return r;
    }
}
