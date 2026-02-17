package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgArtistSearchReq {
    private static KgArtistSearchReq instance;

    private KgArtistSearchReq() {
    }

    public static KgArtistSearchReq getInstance() {
        if (instance == null) instance = new KgArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (酷狗)
    private final String SEARCH_ARTIST_KG_API = "/v1/search/author";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("platform", "AndroidFilter");
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pagesize", limit);
        params.put("category", 1);
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_ARTIST_KG_API);
        String artistInfoBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "complexsearch.kugou.com")
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray artistArray = data.getJSONArray("lists");
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);
            // 部分搜索结果包含空 json
            if (JsonUtil.isEmpty(artistJson)) continue;

            String artistId = artistJson.getString("AuthorId");
            String artistName = artistJson.getString("AuthorName");
            Integer songNum = artistJson.getIntValue("AudioCount");
            Integer albumNum = artistJson.getIntValue("AlbumCount");
            Integer mvNum = artistJson.getIntValue("VideoCount");
            String coverImgThumbUrl = artistJson.getString("Avatar");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetMusicSource.KG);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setSongNum(songNum);
            artistInfo.setAlbumNum(albumNum);
            artistInfo.setMvNum(mvNum);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
            r.add(artistInfo);
        }
        return new CommonResult<>(r, t);
    }
}
