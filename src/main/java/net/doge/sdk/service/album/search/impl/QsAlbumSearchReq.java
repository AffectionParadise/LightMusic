package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsAlbumSearchReq {
    private static QsAlbumSearchReq instance;

    private QsAlbumSearchReq() {
    }

    public static QsAlbumSearchReq getInstance() {
        if (instance == null) instance = new QsAlbumSearchReq();
        return instance;
    }

    // 搜索专辑 API (汽水)
    private final String SEARCH_ALBUM_QS_API = "https://api.qishui.com/luna/search/album";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;

        // 每页固定 20 条
        int lim = Math.min(20, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("q", keyword);
        params.put("cursor", (page - 1) * lim);
        params.put("count", lim);
        String albumInfoBody = SdkCommon.qsRequest(SEARCH_ALBUM_QS_API, params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray resultGroups = albumInfoJson.getJSONArray("result_groups");
        if (JsonUtil.notEmpty(resultGroups)) {
            JSONObject result = resultGroups.getJSONObject(0);
            JSONArray albumArray = result.getJSONArray("data");
            t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i).getJSONObject("entity").getJSONObject("album");

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("release_date") * 1000);
                Integer songNum = albumJson.getIntValue("count_tracks");
                String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(albumJson);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetResourceSource.QS);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(albumInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
