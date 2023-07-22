package net.doge.sdk.entity.music.tag;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NewSongTagReq {
    // 曲风 API
    private final String STYLE_API = SdkCommon.PREFIX + "/style/list";
    // 歌曲标签 API (5sing)
    private final String SONG_TAG_API_FS = "http://5sing.kugou.com/yc/list";

    /**
     * 加载新歌标签
     *
     * @return
     */
    public void initNewSongTag() {
        // 网易云 网易云 酷狗 QQ 音乐磁场 咕咕咕音乐 5sing
        Tags.newSongTag.put("默认", new String[]{"0", "", "1", "5", "", "", " "});

        Tags.newSongTag.put("华语", new String[]{"7", "", "1", "5", "forum-1", "forum-1", ""});
        Tags.newSongTag.put("内地", new String[]{"", "", "", "1", "", "", ""});
        Tags.newSongTag.put("港台", new String[]{"", "", "", "6", "", "", ""});
        Tags.newSongTag.put("欧美", new String[]{"96", "", "2", "2", "forum-10", "forum-3", ""});
        Tags.newSongTag.put("韩国", new String[]{"16", "", "4", "4", "", "", ""});
        Tags.newSongTag.put("日本", new String[]{"8", "", "5", "3", "", "", ""});
        Tags.newSongTag.put("日韩", new String[]{"", "", "3", "", "forum-15", "forum-7", ""});

        // 音乐磁场
        Tags.newSongTag.put("Remix", new String[]{"", "", "", "", "forum-11", "", ""});
        Tags.newSongTag.put("纯音乐", new String[]{"", "", "", "", "forum-12", "", ""});
        Tags.newSongTag.put("异次元", new String[]{"", "", "", "", "forum-13", "", ""});
        Tags.newSongTag.put("特供", new String[]{"", "", "", "", "forum-17", "", ""});
        Tags.newSongTag.put("百科", new String[]{"", "", "", "", "forum-18", "", ""});
        Tags.newSongTag.put("站务", new String[]{"", "", "", "", "forum-9", "", ""});

        // 咕咕咕音乐
        Tags.newSongTag.put("音乐分享区", new String[]{"", "", "", "", "", "forum-12", ""});
        Tags.newSongTag.put("伤感", new String[]{"", "", "", "", "", "forum-8", ""});
        Tags.newSongTag.put("粤语", new String[]{"", "", "", "", "", "forum-2", ""});
        Tags.newSongTag.put("青春", new String[]{"", "", "", "", "", "forum-5", ""});
        Tags.newSongTag.put("分享", new String[]{"", "", "", "", "", "forum-11", ""});
        Tags.newSongTag.put("温柔男友音", new String[]{"", "", "", "", "", "forum-10", ""});
        Tags.newSongTag.put("DJ", new String[]{"", "", "", "", "", "forum-9", ""});

        final int c = 7;
        // 网易云曲风
        Runnable initNewSongTag = () -> {
            String tagBody = HttpRequest.get(STYLE_API)
                    .execute()
                    .body();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.newSongTag.containsKey(name)) Tags.newSongTag.put(name, new String[c]);
                Tags.newSongTag.get(name)[1] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(subTags)) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.newSongTag.containsKey(subName)) Tags.newSongTag.put(subName, new String[c]);
                    Tags.newSongTag.get(subName)[1] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (JsonUtil.isEmpty(ssTags)) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.newSongTag.containsKey(ssName)) Tags.newSongTag.put(ssName, new String[c]);
                        Tags.newSongTag.get(ssName)[1] = ssId;
                    }
                }
            }
        };

        // 5sing
        Runnable initNewSongTagFs = () -> {
            String tagBody = HttpRequest.get(SONG_TAG_API_FS)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(tagBody);
            Elements dds = doc.select("dl.song_sort dd");
            // 语种
            Elements tags = dds.first().select("a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element a = tags.get(i);

                String name = a.text();
                if ("全部".equals(name)) continue;
                String id = RegexUtil.getGroup1("&l=(.*)", a.attr("href"));

                if (!Tags.newSongTag.containsKey(name)) Tags.newSongTag.put(name, new String[c]);
                Tags.newSongTag.get(name)[6] = " " + id;
            }
            // 曲风
            tags = dds.last().select("a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element a = tags.get(i);

                String name = a.text();
                if ("全部".equals(name)) continue;
                String id = RegexUtil.getGroup1("s=(.*?)&l=", a.attr("href"));

                if (!Tags.newSongTag.containsKey(name)) Tags.newSongTag.put(name, new String[c]);
                Tags.newSongTag.get(name)[6] = id + " ";
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initNewSongTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initNewSongTagFs));

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
