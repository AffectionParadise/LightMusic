package net.doge.sdk.service.radio.tag.impl;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DbHotRadioTagReq {
    private static DbHotRadioTagReq instance;

    private DbHotRadioTagReq() {
    }

    public static DbHotRadioTagReq getInstance() {
        if (instance == null) instance = new DbHotRadioTagReq();
        return instance;
    }

    // 电台标签 API (豆瓣)
    private final String RADIO_TAG_DB_API = "https://movie.douban.com/chart";
    // 游戏电台标签 API (豆瓣)
    private final String GAME_RADIO_TAG_DB_API = "https://www.douban.com/game/explore";

    /**
     * 分类电台标签
     *
     * @return
     */
    public void initRadioTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(RADIO_TAG_DB_API)
                .executeAsStr();
        Document doc = Jsoup.parse(radioTagBody);
        Elements tags = doc.select(".types span a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = RegexUtil.getGroup1("type=(\\d+)", tag.attr("href"));

            if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
            Tags.radioTags.get(name)[6] = id;
        }
    }

    /**
     * 分类游戏电台标签
     *
     * @return
     */
    public void initGameRadioTag() {
        int c = Tags.radioIndices.length;
        String radioTagBody = HttpRequest.get(GAME_RADIO_TAG_DB_API)
                .executeAsStr();
        Document doc = Jsoup.parse(radioTagBody);
        Elements fieldset = doc.select("form.filters fieldset");
        Elements tags = fieldset.first().select("label:not(.is-active)");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = tag.getElementsByTag("input").attr("value");

            if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
            Tags.radioTags.get(name)[7] = id + " ";
        }
        tags = fieldset.last().select("label:not(.is-active)");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);

            String name = tag.text();
            String id = tag.getElementsByTag("input").attr("value");

            if (!Tags.radioTags.containsKey(name)) Tags.radioTags.put(name, new String[c]);
            Tags.radioTags.get(name)[7] = " " + id;
        }
    }
}
