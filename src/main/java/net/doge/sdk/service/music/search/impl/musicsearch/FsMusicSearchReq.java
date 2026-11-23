package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.HtmlUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FsMusicSearchReq {
    private static FsMusicSearchReq instance;

    private FsMusicSearchReq() {
    }

    public static FsMusicSearchReq getInstance() {
        if (instance == null) instance = new FsMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (5sing)
    private final String SEARCH_MUSIC_FS_API = "http://search.5sing.kugou.com/home/json";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("sort", 1);
        params.put("page", page);
        params.put("filter", 0);
        params.put("type", 0);
        String musicInfoBody = HttpRequest.get(SEARCH_MUSIC_FS_API)
                .queryParams(params)
                .executeAsStr();
        // 关键词过长会返回空 Body
        if (StringUtil.notEmpty(musicInfoBody)) {
            JSONObject data = JSONObject.parseObject(musicInfoBody);
            t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
            JSONArray songArray = data.getJSONArray("list");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("songId");
                    String songType = songJson.getString("typeEname");
                    String songName = HtmlUtil.stripHtml(songJson.getString("songName"));
                    String artist = HtmlUtil.stripHtml(songJson.getString("singer"));
                    String artistId = songJson.getString("singerId");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetResourceSource.FS);
                    musicInfo.setId(songType + "_" + songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
