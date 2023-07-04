package net.doge.sdk.entity.radio.tag;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.SdkCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotRadioTag {
    // 分类热门电台标签 API
    private final String HOT_RADIO_TAG_API = SdkCommon.prefix + "/dj/category/recommend";
    // 分类推荐电台标签 API
    private final String REC_RADIO_TAG_API = SdkCommon.prefix + "/dj/catelist";
    // 电台分类标签 API (喜马拉雅)
    private final String RADIO_TAG_XM_API = "https://www.ximalaya.com/revision/category/allCategoryInfo";
    // 排行榜标签 API (喜马拉雅)
    private final String RADIO_RANKING_TAG_XM_API = "https://www.ximalaya.com/revision/rank/v3/cluster";
    // 广播剧标签 API (猫耳)
    private final String RADIO_TAG_ME_API = "https://www.missevan.com/dramaapi/tag";
    // 电台标签 API (豆瓣)
    private final String RADIO_TAG_DB_API = "https://movie.douban.com/chart";
    // 游戏电台标签 API (豆瓣)
    private final String GAME_RADIO_TAG_DB_API = "https://www.douban.com/game/explore";

    /**
     * 加载电台标签
     *
     * @return
     */
    public void initRadioTag() {
        // 网易云 网易云 喜马拉雅 喜马拉雅 喜马拉雅 猫耳 豆瓣 豆瓣
        Tags.radioTag.put("默认", new String[]{"", "", "", "", "", "0 0 0", "", " "});

        // 喜马拉雅频道
        Tags.radioTag.put("小说", new String[]{"", "", "", "", "7", "", "", ""});
        Tags.radioTag.put("儿童", new String[]{"", "", "", "", "11", "", "", ""});
        Tags.radioTag.put("相声小品", new String[]{"", "", "", "", "9", "", "", ""});
        Tags.radioTag.put("评书", new String[]{"", "", "", "", "10", "", "", ""});
        Tags.radioTag.put("娱乐", new String[]{"", "", "", "", "13", "", "", ""});
        Tags.radioTag.put("悬疑", new String[]{"", "", "", "", "14", "", "", ""});
        Tags.radioTag.put("人文", new String[]{"", "", "", "", "17", "", "", ""});
        Tags.radioTag.put("国学", new String[]{"", "", "", "", "18", "", "", ""});
        Tags.radioTag.put("头条", new String[]{"", "", "", "", "24", "", "", ""});
        Tags.radioTag.put("音乐", new String[]{"", "", "", "", "19", "", "", ""});
        Tags.radioTag.put("历史", new String[]{"", "", "", "", "16", "", "", ""});
        Tags.radioTag.put("情感", new String[]{"", "", "", "", "20", "", "", ""});
        Tags.radioTag.put("投资理财", new String[]{"", "", "", "", "26", "", "", ""});
        Tags.radioTag.put("个人提升", new String[]{"", "", "", "", "31", "", "", ""});
        Tags.radioTag.put("健康", new String[]{"", "", "", "", "22", "", "", ""});
        Tags.radioTag.put("生活", new String[]{"", "", "", "", "21", "", "", ""});
        Tags.radioTag.put("影视", new String[]{"", "", "", "", "15", "", "", ""});
        Tags.radioTag.put("商业管理", new String[]{"", "", "", "", "27", "", "", ""});
        Tags.radioTag.put("英语", new String[]{"", "", "", "", "29", "", "", ""});
        Tags.radioTag.put("少儿素养", new String[]{"", "", "", "", "12", "", "", ""});
        Tags.radioTag.put("科技", new String[]{"", "", "", "", "28", "", "", ""});
        Tags.radioTag.put("教育考试", new String[]{"", "", "", "", "32", "", "", ""});
        Tags.radioTag.put("体育", new String[]{"", "", "", "", "25", "", "", ""});
        Tags.radioTag.put("小语种", new String[]{"", "", "", "", "30", "", "", ""});
        Tags.radioTag.put("广播剧", new String[]{"", "", "", "", "8", "", "", ""});
        Tags.radioTag.put("汽车", new String[]{"", "", "", "", "23", "", "", ""});

        final int c = 8;
        // 网易云
        // 分类热门电台标签
        Runnable initHotRadioTag = () -> {
            String radioTagBody = HttpRequest.get(String.format(HOT_RADIO_TAG_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("categoryName");
                String id = tagJson.getString("categoryId");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[0] = id;
            }
        };
        // 分类推荐电台标签
        Runnable initRecRadioTag = () -> {
            String radioTagBody = HttpRequest.get(String.format(REC_RADIO_TAG_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONArray("categories");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[1] = id;
            }
        };

        // 喜马拉雅
        // 电台分类标签
        Runnable initRadioTagXm = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_XM_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray fTags = radioTagJson.getJSONArray("data");
            for (int i = 0, len = fTags.size(); i < len; i++) {
                JSONArray sTags = fTags.getJSONObject(i).getJSONArray("categories");
                // 大标签
                for (int j = 0, size = sTags.size(); j < size; j++) {
                    JSONObject tagJson = sTags.getJSONObject(j);

                    String name = tagJson.getString("displayName");
                    String pinyin = tagJson.getString("pinyin");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    Tags.radioTag.get(name)[3] = pinyin + " ";

                    // 子标签
                    JSONArray ssTags = tagJson.getJSONArray("subcategories");
                    for (int k = 0, s = ssTags.size(); k < s; k++) {
                        JSONObject ssTagJson = ssTags.getJSONObject(k);

                        String ssName = name + " - " + ssTagJson.getString("displayValue");
                        String ssId = String.format("%s %s", pinyin, ssTagJson.getString("code"));

                        if (!Tags.radioTag.containsKey(ssName)) Tags.radioTag.put(ssName, new String[c]);
                        Tags.radioTag.get(ssName)[3] = ssId;
                    }
                }
            }
        };
        // 排行榜标签
        Runnable initRankingTagXm = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_RANKING_TAG_XM_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray fTags = radioTagJson.getJSONObject("data").getJSONArray("clusterType");
            for (int i = 0, len = fTags.size(); i < len; i++) {
                JSONObject sJson = fTags.getJSONObject(i);
                if (sJson.getInt("rankType") != 2) continue;
                String n = sJson.getString("rankClusterTypeTitle") + " - ";
                String t = sJson.getString("rankClusterTypeId") + " ";
                JSONArray sTags = sJson.getJSONArray("rankClusterCategories");
                for (int j = 0, s = sTags.size(); j < s; j++) {
                    JSONObject tagJson = sTags.getJSONObject(j);

                    String name = n + tagJson.getString("rankClusterTitle");
                    String id = t + tagJson.getString("rankClusterId");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    Tags.radioTag.get(name)[2] = id;
                }
            }
        };

        // 猫耳
        // 广播剧标签
        Runnable initRadioTagMe = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_ME_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONObject tags = radioTagJson.getJSONObject("info");
            final String[] cats = {"integrity", "age", "tags"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    if (i == 0) id = String.format("%s 0 0", id);
                    else if (i == 1) id = String.format("0 %s 0", id);
                    else id = String.format("0 0 %s", id);
                    Tags.radioTag.get(name)[5] = id;
                }
            }
        };

        // 豆瓣
        // 分类电台标签
        Runnable initRadioTagDb = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_DB_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioTagBody);
            Elements tags = doc.select("div.types span a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = ReUtil.get("type=(\\d+)", tag.attr("href"), 1);

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[6] = id;
            }
        };
        // 分类游戏电台标签
        Runnable initGameRadioTagDb = () -> {
            String radioTagBody = HttpRequest.get(String.format(GAME_RADIO_TAG_DB_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioTagBody);
            Elements fieldset = doc.select("form.filters fieldset");
            Elements tags = fieldset.first().select("label:not(.is-active)");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.getElementsByTag("input").attr("value");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[7] = id + " ";
            }
            tags = fieldset.last().select("label:not(.is-active)");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.getElementsByTag("input").attr("value");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[7] = " " + id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHotRadioTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecRadioTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRankingTagXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagDb));
        taskList.add(GlobalExecutors.requestExecutor.submit(initGameRadioTagDb));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
