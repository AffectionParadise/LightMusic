package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsArtistSearchReq {
    private static QsArtistSearchReq instance;

    private QsArtistSearchReq() {
    }

    public static QsArtistSearchReq getInstance() {
        if (instance == null) instance = new QsArtistSearchReq();
        return instance;
    }

    // 搜索歌手 API (汽水)
    private final String SEARCH_ARTIST_QS_API = "https://api.qishui.com/luna/search/artist";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;

        // 每页固定 20 条
        int lim = Math.min(20, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("q", keyword);
        params.put("cursor", (page - 1) * lim);
        params.put("count", lim);
        String artistInfoBody = SdkCommon.qsRequest(SEARCH_ARTIST_QS_API, params)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray resultGroups = artistInfoJson.getJSONArray("result_groups");
        if (JsonUtil.notEmpty(resultGroups)) {
            JSONObject result = resultGroups.getJSONObject(0);
            JSONArray artistArray = result.getJSONArray("data");
            t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("entity").getJSONObject("artist");

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("count_tracks");
                Integer albumNum = artistJson.getIntValue("count_albums");
                String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(artistJson);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.QS);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
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
