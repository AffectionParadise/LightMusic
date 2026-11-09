package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QiArtistSearchReq {
    private static QiArtistSearchReq instance;

    private QiArtistSearchReq() {
    }

    public static QiArtistSearchReq getInstance() {
        if (instance == null) instance = new QiArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (千千)
    private final String SEARCH_ARTIST_QI_API = "https://music.91q.com/v1/search";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageNo", page);
        params.put("pageSize", limit);
        params.put("type", 2);
        params.put("word", keyword);
        String artistInfoBody = SdkCommon.qiRequest(SEARCH_ARTIST_QI_API, params)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray artistArray = data.getJSONArray("typeArtist");
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("artistCode");
            String artistName = artistJson.getString("name");
            Integer songNum = artistJson.getIntValue("trackTotal");
            String coverImgThumbUrl = artistJson.getString("pic");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetResourceSource.QI);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            artistInfo.setSongNum(songNum);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
            r.add(artistInfo);
        }

        return new CommonResult<>(r, t);
    }
}
