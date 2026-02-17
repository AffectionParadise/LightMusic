package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FaMusicUrlReq {
    private static FaMusicUrlReq instance;

    private FaMusicUrlReq() {
    }

    public static FaMusicUrlReq getInstance() {
        if (instance == null) instance = new FaMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (发姐)
    private final String SONG_URL_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        // 获取发姐请求参数
        String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                .executeAsStr();
        Document doc = Jsoup.parse(body);
        Elements ap = doc.select("#aplayer1");
        String musicSet = UrlUtil.encodeAll(ap.attr("data-songs"));
        String _nonce = ap.attr("data-_nonce");

        String songInfoBody = HttpRequest.get(String.format(SONG_URL_FA_API, musicSet, _nonce))
                .executeAsStr();
        JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
        JSONObject data = songInfoJson.getJSONObject("msg");
        JSONArray songArray = data.getJSONArray("songs");
        for (int i = 0, s = songArray.size(); i < s; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            if (!id.equals(songJson.getString("id"))) continue;
            return UrlUtil.encodeBlank(songJson.getString("url"));
        }
        return "";
    }
}
