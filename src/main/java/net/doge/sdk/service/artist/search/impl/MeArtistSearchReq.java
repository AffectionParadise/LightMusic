package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeArtistSearchReq {
    private static MeArtistSearchReq instance;

    private MeArtistSearchReq() {
    }

    public static MeArtistSearchReq getInstance() {
        if (instance == null) instance = new MeArtistSearchReq();
        return instance;
    }

    // 关键词搜索声优 API (猫耳)
    private final String SEARCH_CV_ME_API = "https://www.missevan.com/sound/getsearch?s=%s&type=4&p=%s&page_size=%s";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String artistInfoBody = HttpRequest.get(String.format(SEARCH_CV_ME_API, encodedKeyword, page, limit))
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
            artistInfo.setSource(NetMusicSource.ME);
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
