package net.doge.sdk.service.music.rcmd.impl.newmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcNewMusicReq {
    private static NcNewMusicReq instance;

    private NcNewMusicReq() {
    }

    public static NcNewMusicReq getInstance() {
        if (instance == null) instance = new NcNewMusicReq();
        return instance;
    }

    // 推荐新歌 API (网易云)
    private final String RECOMMEND_NEW_SONG_NC_API = "https://music.163.com/api/personalized/newsong";
    // 曲风歌曲(最新) API (网易云)
    private final String STYLE_NEW_SONG_NC_API = "https://music.163.com/api/style-tag/home/song";
    // 新歌速递 API (网易云)
    private final String FAST_NEW_SONG_NC_API = "https://music.163.com/weapi/v1/discovery/new/songs";

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String musicInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_NEW_SONG_NC_API, "{\"type\":\"recommend\",\"limit\":100,\"areaId\":0}", options)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray songArray = musicInfoJson.getJSONArray("result");
        t = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject jsonObject = songArray.getJSONObject(i);
            JSONObject songJson;
            if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
            else songJson = jsonObject;
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

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 新歌速递
     */
    public CommonResult<NetMusicInfo> getFastNewSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, FAST_NEW_SONG_NC_API, String.format("{\"areaId\":\"%s\",\"total\":true}", s[0]), options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songArray = musicInfoJson.getJSONArray("data");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject jsonObject = songArray.getJSONObject(i);
                JSONObject songJson;
                if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
                else songJson = jsonObject;
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

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 曲风歌曲(最新)
     */
    public CommonResult<NetMusicInfo> getStyleNewSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_NEW_SONG_NC_API,
                            String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":1}", s[1], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("songs");
            t = data.getJSONObject("page").getIntValue("total");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("al");

                String songId = songJson.getString("id");
                String songName = songJson.getString("name").trim();
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
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
