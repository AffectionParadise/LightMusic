package net.doge.sdk.service.playlist.tag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.core.JsonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RecPlaylistTagReq {
    private static RecPlaylistTagReq instance;

    private RecPlaylistTagReq() {
    }

    public static RecPlaylistTagReq getInstance() {
        if (instance == null) instance = new RecPlaylistTagReq();
        return instance;
    }

    // 曲风 API
    private final String STYLE_API = "https://music.163.com/api/tag/list/get";
    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 歌单标签 API (QQ)
    private final String PLAYLIST_TAG_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8" +
            "&notice=0&platform=wk_v15.json&needNewCode=0&data=%7B%22tags%22%3A%7B%22method%22%3A%22get_all_categories" +
            "%22%2C%22param%22%3A%7B%22qq%22%3A%22%22%7D%2C%22module%22%3A%22playlist.PlaylistAllCategoriesServer%22%7D%7D";
    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API = "https://www.missevan.com/malbum/recommand";
    // 歌单标签 API (5sing)
    private final String PLAYLIST_TAG_FS_API = "http://5sing.kugou.com/gd/gdList";

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        // 网易云 酷狗 QQ 猫耳 5sing
        Tags.recPlaylistTag.put("默认", new String[]{"", " ", "10000000", " ", " "});

        final int c = 5;
        // 网易云曲风
        Runnable initRecPlaylistTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_API, "{}", options)
                    .executeAsStr();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                Tags.recPlaylistTag.get(name)[0] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(subTags)) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.recPlaylistTag.containsKey(subName)) Tags.recPlaylistTag.put(subName, new String[c]);
                    Tags.recPlaylistTag.get(subName)[0] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (JsonUtil.isEmpty(ssTags)) continue;
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
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KG_API)
                    .executeAsStr();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
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
                    .executeAsStr();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
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
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_ME_API)
                    .executeAsStr();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
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

        // 5sing
        Runnable initRecPlaylistTagFs = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_FS_API)
                    .executeAsStr();
            Document doc = Jsoup.parse(playlistTagBody);
            Elements tags = doc.select("ul.flx li a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.text();

                if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                Tags.recPlaylistTag.get(name)[4] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagFs));

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
