package net.doge.sdk.service.radio.rcmd.impl.hotradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.RadioType;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DbHotRadioReq {
    private static DbHotRadioReq instance;

    private DbHotRadioReq() {
    }

    public static DbHotRadioReq getInstance() {
        if (instance == null) instance = new DbHotRadioReq();
        return instance;
    }

    // Top 250 电台 API (豆瓣)
    private final String TOP_RADIO_DB_API = "https://movie.douban.com/top250?start=%s&filter=";
    // 分类电台 API (豆瓣)
    private final String CAT_RADIO_DB_API = "https://movie.douban.com/j/chart/top_list?type=%s&interval_id=100:90&action=&start=%s&limit=%s";
    // 分类电台总数 API (豆瓣)
    private final String CAT_RADIO_TOTAL_DB_API = "https://movie.douban.com/j/chart/top_list_count?type=%s&interval_id=100:90";
    // 分类游戏电台 API (豆瓣)
    private final String CAT_GAME_RADIO_DB_API = "https://www.douban.com/j/ilmen/game/search?genres=%s&platforms=%s&more=%s&sort=rating";

    /**
     * Top 250
     */
    public CommonResult<NetRadioInfo> getTopRadios(int page) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        final int rn = 25;
        String radioInfoBody = HttpRequest.get(String.format(TOP_RADIO_DB_API, (page - 1) * rn))
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
        return new CommonResult<>(r, t);
    }

    /**
     * 分类电台
     */
    public CommonResult<NetRadioInfo> getCatRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        if (StringUtil.notEmpty(s[6])) {
            String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_DB_API, s[6], (page - 1) * limit, limit))
                    .executeAsStr();
            JSONArray radioArray = JSONArray.parseArray(radioInfoBody);
            t = JSONObject.parseObject(HttpRequest.get(String.format(CAT_RADIO_TOTAL_DB_API, s[6])).executeAsStr()).getIntValue("total");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("title");
                String dj = SdkUtil.joinString(radioJson.getJSONArray("actors"));
                String coverImgThumbUrl = radioJson.getString("cover_url");
                String category = SdkUtil.joinString(radioJson.getJSONArray("types"));

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
    }

    /**
     * 分类游戏电台
     */
    public CommonResult<NetRadioInfo> getCatGameRadios(String tag, int page) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        if (StringUtil.notEmpty(s[7])) {
            String[] sp = s[7].split(" ", -1);
            String radioInfoBody = HttpRequest.get(String.format(CAT_GAME_RADIO_DB_API, sp[0], sp[1], page))
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
    }
}
