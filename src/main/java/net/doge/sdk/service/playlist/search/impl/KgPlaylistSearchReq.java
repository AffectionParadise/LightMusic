package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgPlaylistSearchReq {
    private static KgPlaylistSearchReq instance;

    private KgPlaylistSearchReq() {
    }

    public static KgPlaylistSearchReq getInstance() {
        if (instance == null) instance = new KgPlaylistSearchReq();
        return instance;
    }

    // 关键词搜索歌单 API (酷狗)
//    private final String SEARCH_PLAYLIST_KG_API = "http://mobilecdnbj.kugou.com/api/v3/search/special?filter=0&keyword=%s&page=%s&pagesize=%s";
    private final String SEARCH_PLAYLIST_KG_API = "/v1/search/special";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        //            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray playlistArray = data.getJSONArray("info");
//            for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                String playlistId = playlistJson.getString("specialid");
//                String playlistName = playlistJson.getString("specialname");
//                String creator = playlistJson.getString("nickname");
//                Long playCount = playlistJson.getLong("playcount");
//                Integer trackCount = playlistJson.getIntValue("songcount");
//                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.KG);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(playlistName);
//                playlistInfo.setCreator(creator);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//                r.add(playlistInfo);
//            }

        Map<String, Object> params = new TreeMap<>();
        params.put("platform", "AndroidFilter");
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pagesize", limit);
        params.put("category", 1);
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_PLAYLIST_KG_API);
        String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "complexsearch.kugou.com")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray playlistArray = data.getJSONArray("lists");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("specialid");
            String playlistName = playlistJson.getString("specialname");
            String creator = playlistJson.getString("nickname");
            Long playCount = playlistJson.getLong("total_play_count");
            Integer trackCount = playlistJson.getIntValue("song_count");
            String coverImgThumbUrl = playlistJson.getString("img");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.KG);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setTrackCount(trackCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });
            r.add(playlistInfo);
        }
        return new CommonResult<>(r, t);
    }
}
