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
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QsAlbumInfoReq {
    private static QsAlbumInfoReq instance;

    private QsAlbumInfoReq() {
    }

    public static QsAlbumInfoReq getInstance() {
        if (instance == null) instance = new QsAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (汽水)
    private final String ALBUM_DETAIL_QS_API = "https://api.qishui.com/luna/pc/albums/%s";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = SdkCommon.qsRequest(String.format(ALBUM_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("album_info");

        String albumId = albumJson.getString("id");
        String albumName = albumJson.getString("name");
        String artist = SdkUtil.parseArtist(albumJson);
        String artistId = SdkUtil.parseArtistId(albumJson);
        String publishTime = TimeUtil.msToDate(albumJson.getLong("release_date") * 1000);
        Integer songNum = albumJson.getIntValue("count_tracks");
        String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(albumJson);

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetResourceSource.QS);
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

        return new CommonResult<>(res, t);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = SdkCommon.qsRequest(String.format(ALBUM_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("album_info");

        String coverImgUrl = SdkUtil.parseQsCoverImgUrl(albumJson);

        if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getIntValue("count_tracks"));
        if (!albumInfo.hasPublishTime())
            albumInfo.setPublishTime(TimeUtil.msToDate(albumJson.getLong("release_date") * 1000));

        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        albumInfo.setDescription("");
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = albumInfo.getId();
        String albumInfoBody = SdkCommon.qsRequest(String.format(ALBUM_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray songArray = albumInfoJson.getJSONArray("tracks");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("album");

            String songId = songJson.getString("id");
            String songName = songJson.getString("name");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("name");
            String albumId = albumJson.getString("id");
            Double duration = songJson.getDouble("duration") / 1000;
            int qualityType = AudioQuality.UNKNOWN;
            JSONArray bitRates = songJson.getJSONArray("bit_rates");
            if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "hi_res")))
                qualityType = AudioQuality.HR;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "lossless")))
                qualityType = AudioQuality.SQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "highest")))
                qualityType = AudioQuality.HQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "higher")))
                qualityType = AudioQuality.MQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "medium")))
                qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QS);
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
