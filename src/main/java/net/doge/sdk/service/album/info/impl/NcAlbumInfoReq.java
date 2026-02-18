package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
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

public class NcAlbumInfoReq {
    private static NcAlbumInfoReq instance;

    private NcAlbumInfoReq() {
    }

    public static NcAlbumInfoReq getInstance() {
        if (instance == null) instance = new NcAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (网易云)
    private final String ALBUM_DETAIL_NC_API = "https://music.163.com/weapi/v1/album/%s";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, String.format(ALBUM_DETAIL_NC_API, id), "{}", options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("album");

        String albumId = albumJson.getString("id");
        String name = albumJson.getString("name");
        String artist = SdkUtil.parseArtist(albumJson);
        String artistId = SdkUtil.parseArtistId(albumJson);
        String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
        String coverImgThumbUrl = albumJson.getString("picUrl");
        Integer songNum = albumJson.getIntValue("size");

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setId(albumId);
        albumInfo.setName(name);
        albumInfo.setArtist(artist);
        albumInfo.setArtistId(artistId);
        albumInfo.setPublishTime(publishTime);
        albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, String.format(ALBUM_DETAIL_NC_API, id), "{}", options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("album");

        String coverImgUrl = albumJson.getString("picUrl");
        String description = albumJson.getString("description");
        if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getIntValue("size"));
        if (!albumInfo.hasPublishTime())
            albumInfo.setPublishTime(TimeUtil.msToDate(albumJson.getLong("publishTime")));

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
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String albumInfoBody = SdkCommon.ncRequest(Method.POST, String.format(ALBUM_DETAIL_NC_API, id), "{}", options)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray songArray = albumInfoJson.getJSONArray("songs");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("al");

            String songId = songJson.getString("id");
            String name = songJson.getString("name").trim();
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("name");
            String albumId = albumJson.getString("id");
            Double duration = songJson.getDouble("dt") / 1000;
            String mvId = songJson.getString("mv");
            int qualityType = AudioQuality.UNKNOWN;
            if (JsonUtil.notEmpty(songJson.getJSONObject("hr"))) qualityType = AudioQuality.HR;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("sq"))) qualityType = AudioQuality.SQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("h"))) qualityType = AudioQuality.HQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("m"))) qualityType = AudioQuality.MQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("l"))) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
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
