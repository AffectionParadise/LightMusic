package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwArtistSearchReq {
    private static KwArtistSearchReq instance;

    private KwArtistSearchReq() {
    }

    public static KwArtistSearchReq getInstance() {
        if (instance == null) instance = new KwArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (酷我)
    private final String SEARCH_ARTIST_KW_API = "https://kuwo.cn/api/www/search/searchArtistBykeyWord";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;

        Map<String, Object> params = new HashMap<>();
        params.put("key", keyword);
        params.put("pn", page);
        params.put("rn", limit);
        params.put("httpsStatus", 1);
        HttpResponse resp = SdkCommon.kwRequest(SEARCH_ARTIST_KW_API)
                .queryParams(params)
                .execute();
        // 酷我有时候会崩，先验证是否请求成功
        if (resp.isSuccessful()) {
            String artistInfoBody = resp.body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("list");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicNum");
                String coverImgThumbUrl = artistJson.getString("pic300");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.KW);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setSongNum(songNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(artistInfo);
            }
        }

        return new CommonResult<>(r, t);
    }
}
