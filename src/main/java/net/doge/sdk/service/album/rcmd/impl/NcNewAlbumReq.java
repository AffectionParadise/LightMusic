package net.doge.sdk.service.album.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcNewAlbumReq {
    private static NcNewAlbumReq instance;

    private NcNewAlbumReq() {
    }

    public static NcNewAlbumReq getInstance() {
        if (instance == null) instance = new NcNewAlbumReq();
        return instance;
    }

    // 新碟上架 API (网易云)
    private final String NEW_ALBUM_NC_API = "https://music.163.com/api/discovery/new/albums/area";
    // 新碟上架(热门) API (网易云)
//    private final String HOT_ALBUM_NC_API = SdkCommon.PREFIX + "/top/album?type=hot&area=%s";
    // 全部新碟 API (网易云)
    private final String ALL_NEW_ALBUM_NC_API = "https://music.163.com/weapi/album/new";
    // 最新专辑 API (网易云)
    private final String NEWEST_ALBUM_NC_API = "https://music.163.com/api/discovery/newAlbum";
    // 数字新碟上架 API (网易云)
    private final String NEWEST_DI_ALBUM_NC_API = "https://music.163.com/weapi/vipmall/albumproduct/list";
    // 数字专辑语种风格馆 API (网易云)
    private final String LANG_DI_ALBUM_NC_API = "https://music.163.com/weapi/vipmall/appalbum/album/style";
    // 曲风专辑 API (网易云)
    private final String STYLE_ALBUM_NC_API = "https://music.163.com/api/style-tag/home/album";

    /**
     * 新碟上架
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, NEW_ALBUM_NC_API,
                            String.format("{\"area\":\"%s\",\"type\":\"new\",\"offset\":0,\"limit\":50,\"year\":%s,\"month\":%s,\"total\":false,\"rcmd\":true}",
                                    s[0], TimeUtil.currYear(), TimeUtil.currMonth()),
                            options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONArray("weekData");
            JSONArray monthData = albumInfoJson.getJSONArray("monthData");
            if (JsonUtil.isEmpty(albumArray)) albumArray = monthData;
            else albumArray.addAll(monthData);
            t = albumArray.size();
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getIntValue("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
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

//    /**
//     * 新碟上架(热门)
//     */
//    public CommonResult<NetAlbumInfo> getHotAlbums(String tag, int page, int limit) {
//        List<NetAlbumInfo> r = new LinkedList<>();
//        int t = 0;
//        String[] s = Tags.newAlbumTag.get(tag);
//
//        if (StringUtil.notEmpty(s[0])) {
//            String albumInfoBody = HttpRequest.get(String.format(HOT_ALBUM_NC_API, s[0]))
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            JSONArray albumArray = albumInfoJson.getJSONArray("weekData");
//            JSONArray monthData = albumInfoJson.getJSONArray("monthData");
//            if (JsonUtil.isEmpty(albumArray)) albumArray = monthData;
//            else albumArray.addAll(monthData);
//            t = albumArray.size();
//            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
//                JSONObject albumJson = albumArray.getJSONObject(i);
//
//                String albumId = albumJson.getString("id");
//                String albumName = albumJson.getString("name");
//                String artist = SdkUtil.parseArtist(albumJson);
//                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
//                Integer songNum = albumJson.getIntValue("size");
//                String coverImgThumbUrl = albumJson.getString("picUrl");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
//                albumInfo.setSongNum(songNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(albumInfo);
//            }
//        }
//        return new CommonResult<>(r, t);
//    }

    /**
     * 全部新碟(接口分页，与上面两个分开处理)
     */
    public CommonResult<NetAlbumInfo> getAllNewAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, ALL_NEW_ALBUM_NC_API,
                            String.format("{\"area\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[0], (page - 1) * limit, limit),
                            options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            t = albumInfoJson.getIntValue("total");
            JSONArray albumArray = albumInfoJson.getJSONArray("albums");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getIntValue("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
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

    /**
     * 最新专辑
     */
    public CommonResult<NetAlbumInfo> getNewestAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, NEWEST_ALBUM_NC_API, "{}", options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray albumArray = albumInfoJson.getJSONArray("albums");
        t = albumArray.size();
        for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
            Integer songNum = albumJson.getIntValue("size");
            String coverImgThumbUrl = albumJson.getString("picUrl");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
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
     * 数字新碟上架
     */
    public CommonResult<NetAlbumInfo> getNewestDiAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, NEWEST_DI_ALBUM_NC_API,
                        "{\"area\":\"ALL\",\"offset\":0,\"limit\":200,\"total\":true,\"type\":\"\"}", options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray albumArray = albumInfoJson.getJSONArray("products");
        t = albumArray.size();
        for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumId");
            String albumName = albumJson.getString("albumName");
            String artist = albumJson.getString("artistName");
            String publishTime = TimeUtil.msToDate(albumJson.getLong("pubTime"));
            String coverImgThumbUrl = albumJson.getString("coverUrl");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            albumInfo.setPublishTime(publishTime);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 数字专辑语种风格馆
     */
    public CommonResult<NetAlbumInfo> getLangDiAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, LANG_DI_ALBUM_NC_API,
                            String.format("{\"area\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[1], (page - 1) * limit, limit),
                            options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONArray("albumProducts");
            t = albumInfoJson.getBooleanValue("hasNextPage") ? page * limit + 1 : page * limit;
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumId");
                String albumName = albumJson.getString("albumName");
                String artist = albumJson.getString("artistName");
                String coverImgThumbUrl = albumJson.getString("coverUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(albumInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 曲风专辑
     */
    public CommonResult<NetAlbumInfo> getStyleAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTag.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_ALBUM_NC_API,
                            String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", s[2], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray albumArray = data.getJSONArray("albums");
            t = data.getJSONObject("page").getIntValue("total");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getIntValue("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
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
