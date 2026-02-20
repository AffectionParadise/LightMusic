package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqArtistMenuReq {
    private static QqArtistMenuReq instance;

    private QqArtistMenuReq() {
    }

    public static QqArtistMenuReq getInstance() {
        if (instance == null) instance = new QqArtistMenuReq();
        return instance;
    }

    // 歌手 MV API (QQ)
//    private final String ARTIST_MVS_QQ_API = "http://c.y.qq.com/mv/fcgi-bin/fcg_singer_mv.fcg?singermid=%s&order=time&begin=%s&num=%s&cid=205360581";
    // 相似歌手 API (QQ)
    private final String SIMILAR_ARTIST_QQ_API = "http://c.y.qq.com/v8/fcg-bin/fcg_v8_simsinger.fcg?singer_mid=%s&num=10&utf8=1";
    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // 歌曲封面信息 API (QQ)
    private final String SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":0},\"singerAlbum\":{\"method\":\"get_singer_album\",\"param\":" +
                        "{\"singermid\":\"%s\",\"order\":\"time\",\"begin\":%s,\"num\":%s,\"exstatus\":1}," +
                        "\"module\":\"music.web_singer_info_svr\"}}", id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("singerAlbum").getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("list");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("album_mid");
            String albumName = albumJson.getString("album_name");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = albumJson.getString("pub_time");
            Integer songNum = albumJson.getJSONObject("latest_song").getIntValue("song_count");
            String coverImgThumbUrl = String.format(SONG_IMG_QQ_API, albumId);

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

        return new CommonResult<>(res, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String name = artistInfo.getName();
        //            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_QQ_API, id, (page - 1) * limit, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//            JSONObject data = mvInfoJson.getJSONObject("data");
//            total = data.getIntValue("total");
//            JSONArray mvArray = data.getJSONArray("list");
//            for (int i = 0, len = mvArray.size(); i < len; i++) {
//                JSONObject mvJson = mvArray.getJSONObject(i);
//
//                String mvId = mvJson.getString("vid");
//                String mvName = mvJson.getString("title").trim();
//                String artistName = mvJson.getString("singer_name");
//                String creatorId = mvJson.getString("singer_id");
//                String coverImgUrl = mvJson.getString("pic");
//                Long playCount = mvJson.getLong("listenCount");
//
//                NetMvInfo mvInfo = new NetMvInfo();
//                mvInfo.setSource(NetMusicSource.QQ);
//                mvInfo.setId(mvId);
//                mvInfo.setName(mvName);
//                mvInfo.setArtist(artistName);
//                mvInfo.setCreatorId(creatorId);
//                mvInfo.setCoverImgUrl(coverImgUrl);
//                mvInfo.setPlayCount(playCount);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                    mvInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(mvInfo);
//            }

        String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"mv\":{\"method\":\"GetSingerMvList\",\"param\":{\"singermid\":\"%s\",\"order\":1,\"start\":%s,\"count\":%s}," +
                        "\"module\":\"MvService.MvInfoProServer\"}}", id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("mv").getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("vid");
            String mvName = mvJson.getString("title").trim();
            String artistName = name;
            String creatorId = id;
            String coverImgUrl = mvJson.getString("picurl");
            Double duration = mvJson.getDouble("duration");
            Long playCount = mvJson.getLong("playcnt");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.QQ);
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setCoverImgUrl(coverImgUrl);
            mvInfo.setDuration(duration);
            mvInfo.setPlayCount(playCount);
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
        int t = 0;

        String id = netArtistInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_QQ_API, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray artistArray = artistInfoJson.getJSONObject("singers").getJSONArray("items");
        if (JsonUtil.notEmpty(artistArray)) {
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("mid");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.QQ);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
