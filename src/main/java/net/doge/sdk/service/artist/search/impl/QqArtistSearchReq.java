package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqArtistSearchReq {
    private static QqArtistSearchReq instance;

    private QqArtistSearchReq() {
    }

    public static QqArtistSearchReq getInstance() {
        if (instance == null) instance = new QqArtistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        int lim = Math.min(40, limit);
        String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, lim, keyword, 1))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        int sum = data.getJSONObject("meta").getIntValue("sum");
        t = PageUtil.totalPage(sum, lim) * limit;
        JSONArray artistArray = data.getJSONObject("body").getJSONObject("singer").getJSONArray("list");
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("singerMID");
            String artistName = artistJson.getString("singerName");
            Integer songNum = artistJson.getIntValue("songNum");
            Integer albumNum = artistJson.getIntValue("albumNum");
            Integer mvNum = artistJson.getIntValue("mvNum");
            String coverImgThumbUrl = String.format(SdkCommon.ARTIST_IMG_QQ_API, artistId);

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetResourceSource.QQ);
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
