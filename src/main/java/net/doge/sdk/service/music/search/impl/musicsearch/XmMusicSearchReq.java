package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmMusicSearchReq {
    private static XmMusicSearchReq instance;

    private XmMusicSearchReq() {
    }

    public static XmMusicSearchReq getInstance() {
        if (instance == null) instance = new XmMusicSearchReq();
        return instance;
    }

    // 关键词搜索节目 API (喜马拉雅)
    private final String SEARCH_PROGRAM_XM_API = "https://www.ximalaya.com/revision/search/main";

    /**
     * 根据关键词获取节目
     */
    public CommonResult<NetMusicInfo> searchProgram(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("kw", keyword);
        params.put("page", page);
        params.put("spellchecker", true);
        params.put("condition", "relation");
        params.put("rows", limit);
        params.put("device", "iPhone");
        params.put("core", "track");
        params.put("fq", "");
        params.put("paidFilter", false);
        String musicInfoBody = HttpRequest.get(SEARCH_PROGRAM_XM_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data").getJSONObject("track");
        t = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("docs");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("title");
            String artist = songJson.getString("nickname");
            String artistId = songJson.getString("uid");
            String albumName = songJson.getString("albumTitle");
            String albumId = songJson.getString("albumId");
            Double duration = songJson.getDouble("duration");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.XM);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
