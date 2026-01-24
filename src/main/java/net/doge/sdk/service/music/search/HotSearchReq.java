package net.doge.sdk.service.music.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotSearchReq {
    private static HotSearchReq instance;

    private HotSearchReq() {
    }

    public static HotSearchReq getInstance() {
        if (instance == null) instance = new HotSearchReq();
        return instance;
    }
    
    // 热搜 API
    private final String HOT_SEARCH_API = "https://music.163.com/api/search/chart/detail";
    // 热搜 API (酷狗)
    private final String HOT_SEARCH_KG_API = "http://gateway.kugou.com/api/v3/search/hot_tab?signature=ee44edb9d7155821412d220bcaf509dd&appid=1005&clientver=10026&plat=0";
    // 热搜 API (QQ)
    private final String HOT_SEARCH_QQ_API = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    // 热搜 API (酷我)
    private final String HOT_SEARCH_KW_API
            = "http://hotword.kuwo.cn/hotword.s?prod=kwplayer_ar_9.3.0.1&corp=kuwo&newver=2&vipver=9.3.0.1&source=kwplayer_ar_9.3.0.1_40.apk" +
            "&p2p=1&notrace=0&uid=0&plat=kwplayer_ar&rformat=json&encoding=utf8&tabid=1";
    private final String HOT_SEARCH_KW_WEB_API = "https://kuwo.cn/api/www/search/searchKey?key=&httpsStatus=1";
    // 热搜 API (咪咕)
    private final String HOT_SEARCH_MG_API = "http://jadeite.migu.cn:7090/music_search/v3/search/hotword";
    // 热搜 API (5sing)
    private final String HOT_SEARCH_FS_API = "http://search.5sing.kugou.com";

    /**
     * 获取热搜
     *
     * @return
     */
    public Set<String> getHotSearch() {
        Set<String> res = new LinkedHashSet<>();

        // 网易云
        Callable<List<String>> getHotSearch = () -> {
            List<String> r = new LinkedList<>();

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/search/chart/detail");
            String hotSearchBody = SdkCommon.ncRequest(Method.POST, HOT_SEARCH_API, "{\"id\":\"HOT_SEARCH_SONG#@#\"}", options)
                    .executeAsync()
                    .body();
            JSONObject hotSearchJson = JSONObject.parseObject(hotSearchBody);
            JSONObject data = hotSearchJson.getJSONObject("data");
            JSONArray hotSearchArray = data.getJSONArray("itemList");
            for (int i = 0, len = hotSearchArray.size(); i < len; i++) {
                JSONObject keywordJson = hotSearchArray.getJSONObject(i);
                r.add(keywordJson.getString("searchWord"));
            }
            return r;
        };

        // 酷狗
        Callable<List<String>> getHotSearchKg = () -> {
            List<String> r = new LinkedList<>();

            String hotSearchBody = HttpRequest.get(HOT_SEARCH_KG_API)
                    .header("dfid", "1ssiv93oVqMp27cirf2CvoF1")
                    .header("mid", "156798703528610303473757548878786007104")
                    .header("clienttime", "1584257267")
                    .header("x-router", "msearch.kugou.com")
                    .header("user-agent", "Android9-AndroidPhone-10020-130-0-searchrecommendprotocol-wifi")
                    .header("kg-rc", "1")
                    .executeAsync()
                    .body();
            JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("keywords");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                r.add(keywordJson.getString("keyword"));
            }
            return r;
        };

        // QQ
        Callable<List<String>> getHotSearchQq = () -> {
            List<String> r = new LinkedList<>();

            String hotSearchBody = HttpRequest.post(HOT_SEARCH_QQ_API)
                    .body("{\"comm\":{\"ct\":\"19\",\"cv\":\"1803\",\"guid\":\"0\",\"patch\":\"118\",\"psrf_access_token_expiresAt\":0," +
                            "\"psrf_qqaccess_token\":\"\",\"psrf_qqopenid\":\"\",\"psrf_qqunionid\":\"\",\"tmeAppID\":\"qqmusic\",\"tmeLoginType\":0," +
                            "\"uin\":\"0\",\"wid\":\"0\"},\"hotkey\":{\"method\":\"GetHotkeyForQQMusicPC\"," +
                            "\"module\":\"tencent_musicsoso_hotkey.HotkeyService\",\"param\":{\"search_id\":\"\",\"uin\":0}}}")
                    .executeAsync()
                    .body();
            JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("hotkey").getJSONObject("data").getJSONArray("vec_hotkey");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                r.add(keywordJson.getString("title"));
            }
            return r;
        };

        // 酷我
        // 桌面端
        Callable<List<String>> getHotSearchKw = () -> {
            List<String> r = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(HOT_SEARCH_KW_API).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONArray hotkeys = JSONObject.parseObject(resp.body()).getJSONArray("tagvalue");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    r.add(hotkeys.getJSONObject(i).getString("key"));
                }
            }
            return r;
        };
        // Web
        Callable<List<String>> getHotSearchKwWeb = () -> {
            List<String> r = new LinkedList<>();

            HttpResponse resp = SdkCommon.kwRequest(HOT_SEARCH_KW_WEB_API).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONArray hotkeys = JSONObject.parseObject(resp.body()).getJSONArray("data");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    r.add(hotkeys.getString(i));
                }
            }
            return r;
        };

        // 咪咕
        Callable<List<String>> getHotSearchMg = () -> {
            List<String> r = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(HOT_SEARCH_MG_API).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject data = JSONObject.parseObject(resp.body()).getJSONObject("data");
                JSONArray hotkeys = data.getJSONArray("hotwords").getJSONObject(0).getJSONArray("hotwordList");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    r.add(hotkeys.getJSONObject(i).getString("word"));
                }
                hotkeys = data.getJSONArray("discovery");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    r.add(hotkeys.getJSONObject(i).getString("word"));
                }
            }
            return r;
        };

        // 5sing
        Callable<List<String>> getHotSearchFs = () -> {
            List<String> r = new LinkedList<>();

            String body = HttpRequest.get(HOT_SEARCH_FS_API)
                    .executeAsync()
                    .body();
            Elements hotkeys = Jsoup.parse(body).select(".hot_search a");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                r.add(hotkeys.get(i).text());
            }
            return r;
        };

        List<Future<List<String>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearch));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKwWeb));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchFs));

        taskList.forEach(task -> {
            try {
                res.addAll(task.get());
            } catch (InterruptedException e) {
//                e.printStackTrace();
            } catch (ExecutionException e) {
//                e.printStackTrace();
            }
        });

        return res;
    }
}
