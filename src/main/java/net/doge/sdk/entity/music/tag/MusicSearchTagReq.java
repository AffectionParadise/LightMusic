package net.doge.sdk.entity.music.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.Tags;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MusicSearchTagReq {
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
            String playlistTagBody = HttpRequest.get(String.format(PROGRAM_SEARCH_TAG_ME_API))
                    .execute()
                    .body();
            JSONArray tags = JSONArray.fromObject(playlistTagBody);
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject son = tags.getJSONObject(i).getJSONObject("son");
                Iterator<String> keys = son.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
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
