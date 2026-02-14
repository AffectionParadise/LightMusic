package net.doge.sdk.service.artist.tag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.ExceptionUtil;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.http.HttpRequest;
import net.doge.util.http.constant.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class ArtistTagReq {
    private static ArtistTagReq instance;

    private ArtistTagReq() {
    }

    public static ArtistTagReq getInstance() {
        if (instance == null) instance = new ArtistTagReq();
        return instance;
    }

    // 曲风 API
    private final String STYLE_API = "https://music.163.com/api/tag/list/get";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";

    /**
     * 加载歌手标签
     *
     * @return
     */
    public void initArtistTag() {
        // 网易云 网易云 网易云 酷狗 酷狗 QQ 酷我 酷我 千千 猫耳
        Tags.artistTag.put("默认", new String[]{"1", "", "", "0 0", "", "-100 -100 -100 -100", "11", "0 ", "  ", " "});

        Tags.artistTag.put("男", new String[]{"", "1 -1 -1", "", "1 0", "", "0 -100 -100 -100", "", "", "  男", ""});
        Tags.artistTag.put("女", new String[]{"", "2 -1 -1", "", "2 0", "", "1 -100 -100 -100", "", "", "  女", ""});
        Tags.artistTag.put("组合", new String[]{"", "3 -1 -1", "", "3 0", "", "2 -100 -100 -100", "16", "", "  组合", ""});
        Tags.artistTag.put("乐队", new String[]{"", "", "", "", "", "", "", "", "  乐队", ""});
        Tags.artistTag.put("华语", new String[]{"1", "-1 7 -1", "", "0 1", "", "", "11", "", "", ""});
        Tags.artistTag.put("华语男", new String[]{"", "1 7 -1", "", "1 1", "", "", "", "1 ", "", ""});
        Tags.artistTag.put("华语女", new String[]{"", "2 7 -1", "", "2 1", "", "", "", "2 ", "", ""});
        Tags.artistTag.put("华语组合", new String[]{"", "3 7 -1", "", "3 1", "", "", "", "3 ", "", ""});
        Tags.artistTag.put("内地", new String[]{"", "", "", "", "", "-100 -100 -100 200", "", "", " 内地 ", ""});
        Tags.artistTag.put("内地男", new String[]{"", "", "", "", "", "0 -100 -100 200", "", "", " 内地 男", ""});
        Tags.artistTag.put("内地女", new String[]{"", "", "", "", "", "1 -100 -100 200", "", "", " 内地 女", ""});
        Tags.artistTag.put("内地组合", new String[]{"", "", "", "", "", "2 -100 -100 200", "", "", " 内地 组合", ""});
        Tags.artistTag.put("内地乐队", new String[]{"", "", "", "", "", "", "", "", " 内地 乐队", ""});
        Tags.artistTag.put("港台", new String[]{"", "", "", "", "", "-100 -100 -100 2", "", "", " 港台 ", ""});
        Tags.artistTag.put("港台男", new String[]{"", "", "", "", "", "0 -100 -100 2", "", "", " 港台 男", ""});
        Tags.artistTag.put("港台女", new String[]{"", "", "", "", "", "1 -100 -100 2", "", "", " 港台 女", ""});
        Tags.artistTag.put("港台组合", new String[]{"", "", "", "", "", "2 -100 -100 2", "", "", " 港台 组合", ""});
        Tags.artistTag.put("港台乐队", new String[]{"", "", "", "", "", "", "", "", " 港台 乐队", ""});
        Tags.artistTag.put("欧美", new String[]{"2", "-1 96 -1", "", "0 2", "", "-100 -100 -100 5", "13", "", " 欧美 ", ""});
        Tags.artistTag.put("欧美男", new String[]{"", "1 96 -1", "", "1 2", "", "0 -100 -100 5", "", "7 ", " 欧美 男", ""});
        Tags.artistTag.put("欧美女", new String[]{"", "2 96 -1", "", "2 2", "", "1 -100 -100 5", "", "8 ", " 欧美 女", ""});
        Tags.artistTag.put("欧美组合", new String[]{"", "3 96 -1", "", "3 2", "", "2 -100 -100 5", "", "9 ", " 欧美 组合", ""});
        Tags.artistTag.put("欧美乐队", new String[]{"", "", "", "", "", "", "", "", " 欧美 乐队", ""});
        Tags.artistTag.put("韩国", new String[]{"3", "-1 16 -1", "", "0 6", "", "-100 -100 -100 3", "", "", " 韩国 ", ""});
        Tags.artistTag.put("韩国男", new String[]{"", "1 16 -1", "", "1 6", "", "0 -100 -100 3", "", "", " 韩国 男", ""});
        Tags.artistTag.put("韩国女", new String[]{"", "2 16 -1", "", "2 6", "", "1 -100 -100 3", "", "", " 韩国 女", ""});
        Tags.artistTag.put("韩国组合", new String[]{"", "3 16 -1", "", "3 6", "", "2 -100 -100 3", "", "", " 韩国 组合", ""});
        Tags.artistTag.put("韩国乐队", new String[]{"", "", "", "", "", "", "", "", " 韩国 乐队", ""});
        Tags.artistTag.put("日本", new String[]{"4", "-1 8 -1", "", "0 5", "", "-100 -100 -100 4", "", "", " 日本 ", ""});
        Tags.artistTag.put("日本男", new String[]{"", "1 8 -1", "", "1 5", "", "0 -100 -100 4", "", "", " 日本 男", ""});
        Tags.artistTag.put("日本女", new String[]{"", "2 8 -1", "", "2 5", "", "1 -100 -100 4", "", "", " 日本 女", ""});
        Tags.artistTag.put("日本组合", new String[]{"", "3 8 -1", "", "3 5", "", "2 -100 -100 4", "", "", " 日本 组合", ""});
        Tags.artistTag.put("日本乐队", new String[]{"", "", "", "", "", "", "", "", " 日本 乐队", ""});
        Tags.artistTag.put("日韩", new String[]{"", "", "", "0 3", "", "", "12", "", "", ""});
        Tags.artistTag.put("日韩男", new String[]{"", "", "", "1 3", "", "", "", "4 ", "", ""});
        Tags.artistTag.put("日韩女", new String[]{"", "", "", "2 3", "", "", "", "5 ", "", ""});
        Tags.artistTag.put("日韩组合", new String[]{"", "", "", "3 3", "", "", "", "6 ", "", ""});
        Tags.artistTag.put("其他", new String[]{"", "-1 0 -1", "", "0 4", "", "-100 -100 -100 6", "", "10 ", " 其他 ", ""});
        Tags.artistTag.put("其他男", new String[]{"", "1 0 -1", "", "1 4", "", "0 -100 -100 6", "", "", " 其他 男", ""});
        Tags.artistTag.put("其他女", new String[]{"", "2 0 -1", "", "2 4", "", "1 -100 -100 6", "", "", " 其他 女", ""});
        Tags.artistTag.put("其他组合", new String[]{"", "3 0 -1", "", "3 4", "", "2 -100 -100 6", "", "", " 其他 组合", ""});
        Tags.artistTag.put("其他乐队", new String[]{"", "", "", "", "", "", "", "", " 其他 乐队", ""});

        final int c = 10;
        // 网易云曲风
        Runnable initStyleArtistTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_API, "{}", options)
                    .executeAsStr();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[2] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(subTags)) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.artistTag.containsKey(subName)) Tags.artistTag.put(subName, new String[c]);
                    Tags.artistTag.get(subName)[2] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (JsonUtil.isEmpty(ssTags)) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.artistTag.containsKey(ssName)) Tags.artistTag.put(ssName, new String[c]);
                        Tags.artistTag.get(ssName)[2] = ssId;
                    }
                }
            }
        };

        // 酷狗
        // 编辑精选标签
        Runnable initIpTagKg = () -> {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
            String tagBody = SdkCommon.kgRequest(null, null, options)
                    .header("x-router", "yuekucategory.kugou.com")
                    .executeAsStr();
            JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
                String name = tag.getString("name");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[4] = id;
            }
        };

        // QQ + 网易云 + 千千
        // 分类歌手标签
        Runnable initArtistTagQq = () -> {
            String artistTagBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody("{\"comm\":{\"ct\":24,\"cv\":0},\"singerList\":{\"module\":\"Music.SingerListServer\",\"method\":\"get_singer_list\"," +
                            "\"param\":{\"area\":-100,\"sex\":-100,\"genre\":-100,\"index\":-100,\"sin\":0,\"cur_page\":1}}}")
                    .executeAsStr();
            JSONObject artistTagJson = JSONObject.parseObject(artistTagBody);
            JSONObject data = artistTagJson.getJSONObject("singerList").getJSONObject("data").getJSONObject("tags");
            // 流派
            JSONArray genre = data.getJSONArray("genre");
            for (int i = 0, len = genre.size(); i < len; i++) {
                JSONObject tagJson = genre.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("全部".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[5] = String.format("-100 %s -100 -100", id);
            }
            // 首字母
            JSONArray index = data.getJSONArray("index");
            for (int i = 0, len = index.size(); i < len; i++) {
                JSONObject tagJson = index.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("热门".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[5] = String.format("-100 -100 %s -100", id);

                // 网易云
                Tags.artistTag.get(name)[1] = String.format("-1 -1 %s", "#".equals(name) ? "0" : String.valueOf((int) name.toUpperCase().charAt(0)));

                // 酷我
                if (!"#".equals(name)) Tags.artistTag.get(name)[7] = String.format("0 %s", name);

                // 千千
                Tags.artistTag.get(name)[8] = String.format("%s  ", "#".equals(name) ? "other" : name);

                // 猫耳
                Tags.artistTag.get(name)[9] = String.format("%s", "#".equals(name) ? "0" : String.valueOf(name.charAt(0) - 64));
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initStyleArtistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initIpTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initArtistTagQq));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (Exception e) {
                ExceptionUtil.handleAsyncException(e);
            }
        });
    }
}
