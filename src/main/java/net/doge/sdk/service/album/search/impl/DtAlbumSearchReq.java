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
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

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
            = "https://www.duitang.com/napi/album/list/by_search/?include_fields=is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover" +
            "&kw=%s&start=%s&limit=%s&type=album&_type=&_=%s";
    // 关键词搜索专辑 API 2 (堆糖)
    private final String SEARCH_ALBUM_DT_API_2
            = "https://www.duitang.com/napi/blogv2/list/by_search/?include_fields=is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover" +
            "&kw=%s&start=%s&limit=%s&type=feed&_type=&_=%s";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_DT_API, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis())).execute();
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

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_DT_API_2, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis())).execute();
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
