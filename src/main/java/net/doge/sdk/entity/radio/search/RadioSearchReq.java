package net.doge.sdk.entity.radio.search;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.model.RadioType;
import net.doge.model.entity.NetRadioInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RadioSearchReq {
    private static RadioSearchReq instance;

    private RadioSearchReq() {
    }

    public static RadioSearchReq getInstance() {
        if (instance == null) instance = new RadioSearchReq();
        return instance;
    }
    
    // 关键词搜索电台 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    // 关键词搜索电台 API(喜马拉雅)
    private final String SEARCH_RADIO_XM_API
            = "https://www.ximalaya.com/revision/search/main?core=album&kw=%s&page=%s&spellchecker=true&rows=%s&condition=relation&device=iPhone&fq=&paidFilter=false";
    // 关键词搜索电台 API(猫耳)
    private final String SEARCH_RADIO_ME_API = "https://www.missevan.com/dramaapi/search?s=%s&page=%s&page_size=%s";
    // 关键词搜索电台 API(豆瓣)
    private final String SEARCH_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1002";
    // 关键词搜索图书电台 API(豆瓣)
    private final String SEARCH_BOOK_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1001";
    // 关键词搜索游戏电台 API(豆瓣)
    private final String SEARCH_GAME_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=3114";

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(int src, String keyword, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> res = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncodeAll(keyword);

        // 网易云
        Callable<CommonResult<NetRadioInfo>> searchRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1009,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject result = radioInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("djRadiosCount");
                JSONArray radioArray = result.getJSONArray("djRadios");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);
                    JSONObject djJson = radioJson.getJSONObject("dj");

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = djJson.getString("nickname");
                    String djId = djJson.getString("userId");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getIntValue("programCount");
                    String category = radioJson.getString("category");
                    if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
                    String coverImgThumbUrl = radioJson.getString("picUrl");
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetRadioInfo>> searchRadiosXm = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_XM_API, encodedKeyword, page, limit))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data").getJSONObject("album");
            t = data.getIntValue("total");
            JSONArray radioArray = data.getJSONArray("docs");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("albumId");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getString("nickname");
                String djId = radioJson.getString("uid");
                String coverImgThumbUrl = radioJson.getString("coverPath").replaceFirst("http:", "https:");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("tracksCount");
                String category = radioJson.getString("categoryTitle");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        Callable<CommonResult<NetRadioInfo>> searchRadiosMe = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_ME_API, encodedKeyword, page, limit))
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            t = data.getJSONObject("pagination").getIntValue("count");
            JSONArray radioArray = data.getJSONArray("Datas");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                Long playCount = radioJson.getLong("view_count");
                String category = radioJson.getString("catalog_name");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setPlayCount(playCount);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 豆瓣
        Callable<CommonResult<NetRadioInfo>> searchRadiosDb = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            if (JsonUtil.notEmpty(radioArray)) {
                int to = radioInfoJson.getIntValue("total");
                t = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    Document doc = Jsoup.parse(radioArray.getString(i));
                    Elements result = doc.select(".result");
                    Elements a = result.select("h3 a");
                    Elements span = result.select(".title h3 span");

                    String radioId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                    String radioName = a.text().trim();
                    String dj = result.select("span.subject-cast").text();
                    String coverImgThumbUrl = result.select(".pic img").attr("src");
                    String category = RegexUtil.getGroup1("\\[(.*?)\\]", span.text());

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 豆瓣图书
        Callable<CommonResult<NetRadioInfo>> searchBookRadiosDb = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            String radioInfoBody = HttpRequest.get(String.format(SEARCH_BOOK_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            if (JsonUtil.notEmpty(radioArray)) {
                int to = radioInfoJson.getIntValue("total");
                t = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    Document doc = Jsoup.parse(radioArray.getString(i));
                    Elements result = doc.select(".result");
                    Elements a = result.select("h3 a");
                    Elements span = result.select(".title h3 span");

                    String radioId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                    String radioName = a.text().trim();
                    String dj = result.select("span.subject-cast").text();
                    String coverImgThumbUrl = result.select(".pic img").attr("src");
                    String category = RegexUtil.getGroup1("\\[(.*?)\\]", span.text());

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setType(RadioType.BOOK);
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 豆瓣游戏
        Callable<CommonResult<NetRadioInfo>> searchGameRadiosDb = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            String radioInfoBody = HttpRequest.get(String.format(SEARCH_GAME_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            if (JsonUtil.notEmpty(radioArray)) {
                int to = radioInfoJson.getIntValue("total");
                t = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    Document doc = Jsoup.parse(radioArray.getString(i));
                    Elements result = doc.select(".result");
                    Elements a = result.select("h3 a");
                    Elements span = result.select(".title h3 span");

                    String radioId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                    String radioName = a.text().trim();
                    String dj = result.select("span.subject-cast").text();
                    String coverImgThumbUrl = result.select(".pic img").attr("src");
                    String category = RegexUtil.getGroup1("\\[(.*?)\\]", span.text());

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setType(RadioType.GAME);
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchRadios));
        if (src == NetMusicSource.XM || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosXm));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosMe));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosDb));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchBookRadiosDb));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchGameRadiosDb));

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get());
    }
}
