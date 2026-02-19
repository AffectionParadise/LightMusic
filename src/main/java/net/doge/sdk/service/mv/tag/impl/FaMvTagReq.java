package net.doge.sdk.service.mv.tag.impl;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FaMvTagReq {
    private static FaMvTagReq instance;

    private FaMvTagReq() {
    }

    public static FaMvTagReq getInstance() {
        if (instance == null) instance = new FaMvTagReq();
        return instance;
    }

    // 视频标签 API (发姐)
    private final String VIDEO_TAG_FA_API = "https://www.chatcyf.com/video/";
    // 直播标签 API (发姐)
    private final String LIVE_TAG_FA_API = "https://www.chatcyf.com/teaparty/";

    /**
     * 视频标签
     *
     * @return
     */
    public void initVideoTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.get(VIDEO_TAG_FA_API)
                .executeAsStr();
        Document doc = Jsoup.parse(mvTagBody);
        Elements tags = doc.select(".filter-item .filter a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element a = tags.get(i);

            String id = RegexUtil.getGroup1("c2=(\\d+)", a.attr("href"));
            if (StringUtil.isEmpty(id)) continue;
            String name = a.text();

            if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
            Tags.mvTag.get(name)[10] = id + " ";
        }
    }

    /**
     * 直播标签
     *
     * @return
     */
    public void initLiveTag() {
        int c = Tags.mvIndices.length;
        String mvTagBody = HttpRequest.get(LIVE_TAG_FA_API)
                .executeAsStr();
        Document doc = Jsoup.parse(mvTagBody);
        Elements tags = doc.select(".filter-item .filter a");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element a = tags.get(i);

            String id = RegexUtil.getGroup1("c2=(\\d+)", a.attr("href"));
            if (StringUtil.isEmpty(id)) continue;
            String name = a.text();

            if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
            Tags.mvTag.get(name)[10] = " " + id;
        }
    }
}
