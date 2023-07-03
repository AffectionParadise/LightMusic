package net.doge.sdk.playlist.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.Tags;
import net.doge.sdk.common.SdkCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RecPlaylistTagReq {
    // 曲风 API
    private final String STYLE_API = SdkCommon.prefix + "/style/list";
    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 歌单标签 API (QQ)
    private final String PLAYLIST_TAG_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8" +
            "&notice=0&platform=wk_v15.json&needNewCode=0&data=%7B%22tags%22%3A%7B%22method%22%3A%22get_all_categories" +
            "%22%2C%22param%22%3A%7B%22qq%22%3A%22%22%7D%2C%22module%22%3A%22playlist.PlaylistAllCategoriesServer%22%7D%7D";
    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API
            = "https://www.missevan.com/malbum/recommand";

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        // 网易云 酷狗 QQ 猫耳
        Tags.recPlaylistTag.put("默认", new String[]{"", " ", "10000000", " "});

        final int c = 4;
        // 网易云曲风
        Runnable initRecPlaylistTag = () -> {
            String tagBody = HttpRequest.get(String.format(STYLE_API))
                    .execute()
                    .body();
            JSONObject tagJson = JSONObject.fromObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                Tags.recPlaylistTag.get(name)[0] = id;
                // 子标签
                JSONArray subTags = tag.optJSONArray("childrenTags");
                if (subTags == null) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.recPlaylistTag.containsKey(subName)) Tags.recPlaylistTag.put(subName, new String[c]);
                    Tags.recPlaylistTag.get(subName)[0] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.optJSONArray("childrenTags");
                    if (ssTags == null) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.recPlaylistTag.containsKey(ssName)) Tags.recPlaylistTag.put(ssName, new String[c]);
                        Tags.recPlaylistTag.get(ssName)[0] = ssId;
                    }
                }
            }
        };

        // 酷狗
        Runnable initRecPlaylistTagKg = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_KG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
            final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[1] = id;
                }
            }
        };

        // QQ
        Runnable initRecPlaylistTagQq = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_QQ_API)
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONObject("tags").getJSONObject("data").getJSONArray("v_group");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("v_item");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[2] = id;
                }
            }
        };

        // 猫耳
        Runnable initRecPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_ME_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tags = playlistTagJson.getJSONObject("info");
            final String[] cats = new String[]{"主题", "场景", "情感"};
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONArray(j);

                    String name = tagJsonArray.getString(1);
                    String id = tagJsonArray.getString(0);

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[3] = id;
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagMe));

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
