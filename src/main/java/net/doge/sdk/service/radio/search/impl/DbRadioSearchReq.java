package net.doge.sdk.service.radio.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.RadioType;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbRadioSearchReq {
    private static DbRadioSearchReq instance;

    private DbRadioSearchReq() {
    }

    public static DbRadioSearchReq getInstance() {
        if (instance == null) instance = new DbRadioSearchReq();
        return instance;
    }

    // 关键词搜索电台 API(豆瓣)
    private final String SEARCH_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1002";
    // 关键词搜索图书电台 API(豆瓣)
    private final String SEARCH_BOOK_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1001";
    // 关键词搜索游戏电台 API(豆瓣)
    private final String SEARCH_GAME_RADIO_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=3114";

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        final int lim = Math.min(20, limit);
        String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("items");
        if (JsonUtil.notEmpty(radioArray)) {
            int to = radioInfoJson.getIntValue("total");
            t = PageUtil.totalPage(to, lim) * limit;
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
                radioInfo.setSource(NetResourceSource.DB);
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
    }

    /**
     * 豆瓣图书
     */
    public CommonResult<NetRadioInfo> searchBookRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        final int lim = Math.min(20, limit);
        String radioInfoBody = HttpRequest.get(String.format(SEARCH_BOOK_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("items");
        if (JsonUtil.notEmpty(radioArray)) {
            int to = radioInfoJson.getIntValue("total");
            t = PageUtil.totalPage(to, lim) * limit;
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
                radioInfo.setSource(NetResourceSource.DB);
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
    }

    /**
     * 豆瓣游戏
     */
    public CommonResult<NetRadioInfo> searchGameRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        final int lim = Math.min(20, limit);
        String radioInfoBody = HttpRequest.get(String.format(SEARCH_GAME_RADIO_DB_API, encodedKeyword, (page - 1) * lim))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("items");
        if (JsonUtil.notEmpty(radioArray)) {
            int to = radioInfoJson.getIntValue("total");
            t = PageUtil.totalPage(to, lim) * limit;
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
                radioInfo.setSource(NetResourceSource.DB);
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
    }
}
