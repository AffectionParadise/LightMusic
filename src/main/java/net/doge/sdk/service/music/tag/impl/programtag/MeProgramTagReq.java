package net.doge.sdk.service.music.tag.impl.programtag;

import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MeProgramTagReq {
    private static MeProgramTagReq instance;

    private MeProgramTagReq() {
    }

    public static MeProgramTagReq getInstance() {
        if (instance == null) instance = new MeProgramTagReq();
        return instance;
    }

    // 探索节目标签 API (猫耳)
    private final String EXP_PROGRAM_TAG_ME_API = "https://www.missevan.com/explore";
    // 首页子标签 API (猫耳)
    private final String PROGRAM_SUB_TAG_ME_API = "https://www.missevan.com";

    /**
     * 猫耳探索
     *
     * @return
     */
    public void initExpProgramTag() {
        int c = Tags.programIndices.length;
        String playlistTagBody = HttpRequest.get(EXP_PROGRAM_TAG_ME_API)
                .executeAsStr();
        Document doc = Jsoup.parse(playlistTagBody);

        Elements tags = doc.select(".explore-tag");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element t = tags.get(i);

            String id = t.attr("data-tagid");
            String name = t.getElementsByTag("a").text().trim();

            if (!Tags.programTags.containsKey(name)) Tags.programTags.put(name, new String[c]);
            Tags.programTags.get(name)[0] = id;
        }
    }

    /**
     * 首页标签
     *
     * @return
     */
    public void initProgramIndexTag() {
        int c = Tags.programIndices.length;
        String radioTagBody = HttpRequest.get(PROGRAM_SUB_TAG_ME_API)
                .executeAsStr();
        Document doc = Jsoup.parse(radioTagBody);

        // 大标签
        Elements tags = doc.select(".vw-topcatalog-item.fc-topcatalog-item");
        for (int i = 0, len = tags.size(); i < len; i++) {
            Element tag = tags.get(i);
            Element t = tag.getElementsByTag("a").first();

            String name = t.attr("title");
            String href = t.attr("href");
            // 排除广播剧标签
            if (href.contains("drama")) continue;
            String id = href.replaceFirst("/sound/m/", "");

            if (!Tags.programTags.containsKey(name)) Tags.programTags.put(name, new String[c]);
            Tags.programTags.get(name)[1] = id;

            // 子标签
            Elements subTags = tag.select(".vw-topcatalog-subitem-container.fc-topcatalog-subitem-container a");
            for (int j = 0, size = subTags.size(); j < size; j++) {
                Element subTag = subTags.get(j);

                String subName = String.format("%s - %s", name, subTag.attr("title"));
                String subId = subTag.attr("href").replaceFirst("/sound/m/", "");

                if (!Tags.programTags.containsKey(subName)) Tags.programTags.put(subName, new String[c]);
                Tags.programTags.get(subName)[1] = subId;
            }
        }
    }
}
