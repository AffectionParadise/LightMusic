package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class NcPlaylistInfoReq {
    private static NcPlaylistInfoReq instance;

    private NcPlaylistInfoReq() {
    }

    public static NcPlaylistInfoReq getInstance() {
        if (instance == null) instance = new NcPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (网易云)
    private final String PLAYLIST_DETAIL_NC_API = "https://music.163.com/api/v6/playlist/detail";
    // 歌单歌曲 API (网易云)
    private final String BATCH_SONGS_DETAIL_NC_API = "https://music.163.com/api/v3/song/detail";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        int t = 1;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_NC_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");
        if (JsonUtil.notEmpty(playlistJson)) {
            JSONObject ct = playlistJson.getJSONObject("creator");

            String playlistId = playlistJson.getString("id");
            String name = playlistJson.getString("name");
            String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
            String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
            Integer trackCount = playlistJson.getIntValue("trackCount");
            Long playCount = playlistJson.getLong("playCount");
            String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setId(playlistId);
            playlistInfo.setName(name);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setTrackCount(trackCount);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(playlistInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        // 先获取 trackId 列表
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String trackIdBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_NC_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                .executeAsStr();
        JSONObject playlistJson = JSONObject.parseObject(trackIdBody).getJSONObject("playlist");
        total = playlistJson.getIntValue("trackCount");
        JSONArray trackIdArray = playlistJson.getJSONArray("trackIds");
        StringJoiner sj = new StringJoiner(",");
        for (int i = (page - 1) * limit, s = Math.min(trackIdArray.size(), page * limit); i < s; i++)
            sj.add(String.format("{'id':'%s'}", trackIdArray.getJSONObject(i).getString("id")));
        String ids = sj.toString();

        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, BATCH_SONGS_DETAIL_NC_API, String.format("{\"c\":\"[%s]\"}", ids), options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray songArray = playlistInfoJson.getJSONArray("songs");
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
