package net.doge.sdk.entity.music.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.SdkCommon;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotSongTagReq {
    // 曲风 API
    private final String STYLE_API = SdkCommon.prefix + "/style/list";

    /**
     * 加载飙升歌曲标签
     *
     * @return
     */
    public void initHotSongTag() {
        Tags.hotSongTag.put("默认", new String[]{"", "index", "index"});

        // 音乐磁场
        Tags.hotSongTag.put("热门", new String[]{"", "index-0-2", "index-0-hot"});
        Tags.hotSongTag.put("月榜", new String[]{"", "index-0-3", ""});
        Tags.hotSongTag.put("周榜", new String[]{"", "index-0-4", ""});
        Tags.hotSongTag.put("日榜", new String[]{"", "index-0-5", ""});
        Tags.hotSongTag.put("华语", new String[]{"", "forum-1", "forum-1"});
        Tags.hotSongTag.put("日韩", new String[]{"", "forum-15", "forum-7"});
        Tags.hotSongTag.put("欧美", new String[]{"", "forum-10", "forum-3"});
        Tags.hotSongTag.put("Remix", new String[]{"", "forum-11", ""});
        Tags.hotSongTag.put("纯音乐", new String[]{"", "forum-12", "forum-6"});
        Tags.hotSongTag.put("异次元", new String[]{"", "forum-13", ""});
        Tags.hotSongTag.put("特供", new String[]{"", "forum-17", ""});
        Tags.hotSongTag.put("百科", new String[]{"", "forum-18", ""});
        Tags.hotSongTag.put("站务", new String[]{"", "forum-9", ""});

        // 咕咕咕音乐
        Tags.hotSongTag.put("音乐分享区", new String[]{"", "", "forum-12"});
        Tags.hotSongTag.put("伤感", new String[]{"", "", "forum-8"});
        Tags.hotSongTag.put("粤语", new String[]{"", "", "forum-2"});
        Tags.hotSongTag.put("青春", new String[]{"", "", "forum-5"});
        Tags.hotSongTag.put("分享", new String[]{"", "", "forum-11"});
        Tags.hotSongTag.put("温柔男友音", new String[]{"", "", "forum-10"});
        Tags.hotSongTag.put("DJ", new String[]{"", "", "forum-9"});

        final int c = 3;
        // 网易云曲风
        Runnable initHotSongTag = () -> {
            String tagBody = HttpRequest.get(String.format(STYLE_API))
                    .execute()
                    .body();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.hotSongTag.containsKey(name)) Tags.hotSongTag.put(name, new String[c]);
                Tags.hotSongTag.get(name)[0] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (subTags == null) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.hotSongTag.containsKey(subName)) Tags.hotSongTag.put(subName, new String[c]);
                    Tags.hotSongTag.get(subName)[0] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (ssTags == null) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.hotSongTag.containsKey(ssName)) Tags.hotSongTag.put(ssName, new String[c]);
                        Tags.hotSongTag.get(ssName)[0] = ssId;
                    }
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHotSongTag));

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
