package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class QqUserMenuReq {
    private static QqUserMenuReq instance;

    private QqUserMenuReq() {
    }

    public static QqUserMenuReq getInstance() {
        if (instance == null) instance = new QqUserMenuReq();
        return instance;
    }

    // 用户创建歌单 API (QQ)
    private final String USER_CREATED_PLAYLIST_QQ_API = "https://c.y.qq.com/rsc/fcgi-bin/fcg_user_created_diss";
    // 用户收藏歌单 API (QQ)
    private final String USER_COLLECTED_PLAYLIST_QQ_API = "https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg";
    // 用户收藏专辑 API (QQ)
    private final String USER_COLLECTED_ALBUM_QQ_API = "https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg";

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        String id = userInfo.getId();
        // 创建的歌单
        Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            int t = 0;

            Map<String, Object> params = new HashMap<>();
            params.put("hostUin", 0);
            params.put("hostuin", id);
            params.put("sin", 0);
            params.put("size", 200);
            params.put("g_tk", 5381);
            params.put("loginUin", 0);
            params.put("format", "json");
            params.put("inCharset", "utf8");
            params.put("outCharset", "utf-8");
            params.put("notice", 0);
            params.put("platform", "yqq.json");
            params.put("needNewCode", 0);
            String playlistInfoBody = HttpRequest.get(USER_CREATED_PLAYLIST_QQ_API)
                    .queryParams(params)
                    .referer("https://y.qq.com/portal/profile.html")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                JSONArray playlistArray = data.getJSONArray("disslist");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("diss_name");
                    String creator = userInfo.getName();
                    Long playCount = playlistJson.getLong("listen_num");
                    Integer trackCount = playlistJson.getIntValue("song_cnt");
                    String coverImgThumbUrl = playlistJson.getString("diss_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetResourceSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }
            }

            return new CommonResult<>(r, t);
        };
        // 收藏的歌单
        Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            int t = 0;

            Map<String, Object> params = new HashMap<>();
            params.put("ct", 20);
            params.put("cid", 205360956);
            params.put("userid", id);
            params.put("reqtype", 3);
            params.put("sin", (page - 1) * limit);
            params.put("ein", page * limit);
            String playlistInfoBody = HttpRequest.get(USER_COLLECTED_PLAYLIST_QQ_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("cdlist");
            if (JsonUtil.notEmpty(playlistArray)) {
                t = data.getIntValue("totaldiss");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("dissid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("dissname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("listennum");
                    Integer trackCount = playlistJson.getIntValue("songnum");
                    String coverImgThumbUrl = playlistJson.getString("logo");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetResourceSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }
            }

            return new CommonResult<>(r, t);
        };

        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getCreatedPlaylists);
        executor.submit(getCollectedPlaylists);
        return executor.getResult();
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("ct", 20);
        params.put("cid", 205360956);
        params.put("userid", id);
        params.put("reqtype", 2);
        params.put("sin", (page - 1) * limit);
        params.put("ein", page * limit - 1);
        String albumInfoBody = HttpRequest.get(USER_COLLECTED_ALBUM_QQ_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        total = data.getIntValue("totalalbum");
        JSONArray albumArray = data.getJSONArray("albumlist");
        if (JsonUtil.notEmpty(albumArray)) {
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albummid");
                String albumName = albumJson.getString("albumname");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("pubtime") * 1000);
                Integer songNum = albumJson.getIntValue("songnum");
                String coverImgThumbUrl = String.format(SdkCommon.SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetResourceSource.QQ);
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
        }

        return new CommonResult<>(res, total);
    }
}
