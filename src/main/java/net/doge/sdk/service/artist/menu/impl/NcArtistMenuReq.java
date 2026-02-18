package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NcArtistMenuReq {
    private static NcArtistMenuReq instance;

    private NcArtistMenuReq() {
    }

    public static NcArtistMenuReq getInstance() {
        if (instance == null) instance = new NcArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (网易云)
    private final String ARTIST_ALBUMS_NC_API = "https://music.163.com/weapi/artist/albums/%s";
    // 歌手 MV API (网易云)
    private final String ARTIST_MVS_NC_API = "https://music.163.com/weapi/artist/mvs";
    // 歌手视频 API (网易云)
//    private final String ARTIST_VIDEOS_NC_API = SdkCommon.PREFIX + "/artist/video?id=%s&cursor=%s&size=%s";
    // 相似歌手 API (网易云)
    private final String SIMILAR_ARTIST_NC_API = "https://music.163.com/weapi/discovery/simiArtist";
    // 歌手粉丝 API (网易云)
    private final String ARTIST_FANS_NC_API = "https://music.163.com/weapi/artist/fans/get";
    // 歌手粉丝总数 API (网易云)
    private final String ARTIST_FANS_TOTAL_NC_API = "https://music.163.com/weapi/artist/follow/count/get";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, String.format(ARTIST_ALBUMS_NC_API, id),
                        String.format("{\"offset\":%s,\"limit\":%s,\"total\":true}", (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        total = albumInfoJson.getJSONObject("artist").getIntValue("albumSize");
        JSONArray albumArray = albumInfoJson.getJSONArray("hotAlbums");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("id");
            String name = albumJson.getString("name");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
            Integer songNum = albumJson.getIntValue("size");
            String coverImgThumbUrl = albumJson.getString("picUrl");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setId(albumId);
            albumInfo.setName(name);
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

        return new CommonResult<>(res, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        // 歌手 MV
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_MVS_NC_API,
                        String.format("{\"artistId\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}", id, (page - 1) * limit, limit),
                        options)
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
        total = artistInfo.getMvNum();
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("name").trim();
            String artistName = mvJson.getString("artistName");
            String creatorId = mvJson.getJSONObject("artist").getString("id");
            Long playCount = mvJson.getLong("playCount");
            Double duration = mvJson.getDouble("duration") / 1000;
            String coverImgUrl = mvJson.getString("imgurl");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setCoverImgUrl(coverImgUrl);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(mvInfo);
        }
        // 歌手视频
//            Callable<CommonResult<NetMvInfo>> getArtistVideo = ()->{
//                List<NetMvInfo> res = new LinkedList<>();
//                int t = 0;
//
//                String mvInfoBody = HttpRequest.get(String.format(ARTIST_VIDEOS_NC_API, id, (page - 1) * limit, limit))
//                        .executeAsync()
//                        .body();
//                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("records");
//                t = artistInfo.getMvNum();
//                for (int i = 0, len = mvArray.size(); i < len; i++) {
//                    JSONObject mvJson = mvArray.getJSONObject(i);
//
//                    String mvId = mvJson.getString("id");
//                    String mvName = mvJson.getString("name");
//                    String artistName = mvJson.getString("artistName");
//                    Long playCount = mvJson.getLong("playCount");
//                    Double duration = mvJson.getDouble("duration") / 1000;
//                    String coverImgUrl = mvJson.getString("imgurl");
//
//                    NetMvInfo mvInfo = new NetMvInfo();
//                    mvInfo.setId(mvId);
//                    mvInfo.setName(mvName.trim());
//                    mvInfo.setArtist(artistName);
//                    mvInfo.setCoverImgUrl(coverImgUrl);
//                    mvInfo.setPlayCount(playCount);
//                    mvInfo.setDuration(duration);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                        mvInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(mvInfo);
//                }
//
//                return new CommonResult<>(res, t);
//            };

        return new CommonResult<>(res, total);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t = 0;

        String id = netArtistInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_ARTIST_NC_API, String.format("{\"artistid\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray artistArray = artistInfoJson.getJSONArray("artists");
        if (JsonUtil.notEmpty(artistArray)) {
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
//                Integer mvNum = artistJson.getIntValue("mvSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        AtomicInteger t = new AtomicInteger(0);

        String id = artistInfo.getId();
        Runnable getFans = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String userInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_FANS_NC_API,
                            String.format("{\"id\":\"%s\",\"offset\":%s,\"limit\":%s}", id, (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("data");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i).getJSONObject("userProfile");

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = userJson.getString("avatarUrl");
//                    Integer follow = userJson.getIntValue("follows");
//                    Integer fan = userJson.getIntValue("followeds");
//                    Integer playlistCount = userJson.getIntValue("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                    userInfo.setFollow(follow);
//                    userInfo.setFan(fan);
//                    userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        };
        Runnable getFansCnt = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String tBody = SdkCommon.ncRequest(Method.POST, ARTIST_FANS_TOTAL_NC_API, String.format("{\"id\":\"%s\"}", id), options)
                    .executeAsStr();
            t.set(JSONObject.parseObject(tBody).getJSONObject("data").getIntValue("fansCnt"));
        };

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(getFans);
        executor.submit(getFansCnt);
        executor.await();

        return new CommonResult<>(res, t.get());
    }
}
