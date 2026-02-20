package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class FaHotMusicRecommendReq {
    private static FaHotMusicRecommendReq instance;

    private FaHotMusicRecommendReq() {
    }

    public static FaHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new FaHotMusicRecommendReq();
        return instance;
    }

    // 发姐歌曲 API (发姐)
    private final String HOT_MUSIC_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";

    /**
     * 发姐热门歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusic(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        // 获取发姐请求参数
        String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                .executeAsStr();
        Document doc = Jsoup.parse(body);
        Elements ap = doc.select("#aplayer1");
        String musicSet = UrlUtil.encodeAll(ap.attr("data-songs"));
        String _nonce = ap.attr("data-_nonce");

        String songInfoBody = HttpRequest.get(String.format(HOT_MUSIC_FA_API, musicSet, _nonce))
                .executeAsStr();
        JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
        JSONObject data = songInfoJson.getJSONObject("msg");
        JSONArray songArray = data.getJSONArray("songs");
        t = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("title");
            String artist = songJson.getString("author");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.FA);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
