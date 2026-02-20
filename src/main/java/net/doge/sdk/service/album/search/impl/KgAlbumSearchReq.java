package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgAlbumSearchReq {
    private static KgAlbumSearchReq instance;

    private KgAlbumSearchReq() {
    }

    public static KgAlbumSearchReq getInstance() {
        if (instance == null) instance = new KgAlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API (酷狗)
//    private final String SEARCH_ALBUM_KG_API = "http://msearch.kugou.com/api/v3/search/album?keyword=%s&page=%s&pagesize=%s";
    private final String SEARCH_ALBUM_KG_API = "/v1/search/album";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

//            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray albumArray = data.getJSONArray("info");
//            for (int i = 0, len = albumArray.size(); i < len; i++) {
//                JSONObject albumJson = albumArray.getJSONObject(i);
//
//                String albumId = albumJson.getString("albumid");
//                String albumName = albumJson.getString("albumname");
//                String artist = albumJson.getString("singername");
//                String artistId = albumJson.getString("singerid");
//                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
//                Integer songNum = albumJson.getIntValue("songcount");
//                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.KG);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setArtistId(artistId);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
//                albumInfo.setSongNum(songNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//                r.add(albumInfo);
//            }

        Map<String, Object> params = new TreeMap<>();
        params.put("platform", "AndroidFilter");
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pagesize", limit);
        params.put("category", 1);
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_ALBUM_KG_API);
        String albumInfoBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "complexsearch.kugou.com")
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("lists");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumid");
            String albumName = albumJson.getString("albumname");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = albumJson.getString("publish_time");
            Integer songNum = albumJson.getIntValue("songcount");
            String coverImgThumbUrl = albumJson.getString("img");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.KG);
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
