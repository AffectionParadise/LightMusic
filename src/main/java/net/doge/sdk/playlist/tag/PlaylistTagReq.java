package net.doge.sdk.playlist.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.utils.StringUtil;
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

public class PlaylistTagReq {
    // 精品歌单标签 API
    private final String HIGH_QUALITY_PLAYLIST_TAG_API
            = SdkCommon.prefix + "/playlist/highquality/tags";
    // 网友精选碟标签 API
    private final String PICKED_PLAYLIST_TAG_API
            = SdkCommon.prefix + "/playlist/catlist";
    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 歌单标签 API (QQ)
    private final String PLAYLIST_TAG_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8" +
            "&notice=0&platform=wk_v15.json&needNewCode=0&data=%7B%22tags%22%3A%7B%22method%22%3A%22get_all_categories" +
            "%22%2C%22param%22%3A%7B%22qq%22%3A%22%22%7D%2C%22module%22%3A%22playlist.PlaylistAllCategoriesServer%22%7D%7D";
    // 歌单标签 API (酷我)
    private final String PLAYLIST_TAG_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList?cmd=rcm_keyword_playlist&user=0" +
            "&prod=kwplayer_pc_9.0.5.0&vipver=9.0.5.0&source=kwplayer_pc_9.0.5.0&loginUid=0&loginSid=0&appUid=76039576";
    // 歌单标签 API (咪咕)
    private final String PLAYLIST_TAG_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-taglist/release";
    // 歌单标签 API (千千)
    private final String PLAYLIST_TAG_QI_API
            = "https://music.91q.com/v1/tracklist/category?appid=16073360&timestamp=%s";
    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API
            = "https://www.missevan.com/malbum/recommand";
    // 探索歌单标签 API (猫耳)
    private final String EXP_PLAYLIST_TAG_ME_API
            = "https://www.missevan.com/explore";

    /**
     * 加载歌单标签
     *
     * @return
     */
    public void initPlaylistTag() {
        // 网易云精品歌单 网易云网友精选碟 酷狗 QQ 酷我 咪咕 千千 猫耳 猫耳探索
        Tags.playlistTag.put("默认", new String[]{"全部", "全部", " ", "10000000", "", "", " ", " ", ""});

        final int c = 9;
        // 网易云
        // 精品歌单标签
        Runnable initHighQualityPlaylistTag = () -> {
            String playlistTagBody = HttpRequest.get(String.format(HIGH_QUALITY_PLAYLIST_TAG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("tags");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[0] = StringUtil.encode(name);
            }
        };
        // 网友精选碟标签
        Runnable initPickedPlaylistTag = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PICKED_PLAYLIST_TAG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("sub");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[1] = StringUtil.encode(name);
            }
        };

        // 酷狗
        Runnable initPlaylistTagKg = () -> {
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

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[2] = id;
                }
            }
        };

        // QQ
        Runnable initPlaylistTagQq = () -> {
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

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[3] = id;
                }
            }
        };

        // 酷我
        Runnable initPlaylistTagKw = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_KW_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = String.format("%s %s", tagJson.getString("id"), tagJson.getString("digest"));

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[4] = id;
                }
            }
        };

        // 咪咕
        Runnable initPlaylistTagMg = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_MG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("content");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONObject(j).getJSONArray("texts");

                    String name = tagJsonArray.getString(0);
                    String id = tagJsonArray.getString(1);

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[5] = id;
                }
            }
        };

        // 千千
        Runnable initPlaylistTagQi = () -> {
            String playlistTagBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(PLAYLIST_TAG_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("subCate");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("categoryName");
                    String id = tagJson.getString("id");

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[6] = id;
                }
            }
        };

        // 猫耳
        Runnable initPlaylistTagMe = () -> {
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

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[7] = id;
                }
            }
        };
        // 猫耳探索
        Runnable initExpPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(EXP_PLAYLIST_TAG_ME_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);

            Elements tags = doc.select(".explore-tag");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element t = tags.get(i);

                String id = t.attr("data-tagid");
                String name = t.getElementsByTag("a").text().trim();

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[8] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHighQualityPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPickedPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initExpPlaylistTagMe));

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
