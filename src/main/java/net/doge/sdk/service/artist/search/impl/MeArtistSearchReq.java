package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MeArtistSearchReq {
    private static MeArtistSearchReq instance;

    private MeArtistSearchReq() {
    }

    public static MeArtistSearchReq getInstance() {
        if (instance == null) instance = new MeArtistSearchReq();
        return instance;
    }

    // 关键词搜索声优 API (猫耳)
    private final String SEARCH_CV_ME_API = "https://www.missevan.com/sound/getsearch";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("s", keyword);
        params.put("type", 4);
        params.put("p", page);
        params.put("page_size", limit);
        String artistInfoBody = HttpRequest.get(SEARCH_CV_ME_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject info = artistInfoJson.getJSONObject("info");
        t = info.getJSONObject("pagination").getIntValue("count");
        JSONArray artistArray = info.getJSONArray("Datas");
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("id");
            String artistName = artistJson.getString("name");
            String coverImgThumbUrl = artistJson.getString("icon");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetResourceSource.ME);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
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
