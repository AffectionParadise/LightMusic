package net.doge.sdk.service.artist.tag.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;

public class QqArtistTagReq {
    private static QqArtistTagReq instance;

    private QqArtistTagReq() {
    }

    public static QqArtistTagReq getInstance() {
        if (instance == null) instance = new QqArtistTagReq();
        return instance;
    }

    /**
     * 分类歌手标签
     *
     * @return
     */
    public void initArtistTag() {
        int c = Tags.artistIndices.length;
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

            // 此处包含多个平台首字母同时初始化
            // 网易云
            Tags.artistTag.get(name)[1] = String.format("-1 -1 %s", "#".equals(name) ? "0" : String.valueOf((int) name.toUpperCase().charAt(0)));

            // 酷我
            if (!"#".equals(name)) Tags.artistTag.get(name)[7] = String.format("0 %s", name);

            // 千千
            Tags.artistTag.get(name)[9] = String.format("%s  ", "#".equals(name) ? "other" : name);

            // 猫耳
            Tags.artistTag.get(name)[10] = String.format("%s", "#".equals(name) ? "0" : String.valueOf(name.charAt(0) - 64));
        }
    }
}
