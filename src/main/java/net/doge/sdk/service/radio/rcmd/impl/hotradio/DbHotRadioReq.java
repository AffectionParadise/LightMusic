package net.doge.sdk.service.radio.rcmd.impl.hotradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.RadioType;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbHotRadioReq {
    private static DbHotRadioReq instance;

    private DbHotRadioReq() {
    }

    public static DbHotRadioReq getInstance() {
        if (instance == null) instance = new DbHotRadioReq();
        return instance;
    }

    // Top 250 电台 API (豆瓣)
    private final String TOP_RADIO_DB_API = "https://movie.douban.com/top250";
    // 分类电台 API (豆瓣)
    private final String CAT_RADIO_DB_API = "https://movie.douban.com/j/chart/top_list";
    // 分类电台总数 API (豆瓣)
    private final String CAT_RADIO_TOTAL_DB_API = "https://movie.douban.com/j/chart/top_list_count";
    // 分类游戏电台 API (豆瓣)
    private final String CAT_GAME_RADIO_DB_API = "https://www.douban.com/j/ilmen/game/search";

    /**
     * Top 250
     */
    public CommonResult<NetRadioInfo> getTopRadios(int page) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        final int rn = 25;
        Map<String, Object> params = new HashMap<>();
        params.put("start", (page - 1) * rn);
        params.put("filter", "");
        String radioInfoBody = HttpRequest.get(TOP_RADIO_DB_API)
                .queryParams(params)
                .executeAsStr();
        Document doc = Jsoup.parse(radioInfoBody);
        Elements rs = doc.select(".item");
        String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
        t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
        t -= t / rn * 5;
        for (int i = 0, len = rs.size(); i < len; i++) {
            Element radio = rs.get(i);
            Elements a = radio.select(".hd a");
            Elements p = radio.select(".bd p");
            Elements img = radio.select(".pic img");

            String radioId = RegexUtil.getGroup1("/subject/(\\d+)/", a.attr("href"));
            String radioName = a.text().trim();
            String dj = RegexUtil.getGroup1("导演: (.*?) ", p.text());
            String coverImgThumbUrl = img.attr("src");
            String category = "电影";

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
        return new CommonResult<>(r, t);
    }

    /**
     * 分类电台
     */
    public CommonResult<NetRadioInfo> getCatRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagModule.CAT_RADIO_DB];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("type", param);
            params.put("interval_id", "100:90");
            params.put("action", "");
            params.put("start", (page - 1) * limit);
            params.put("limit", limit);
            String radioInfoBody = HttpRequest.get(CAT_RADIO_DB_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONArray radioArray = JSONArray.parseArray(radioInfoBody);
            Map<String, Object> catRadioTotalParams = new HashMap<>();
            catRadioTotalParams.put("type", param);
            catRadioTotalParams.put("interval_id", "100:90");
            t = JSONObject.parseObject(HttpRequest.get(CAT_RADIO_TOTAL_DB_API)
                    .queryParams(catRadioTotalParams)
                    .executeAsStr()).getIntValue("total");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("title");
                String dj = JsonUtil.joinString(radioJson.getJSONArray("actors"));
                String coverImgThumbUrl = radioJson.getString("cover_url");
                String category = JsonUtil.joinString(radioJson.getJSONArray("types"));

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
     * 分类游戏电台
     */
    public CommonResult<NetRadioInfo> getCatGameRadios(String tag, int page) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagModule.CAT_GAME_RADIO_DB];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            Map<String, Object> params = new HashMap<>();
            params.put("genres", sp[0]);
            params.put("platforms", sp[1]);
            params.put("more", page);
            params.put("sort", "rating");
            String radioInfoBody = HttpRequest.get(CAT_GAME_RADIO_DB_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = data.getJSONArray("games");
            t = data.getIntValue("total");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getString("platforms");
                String coverImgThumbUrl = radioJson.getString("cover");
                String category = radioJson.getString("genres");

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
