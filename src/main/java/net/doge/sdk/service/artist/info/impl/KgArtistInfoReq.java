package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgArtistInfoReq {
    private static KgArtistInfoReq instance;

    private KgArtistInfoReq() {
    }

    public static KgArtistInfoReq getInstance() {
        if (instance == null) instance = new KgArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (酷狗)
//    private final String ARTIST_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/info?singerid=%s";
    private final String ARTIST_DETAIL_KG_API = "/kmr/v3/author";
    // 歌手歌曲 API (酷狗)
//    private final String ARTIST_SONGS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/song?singerid=%s&page=%s&pagesize=%s";
    private final String ARTIST_SONGS_KG_API = "https://openapi.kugou.com/kmr/v1/audio_group/author";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        //                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, id))
//                        .executeAsync()
//                        .body();
//                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
//                JSONObject artistJson = artistInfoJson.getJSONObject("data");
//
//                String artistId = artistJson.getString("singerid");
//                String name = artistJson.getString("singername");
//                String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");
//                Integer songNum = artistJson.getIntValue("songcount");
//                Integer albumNum = artistJson.getIntValue("albumcount");
//                Integer mvNum = artistJson.getIntValue("mvcount");
//
//                NetArtistInfo artistInfo = new NetArtistInfo();
//                artistInfo.setSource(NetMusicSource.KG);
//                artistInfo.setId(artistId);
//                artistInfo.setName(name);
//                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                artistInfo.setSongNum(songNum);
//                artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    artistInfo.setCoverImgThumb(coverImgThumb);
//                });

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ARTIST_DETAIL_KG_API);
        String dat = String.format("{\"author_id\":%s}", id);
        String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "openapi.kugou.com")
                .header("kg-tid", "36")
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("data");

        String artistId = artistJson.getString("author_id");
        String name = artistJson.getString("author_name");
        String coverImgThumbUrl = artistJson.getString("sizable_avatar").replace("{size}", "240");
        Integer songNum = artistJson.getIntValue("song_count");
        Integer albumNum = artistJson.getIntValue("album_count");
        Integer mvNum = artistJson.getIntValue("mv_count");

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetMusicSource.KG);
        artistInfo.setId(artistId);
        artistInfo.setName(name);
        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        artistInfo.setSongNum(songNum);
        artistInfo.setAlbumNum(albumNum);
        artistInfo.setMvNum(mvNum);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            artistInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(artistInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        //            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, id))
//                    .executeAsync()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("data");
//
//            String description = data.getString("intro");
//            String coverImgUrl = data.getString("imgurl").replace("{size}", "240");
//
//            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
//            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
//            artistInfo.setDescription(description);
//            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("songcount"));
//            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumcount"));
//            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvcount"));

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ARTIST_DETAIL_KG_API);
        String dat = String.format("{\"author_id\":%s}", id);
        String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "openapi.kugou.com")
                .header("kg-tid", "36")
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");

        String description = data.getString("intro");
        String coverImgUrl = data.getString("sizable_avatar").replace("{size}", "240");

        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("song_count"));
        if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("album_count"));
        if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mv_count"));
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        //            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_KG_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("data");
//            total = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("info");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("hash");
//                String songId = songJson.getString("album_audio_id");
//                String[] s = songJson.getString("filename").split(" - ");
//                String name = s[1];
//                String artist = s[0];
//                String artistId = id;
//                String albumName = songJson.getString("album_name");
//                String albumId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                JSONArray mvdata = songJson.getJSONArray("mvdata");
//                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
//                else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//
//                res.add(musicInfo);
//            }

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ARTIST_SONGS_KG_API);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        String dat = String.format("{\"appid\":%s,\"clientver\":%s,\"mid\":\"%s\",\"clienttime\":%s," +
                        "\"key\":\"%s\",\"author_id\":\"%s\",\"page\":%s,\"pagesize\":%s,\"sort\":1,\"area_code\":\"all\"}",
                KugouReqBuilder.appid, KugouReqBuilder.clientver, KugouReqBuilder.mid, ct, KugouReqBuilder.signParamsKey(ct), id, page, limit);
        String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "openapi.kugou.com")
                .header("kg-tid", "220")
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        total = artistInfoJson.getIntValue("total");
        JSONArray songArray = artistInfoJson.getJSONArray("data");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String hash = songJson.getString("hash");
            String songId = songJson.getString("album_audio_id");
            String name = songJson.getString("audio_name");
            String artist = songJson.getString("author_name");
            String artistId = id;
            String albumName = songJson.getString("album_name");
            String albumId = songJson.getString("album_id");
            Double duration = songJson.getDouble("timelength") / 1000;
            String mvId = songJson.getString("video_hash");
            int qualityType = AudioQuality.UNKNOWN;
            if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
            else if (songJson.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
            else if (songJson.getLong("filesize_320") != 0) qualityType = AudioQuality.HQ;
            else if (songJson.getLong("filesize_128") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KG);
            musicInfo.setHash(hash);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
