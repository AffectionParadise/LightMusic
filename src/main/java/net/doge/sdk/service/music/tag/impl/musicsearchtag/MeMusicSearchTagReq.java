package net.doge.sdk.service.music.tag.impl.musicsearchtag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.util.core.http.HttpRequest;

import java.util.Set;

public class MeMusicSearchTagReq {
    private static MeMusicSearchTagReq instance;

    private MeMusicSearchTagReq() {
    }

    public static MeMusicSearchTagReq getInstance() {
        if (instance == null) instance = new MeMusicSearchTagReq();
        return instance;
    }

    // 搜索子标签 (猫耳)
    private final String PROGRAM_SEARCH_TAG_ME_API = "https://www.missevan.com/sound/getcatalogleaves";

    /**
     * 加载节目搜索子标签
     *
     * @return
     */
    public void initProgramSearchTag() {
        int c = 1;
        String playlistTagBody = HttpRequest.get(PROGRAM_SEARCH_TAG_ME_API)
                .executeAsStr();
        JSONArray tags = JSONArray.parseArray(playlistTagBody);
        for (int i = 0, len = tags.size(); i < len; i++) {
            JSONObject son = tags.getJSONObject(i).getJSONObject("son");
            Set<String> keys = son.keySet();
            for (String key : keys) {
                JSONObject obj = son.getJSONObject(key);

                String name = obj.getString("catalog_name");
                String id = obj.getString("id");

                if (!Tags.programSearchTags.containsKey(name)) Tags.programSearchTags.put(name, new String[c]);
                Tags.programSearchTags.get(name)[0] = id;
            }
        }
    }
}
