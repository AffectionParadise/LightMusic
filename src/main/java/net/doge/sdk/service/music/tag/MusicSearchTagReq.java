package net.doge.sdk.service.music.tag;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MusicSearchTagReq {
    private static MusicSearchTagReq instance;

    private MusicSearchTagReq() {
    }

    public static MusicSearchTagReq getInstance() {
        if (instance == null) instance = new MusicSearchTagReq();
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
        // 猫耳
        Tags.programSearchTag.put("默认", new String[]{" "});

        final int c = 1;
        // 猫耳
        Runnable initProgramSearchTagMe = () -> {
            String playlistTagBody = HttpRequest.get(PROGRAM_SEARCH_TAG_ME_API)
                    .executeAsync()
                    .body();
            JSONArray tags = JSONArray.parseArray(playlistTagBody);
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject son = tags.getJSONObject(i).getJSONObject("son");
                Set<String> keys = son.keySet();
                for (String key : keys) {
                    JSONObject obj = son.getJSONObject(key);

                    String name = obj.getString("catalog_name");
                    String id = obj.getString("id");

                    if (!Tags.programSearchTag.containsKey(name)) Tags.programSearchTag.put(name, new String[c]);
                    Tags.programSearchTag.get(name)[0] = id;
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initProgramSearchTagMe));

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
