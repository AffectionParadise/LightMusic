package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcArtistInfoReq {
    private static NcArtistInfoReq instance;

    private NcArtistInfoReq() {
    }

    public static NcArtistInfoReq getInstance() {
        if (instance == null) instance = new NcArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (网易云)
    private final String ARTIST_DETAIL_NC_API = "https://music.163.com/api/artist/head/info/get";
    // 歌手歌曲 API (网易云)
    private final String ARTIST_SONGS_NC_API = "https://music.163.com/api/v1/artist/songs";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("data").getJSONObject("artist");

        String artistId = artistJson.getString("id");
        String name = artistJson.getString("name");
        String coverImgThumbUrl = artistJson.getString("avatar");
        Integer songNum = artistJson.getIntValue("musicSize");
        Integer albumNum = artistJson.getIntValue("albumSize");
        Integer mvNum = artistJson.getIntValue("mvSize");

        NetArtistInfo artistInfo = new NetArtistInfo();
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
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("data").getJSONObject("artist");

        String coverImgUrl = artistJson.getString("avatar");
        String briefDesc = artistJson.getString("briefDesc");
        String description = StringUtil.notEmpty(briefDesc) ? briefDesc : "";

        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistJson.getIntValue("musicSize"));
        if (!artistInfo.hasAlbumNum()) artistInfo.setSongNum(artistJson.getIntValue("albumSize"));
        if (!artistInfo.hasMvNum()) artistInfo.setMvNum(artistJson.getIntValue("mvSize"));
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_SONGS_NC_API,
                        String.format("{\"id\":\"%s\",\"private_cloud\":true,\"work_type\":1,\"order\":\"hot\",\"offset\":%s,\"limit\":%s}", id, (page - 1) * limit, limit),
                        options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        total = artistInfoJson.getIntValue("total");
        JSONArray songArray = artistInfoJson.getJSONArray("songs");
        for (int i = 0, len = songArray.size(); i < len; i++) {
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
