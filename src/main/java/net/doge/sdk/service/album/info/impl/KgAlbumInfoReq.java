package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgAlbumInfoReq {
    private static KgAlbumInfoReq instance;

    private KgAlbumInfoReq() {
    }

    public static KgAlbumInfoReq getInstance() {
        if (instance == null) instance = new KgAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (酷狗)
    private final String ALBUM_DETAIL_KG_API = "http://mobilecdn.kugou.com/api/v3/album/info?version=9108&albumid=%s";
    //    private final String ALBUM_DETAIL_KG_API = "/kmr/v2/albums";
    // 专辑歌曲 API (酷狗)
//    private final String ALBUM_SONGS_KG_API = "http://mobilecdn.kugou.com/api/v3/album/song?version=9108&albumid=%s&page=%s&pagesize=%s";
    private final String ALBUM_SONGS_KG_API = "/v1/album_audio/lite";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String albumId = albumJson.getString("albumid");
        String name = albumJson.getString("albumname");
        String artist = albumJson.getString("singername");
        String artistId = albumJson.getString("singerid");
        String publishTime = albumJson.getString("publishtime").split(" ")[0];
        String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
//                Integer songNum = albumJson.getIntValue("songcount");

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetResourceSource.KG);
        albumInfo.setId(albumId);
        albumInfo.setName(name);
        albumInfo.setArtist(artist);
        albumInfo.setArtistId(artistId);
        albumInfo.setPublishTime(publishTime);
        albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setSongNum(songNum);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            albumInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(albumInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");

        String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
        String description = data.getString("intro").replace("\\n", "\n");

        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        albumInfo.setDescription(description);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = albumInfo.getId();
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_KG_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
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
//                String albumName = songJson.getString("remark");
//                String alId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getString("mvhash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(alId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//
//                res.add(musicInfo);
//            }

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ALBUM_SONGS_KG_API);
        String dat = String.format("{\"album_id\":\"%s\",\"is_buy\":\"\",\"page\":%s,\"pagesize\":%s}", id, page, limit);
        String albumInfoBody = SdkCommon.kgRequest(null, dat, options)
                .header("x-router", "openapi.kugou.com")
                .header("kg-tid", "255")
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("songs");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject audioInfo = songJson.getJSONObject("audio_info");
            JSONObject base = songJson.getJSONObject("base");
            JSONObject album_info = songJson.getJSONObject("album_info");

            String hash = audioInfo.getString("hash");
            String songId = base.getString("album_audio_id");
            String name = base.getString("audio_name");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = album_info.getString("album_name");
            String albumId = base.getString("album_id");
            Double duration = audioInfo.getDouble("duration") / 1000;
            JSONArray mvdata = songJson.getJSONArray("mvdata");
            String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
            int qualityType = AudioQuality.UNKNOWN;
            if (audioInfo.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
            else if (audioInfo.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
            else if (audioInfo.getLong("filesize_320") != 0) qualityType = AudioQuality.HQ;
            else if (audioInfo.getLong("filesize_128") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.KG);
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
