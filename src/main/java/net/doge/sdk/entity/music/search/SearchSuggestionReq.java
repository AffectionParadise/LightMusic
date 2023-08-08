package net.doge.sdk.entity.music.search;

import cn.hutool.http.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SearchSuggestionReq {
    // 搜索建议(简单) API
    private final String SIMPLE_SEARCH_SUGGESTION_API = "https://music.163.com/weapi/search/suggest/keyword";
    // 搜索建议 API
    private final String SEARCH_SUGGESTION_API = "https://music.163.com/weapi/search/suggest/web";
    // 搜索建议 API (酷狗)
    private final String SEARCH_SUGGESTION_KG_API = "http://msearchcdn.kugou.com/new/app/i/search.php?cmd=302&keyword=%s";
    // 搜索建议 API (QQ)
    private final String SEARCH_SUGGESTION_QQ_API = "https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?is_xml=0&format=json&key=%s" +
            "&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
    // 搜索建议 API (酷我)
    private final String SEARCH_SUGGESTION_KW_API = "http://www.kuwo.cn/api/www/search/searchKey?key=%s&httpsStatus=1";
    // 搜索建议 API (千千)
    private final String SEARCH_SUGGESTION_QI_API = "https://music.91q.com/v1/search/sug?appid=16073360&timestamp=%s&type=&word=%s";

    /**
     * 获取搜索建议
     *
     * @return
     */
    public Set<String> getSearchSuggestion(String keyword) {
        Set<String> res = new LinkedHashSet<>();

        // 关键词为空时直接跳出
        if (StringUtil.isEmpty(keyword.trim())) return res;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncodeAll(keyword);

        // 网易云
        Callable<List<String>> getSimpleSearchSuggestion = () -> {
            List<String> r = new LinkedList<>();

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String searchSuggestionBody = SdkCommon.ncRequest(Method.POST, SIMPLE_SEARCH_SUGGESTION_API, String.format("{\"s\":\"%s\"}", keyword), options)
                    .executeAsync()
                    .body();
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
        };
        Callable<List<String>> getSearchSuggestion = () -> {
            List<String> r = new LinkedList<>();

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String searchSuggestionBody = SdkCommon.ncRequest(Method.POST, SEARCH_SUGGESTION_API, String.format("{\"s\":\"%s\"}", keyword), options)
                    .executeAsync()
                    .body();
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
        };

        // 酷狗
        Callable<List<String>> getSearchSuggestionKg = () -> {
            List<String> r = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_KG_API, encodedKeyword))
                    .executeAsync()
                    .body();
            JSONObject searchSuggestionJson = JSONObject.parseObject(searchSuggestionBody);
            JSONArray data = searchSuggestionJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject keywordJson = data.getJSONObject(i);
                r.add(keywordJson.getString("keyword"));
            }
            return r;
        };

        // QQ
        Callable<List<String>> getSearchSuggestionQq = () -> {
            List<String> r = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_QQ_API, encodedKeyword))
                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                    .executeAsync()
                    .body();
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
        };

        // 酷我
        Callable<List<String>> getSearchSuggestionKw = () -> {
            List<String> r = new LinkedList<>();

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_SUGGESTION_KW_API, encodedKeyword)).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject searchSuggestionJson = JSONObject.parseObject(resp.body());
                JSONArray data = searchSuggestionJson.getJSONArray("data");
                for (int i = 0, len = data.size(); i < len; i++) {
                    r.add(RegexUtil.getGroup1("RELWORD=(.*?)\r\n", data.getString(i)));
                }
            }
            return r;
        };

        // 千千
        Callable<List<String>> getSearchSuggestionQi = () -> {
            List<String> r = new LinkedList<>();

            HttpResponse resp = SdkCommon.qiRequest(String.format(SEARCH_SUGGESTION_QI_API, System.currentTimeMillis(), encodedKeyword)).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject searchSuggestionJson = JSONObject.parseObject(resp.body());
                JSONArray data = searchSuggestionJson.getJSONArray("data");
                for (int i = 0, len = data.size(); i < len; i++) {
                    r.add(data.getString(i));
                }
            }
            return r;
        };

        List<Future<List<String>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getSimpleSearchSuggestion));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestion));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionQi));

        taskList.forEach(task -> {
            try {
                res.addAll(task.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        return res;
    }
}
