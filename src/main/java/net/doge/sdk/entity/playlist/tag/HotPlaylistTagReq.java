package net.doge.sdk.entity.playlist.tag;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.common.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotPlaylistTagReq {
    private static HotPlaylistTagReq instance;

    private HotPlaylistTagReq() {
    }

    public static HotPlaylistTagReq getInstance() {
        if (instance == null) instance = new HotPlaylistTagReq();
        return instance;
    }

    // 精品歌单标签 API
    private final String HIGH_QUALITY_PLAYLIST_TAG_API = "https://music.163.com/api/playlist/highquality/tags";
    // 网友精选碟标签 API
    private final String PICKED_PLAYLIST_TAG_API = "https://music.163.com/weapi/playlist/catalogue";
    // 歌单标签 API (酷狗)
    private final String PLAYLIST_TAG_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";
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
    private final String PLAYLIST_TAG_QI_API = "https://music.91q.com/v1/tracklist/category?appid=16073360&timestamp=%s";
    // 歌单标签 API (猫耳)
    private final String PLAYLIST_TAG_ME_API = "https://www.missevan.com/malbum/recommand";
    // 探索歌单标签 API (猫耳)
    private final String EXP_PLAYLIST_TAG_ME_API = "https://www.missevan.com/explore";
    // 歌单标签 API (5sing)
    private final String PLAYLIST_TAG_FS_API = "http://5sing.kugou.com/gd/gdList";

    /**
     * 加载歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        // 网易云精品歌单 网易云网友精选碟 酷狗 酷狗 QQ 酷我 咪咕 千千 猫耳 猫耳探索 5sing
        Tags.hotPlaylistTag.put("默认", new String[]{"全部", "全部", " ", "", "10000000", "", "", " ", " ", "", " "});

        final int c = 11;
        // 网易云
        // 精品歌单标签
        Runnable initHighQualityPlaylistTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistTagBody = SdkCommon.ncRequest(Method.POST, HIGH_QUALITY_PLAYLIST_TAG_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("tags");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[0] = name;
            }
        };
        // 网友精选碟标签
        Runnable initPickedPlaylistTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistTagBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_TAG_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("sub");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[1] = name;
            }
        };

        // 酷狗
        Runnable initHotPlaylistTagKg = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KG_API)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
            final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[2] = id;
                }
            }
        };
        // 编辑精选标签
        Runnable initIpTagKg = () -> {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
            String tagBody = SdkCommon.kgRequest(null, null, options)
                    .header("x-router", "yuekucategory.kugou.com")
                    .executeAsync()
                    .body();
            JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
                String name = tag.getString("name");

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[3] = id;
            }
        };

        // QQ
        Runnable initHotPlaylistTagQq = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_QQ_API)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONObject("tags").getJSONObject("data").getJSONArray("v_group");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("v_item");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[4] = id;
                }
            }
        };

        // 酷我
        Runnable initHotPlaylistTagKw = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_KW_API)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = String.format("%s %s", tagJson.getString("id"), tagJson.getString("digest"));

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[5] = id;
                }
            }
        };

        // 咪咕
        Runnable initHotPlaylistTagMg = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_MG_API)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("content");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONObject(j).getJSONArray("texts");

                    String name = tagJsonArray.getString(0);
                    String id = tagJsonArray.getString(1);

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[6] = id;
                }
            }
        };

        // 千千
        Runnable initHotPlaylistTagQi = () -> {
            String playlistTagBody = SdkCommon.qiRequest(String.format(PLAYLIST_TAG_QI_API, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("subCate");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("categoryName");
                    String id = tagJson.getString("id");

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[7] = id;
                }
            }
        };

        // 猫耳
        Runnable initHotPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_ME_API)
                    .executeAsync()
                    .body();
            JSONObject playlistTagJson = JSONObject.parseObject(playlistTagBody);
            JSONObject tags = playlistTagJson.getJSONObject("info");
            final String[] cats = new String[]{"主题", "场景", "情感"};
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONArray(j);

                    String name = tagJsonArray.getString(1);
                    String id = tagJsonArray.getString(0);

                    if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                    Tags.hotPlaylistTag.get(name)[8] = id;
                }
            }
        };
        // 猫耳探索
        Runnable initExpPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(EXP_PLAYLIST_TAG_ME_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);

            Elements tags = doc.select(".explore-tag");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element t = tags.get(i);

                String id = t.attr("data-tagid");
                String name = t.getElementsByTag("a").text().trim();

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[9] = id;
            }
        };

        // 5sing
        Runnable initHotPlaylistTagFs = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_FS_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);
            Elements tags = doc.select("ul.flx li a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.text();

                if (!Tags.hotPlaylistTag.containsKey(name)) Tags.hotPlaylistTag.put(name, new String[c]);
                Tags.hotPlaylistTag.get(name)[10] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHighQualityPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPickedPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initIpTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initExpPlaylistTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initHotPlaylistTagFs));

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
