package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgArtistMenuReq {
    private static KgArtistMenuReq instance;

    private KgArtistMenuReq() {
    }

    public static KgArtistMenuReq getInstance() {
        if (instance == null) instance = new KgArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (酷狗)
    private final String ARTIST_ALBUMS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/album?singerid=%s&page=%s&pagesize=%s";
    //    private final String ARTIST_ALBUMS_KG_API = "/kmr/v1/author/albums";
    // 歌手 MV API (酷狗)
//    private final String ARTIST_MVS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/mv?&singerid=%s&page=%s&pagesize=%s";
    private final String ARTIST_MVS_KG_API = "https://openapicdn.kugou.com/kmr/v1/author/videos";
    // 相似歌手 API (酷狗)
    private final String SIMILAR_ARTIST_KG_API = "http://kmr.service.kugou.com/v1/author/similar";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_KG_API, id, page, limit))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("info");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumid");
            String albumName = albumJson.getString("albumname");
            String artist = albumJson.getString("singername");
            String artistId = albumJson.getString("singerid");
            String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
            String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
            Integer songNum = albumJson.getIntValue("songcount");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.KG);
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
            res.add(albumInfo);
        }

        // 部分信息缺失，继续使用旧接口
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ARTIST_ALBUMS_KG_API);
//            String dat = String.format("{\"author_id\":\"%s\",\"page\":%s,\"pagesize\":%s,\"sort\":3,\"category\":1,\"area_code\":\"all\"}",
//                     id, page, limit);
//            String albumInfoBody = SdkCommon.kgRequest(null, dat, options)
//                    .header("x-router", "openapi.kugou.com")
//                    .header("kg-tid", "36")
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            total = albumInfoJson.getIntValue("total");
//            JSONArray albumArray = albumInfoJson.getJSONArray("data");
//            for (int i = 0, len = albumArray.size(); i < len; i++) {
//                JSONObject albumJson = albumArray.getJSONObject(i);
//
//                String albumId = albumJson.getString("album_id");
//                String albumName = albumJson.getString("album_name");
//                String artist = SdkUtil.parseArtist(albumJson);
//                String artistId = SdkUtil.parseArtistId(albumJson);
//                String coverImgThumbUrl = albumJson.getString("sizable_cover").replace("/{size}", "");
//                String publishTime = albumJson.getString("publish_date");
////                Integer songNum = albumJson.getIntValue("songcount");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.KG);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setArtistId(artistId);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
////                albumInfo.setSongNum(songNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//                res.add(albumInfo);
//            }

        return new CommonResult<>(res, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        //            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_KG_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//            JSONObject data = mvInfoJson.getJSONObject("data");
//            total = data.getIntValue("total");
//            JSONArray mvArray = data.getJSONArray("info");
//            for (int i = 0, len = mvArray.size(); i < len; i++) {
//                JSONObject mvJson = mvArray.getJSONObject(i);
//
//                String mvId = mvJson.getString("hash");
//                // 酷狗返回的名称含有 HTML 标签，需要去除
//                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("filename"));
//                String artistName = StringUtil.removeHTMLLabel(mvJson.getString("singername"));
//                String coverImgUrl = mvJson.getString("imgurl");
//
//                NetMvInfo mvInfo = new NetMvInfo();
//                mvInfo.setSource(NetMusicSource.KG);
//                mvInfo.setId(mvId);
//                mvInfo.setName(mvName);
//                mvInfo.setArtist(artistName);
//                mvInfo.setCoverImgUrl(coverImgUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                    mvInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(mvInfo);
//            }

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(ARTIST_MVS_KG_API);
        Map<String, Object> params = new TreeMap<>();
        params.put("author_id", id);
        params.put("is_fanmade", "");
        // 18：官方 20：现场 23：饭制 42419：歌手发布
        params.put("tag_idx", "");
        params.put("page", page);
        params.put("pagesize", limit);
        String mvInfoBody = SdkCommon.kgRequest(params, null, options)
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        total = mvInfoJson.getIntValue("total");
        JSONArray mvArray = mvInfoJson.getJSONArray("data");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("mkv_qhd_hash");
            String mvName = mvJson.getString("video_name");
            String artistName = mvJson.getString("author_name");
            String coverImgUrl = mvJson.getString("hdpic").replace("/{size}", "");
            Double Duration = mvJson.getDouble("timelength") / 1000;
            Long playCount = mvJson.getLong("history_heat");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetMusicSource.KG);
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setDuration(Duration);
            mvInfo.setPlayCount(playCount);
            mvInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(mvInfo);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t;

        String id = netArtistInfo.getId();
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(SIMILAR_ARTIST_KG_API);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        String dat = String.format("{\"clientver\":\"%s\",\"mid\":\"%s\",\"clienttime\":\"%s\",\"key\":\"%s\"," +
                        "\"appid\":\"%s\",\"data\":[{\"author_id\":\"%s\"}]}",
                KugouReqBuilder.clientver, KugouReqBuilder.mid, ct, KugouReqBuilder.signParamsKey(ct), KugouReqBuilder.appid, id);
        String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray artistArray = artistInfoJson.getJSONArray("data").getJSONArray(0);
        t = artistArray.size();
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("author_id");
            String artistName = artistJson.getString("author_name");
            String coverImgThumbUrl = artistJson.getString("sizable_avatar").replace("{size}", "240");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetMusicSource.KG);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(artistInfo);
        }

        return new CommonResult<>(res, t);
    }
}
