package net.doge.sdk.service.mv.tag.impl;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LzMvTagReq {
    private static LzMvTagReq instance;

    private LzMvTagReq() {
    }

    public static LzMvTagReq getInstance() {
        if (instance == null) instance = new LzMvTagReq();
        return instance;
    }

    // 视频标签 API (李志)
    private final String VIDEO_TAG_LZ_API = "https://www.lizhinb.com/ssp/";

    /**
     * 视频标签
     *
     * @return
     */
    public void initVideoTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.get(VIDEO_TAG_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(mvTagBody);
        Elements tags = doc.select(".zaxu-friendly-link-content");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element content = tags.get(i);
            Elements a = content.select("a");
            Elements n = content.select(".zaxu-friendly-link-name");

            String id = RegexUtil.getGroup1("/live-category/(.*?)/", a.attr("href"));
            String name = n.text();

            if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
            Tags.mvTag.get(name)[11] = id;
        }
    }
}
