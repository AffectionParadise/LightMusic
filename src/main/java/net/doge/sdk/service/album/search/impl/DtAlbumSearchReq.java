package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DtAlbumSearchReq {
    private static DtAlbumSearchReq instance;

    private DtAlbumSearchReq() {
    }

    public static DtAlbumSearchReq getInstance() {
        if (instance == null) instance = new DtAlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API (堆糖)
    private final String SEARCH_ALBUM_DT_API
            = "https://www.duitang.com/napi/album/list/by_search/";
    // 关键词搜索专辑 API 2 (堆糖)
    private final String SEARCH_ALBUM_DT_API_2
            = "https://www.duitang.com/napi/blogv2/list/by_search/";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("include_fields", "is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover");
        params.put("kw", keyword);
        params.put("start", (page - 1) * limit);
        params.put("limit", limit);
        params.put("type", "album");
        params.put("_type", "");
        params.put("_", System.currentTimeMillis());
        HttpResponse resp = HttpRequest.get(SEARCH_ALBUM_DT_API)
                .queryParams(params)
                .execute();
        String albumInfoBody = resp.body();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("object_list");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);
            JSONObject user = albumJson.getJSONObject("user");

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = user.getString("username");
            String artistId = user.getString("id");
            String publishTime = TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000);
            String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
            Integer songNum = albumJson.getIntValue("count");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.DT);
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
        return new CommonResult<>(r, t);
    }

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums2(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("include_fields", "is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover");
        params.put("kw", keyword);
        params.put("start", (page - 1) * limit);
        params.put("limit", limit);
        params.put("type", "feed");
        params.put("_type", "");
        params.put("_", System.currentTimeMillis());
        HttpResponse resp = HttpRequest.get(SEARCH_ALBUM_DT_API_2)
                .queryParams(params)
                .execute();
        String albumInfoBody = resp.body();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("object_list");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject mainJson = albumArray.getJSONObject(i);
            JSONObject albumJson = mainJson.getJSONObject("album");
            JSONObject sender = mainJson.getJSONObject("sender");

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = sender.getString("username");
            String artistId = sender.getString("id");
            String publishTime = TimeUtil.msToDate(mainJson.getLong("add_datetime_ts") * 1000);
            String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
            Integer songNum = albumJson.getIntValue("count");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.DT);
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
        return new CommonResult<>(r, t);
    }
}
