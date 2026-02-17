package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcArtistSearchReq {
    private static NcArtistSearchReq instance;

    private NcArtistSearchReq() {
    }

    public static NcArtistSearchReq getInstance() {
        if (instance == null) instance = new NcArtistSearchReq();
        return instance;
    }

    // 关键词搜索歌手 API (网易云)
    private final String CLOUD_SEARCH_NC_API = "https://interface.music.163.com/eapi/cloudsearch/pc";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":100,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject result = artistInfoJson.getJSONObject("result");
        if (JsonUtil.notEmpty(result)) {
            t = result.getIntValue("artistCount");
            JSONArray artistArray = result.getJSONArray("artists");
            if (JsonUtil.notEmpty(artistArray)) {
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer albumNum = artistJson.getIntValue("albumSize");
                    Integer mvNum = artistJson.getIntValue("mvSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setAlbumNum(albumNum);
                    artistInfo.setMvNum(mvNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(artistInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
