package net.doge.sdk.service.music.menu.impl;

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

public class NcMusicMenuReq {
    private static NcMusicMenuReq instance;

    private NcMusicMenuReq() {
    }

    public static NcMusicMenuReq getInstance() {
        if (instance == null) instance = new NcMusicMenuReq();
        return instance;
    }

    // 相似歌曲 API (网易云)
    private final String SIMILAR_SONG_NC_API = "https://music.163.com/weapi/v1/discovery/simiSong";
    // 歌曲相关歌单 API (网易云)
    private final String RELATED_PLAYLIST_NC_API = "https://music.163.com/weapi/discovery/simiPlaylist";

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        List<NetMusicInfo> res = new LinkedList<>();
        int t;

        String id = netMusicInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String musicInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_SONG_NC_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray songArray = musicInfoJson.getJSONArray("songs");
        t = songArray.size();
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("album");

            String songId = songJson.getString("id");
            String songName = songJson.getString("name").trim();
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("name");
            String albumId = albumJson.getString("id");
            Double duration = songJson.getDouble("duration") / 1000;
            String mvId = songJson.getString("mvid");
            int qualityType = AudioQuality.UNKNOWN;
            if (JsonUtil.notEmpty(songJson.getJSONObject("hrMusic"))) qualityType = AudioQuality.HR;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("sqMusic"))) qualityType = AudioQuality.SQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("hMusic"))) qualityType = AudioQuality.HQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("mMusic"))) qualityType = AudioQuality.MQ;
            else if (JsonUtil.notEmpty(songJson.getJSONObject("lMusic"))) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        int t;

        String id = musicInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, RELATED_PLAYLIST_NC_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
        t = playlistArray.size();
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);
            JSONObject creatorJson = playlistJson.getJSONObject("creator");

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("name");
            String creator = creatorJson.getString("nickname");
            String creatorId = creatorJson.getString("userId");
            Long playCount = playlistJson.getLong("playCount");
            Integer trackCount = playlistJson.getIntValue("trackCount");
            String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setTrackCount(trackCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(playlistInfo);
        }

        return new CommonResult<>(res, t);
    }
}
