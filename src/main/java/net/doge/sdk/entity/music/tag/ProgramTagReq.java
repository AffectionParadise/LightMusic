package net.doge.sdk.entity.music.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.Tags;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProgramTagReq {
    // 探索节目标签 API (猫耳)
    private final String EXP_PROGRAM_TAG_ME_API = "https://www.missevan.com/explore";
    // 首页子标签 API (猫耳)
    private final String PROGRAM_SUB_TAG_ME_API = "https://www.missevan.com";

    /**
     * 加载节目标签
     *
     * @return
     */
    public void initProgramTag() {
        // 猫耳 猫耳
        Tags.programTag.put("默认", new String[]{"", ""});

        final int c = 2;
        // 猫耳
        // 猫耳探索
        Runnable initExpProgramTagMe = () -> {
            String playlistTagBody = HttpRequest.get(EXP_PROGRAM_TAG_ME_API)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);

            Elements tags = doc.select(".explore-tag");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element t = tags.get(i);

                String id = t.attr("data-tagid");
                String name = t.getElementsByTag("a").text().trim();

                if (!Tags.programTag.containsKey(name)) Tags.programTag.put(name, new String[c]);
                Tags.programTag.get(name)[0] = id;
            }
        };
        // 首页标签
        Runnable initProgramIndexTagMe = () -> {
            String radioTagBody = HttpRequest.get(PROGRAM_SUB_TAG_ME_API)
                    .execute()
                    .body();
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

                if (!Tags.programTag.containsKey(name)) Tags.programTag.put(name, new String[c]);
                Tags.programTag.get(name)[1] = id;

                // 子标签
                Elements subTags = tag.select(".vw-topcatalog-subitem-container.fc-topcatalog-subitem-container a");
                for (int j = 0, size = subTags.size(); j < size; j++) {
                    Element subTag = subTags.get(j);

                    String subName = String.format("%s - %s", name, subTag.attr("title"));
                    String subId = subTag.attr("href").replaceFirst("/sound/m/", "");

                    if (!Tags.programTag.containsKey(subName)) Tags.programTag.put(subName, new String[c]);
                    Tags.programTag.get(subName)[1] = subId;
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initExpProgramTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initProgramIndexTagMe));

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
