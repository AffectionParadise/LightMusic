package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiArtistSearchReq {
    private static QiArtistSearchReq instance;

    private QiArtistSearchReq() {
    }

    public static QiArtistSearchReq getInstance() {
        if (instance == null) instance = new QiArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (千千)
    private final String SEARCH_ARTIST_QI_API = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=2&word=%s";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String artistInfoBody = SdkCommon.qiRequest(String.format(SEARCH_ARTIST_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword))
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
