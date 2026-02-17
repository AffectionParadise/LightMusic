package net.doge.sdk.service.music.search.impl.hotsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class QqHotSearchReq {
    private static QqHotSearchReq instance;

    private QqHotSearchReq() {
    }

    public static QqHotSearchReq getInstance() {
        if (instance == null) instance = new QqHotSearchReq();
        return instance;
    }

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        String hotSearchBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody("{\"comm\":{\"ct\":\"19\",\"cv\":\"1803\",\"guid\":\"0\",\"patch\":\"118\",\"psrf_access_token_expiresAt\":0," +
                        "\"psrf_qqaccess_token\":\"\",\"psrf_qqopenid\":\"\",\"psrf_qqunionid\":\"\",\"tmeAppID\":\"qqmusic\",\"tmeLoginType\":0," +
                        "\"uin\":\"0\",\"wid\":\"0\"},\"hotkey\":{\"method\":\"GetHotkeyForQQMusicPC\"," +
                        "\"module\":\"tencent_musicsoso_hotkey.HotkeyService\",\"param\":{\"search_id\":\"\",\"uin\":0}}}")
                .executeAsStr();
        JSONArray hotkeys = JSONObject.parseObject(hotSearchBody).getJSONObject("hotkey").getJSONObject("data").getJSONArray("vec_hotkey");
        for (int i = 0, len = hotkeys.size(); i < len; i++) {
            JSONObject keywordJson = hotkeys.getJSONObject(i);
            r.add(keywordJson.getString("title"));
        }
        return r;
    }
}
