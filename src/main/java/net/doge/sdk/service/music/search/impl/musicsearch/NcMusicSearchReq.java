package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.PageUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.HtmlUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class NcMusicSearchReq {
    private static NcMusicSearchReq instance;

    private NcMusicSearchReq() {
    }

    public static NcMusicSearchReq getInstance() {
        if (instance == null) instance = new NcMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲/声音/歌词 API (网易云)
    private final String CLOUD_SEARCH_NC_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    private final String SEARCH_VOICE_NC_API = "https://music.163.com/api/search/voice/get";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String musicInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":1,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject result = musicInfoJson.getJSONObject("result");
        if (JsonUtil.notEmpty(result)) {
            t = result.getIntValue("songCount");
            JSONArray songArray = result.getJSONArray("songs");
            if (JsonUtil.notEmpty(songArray)) {
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
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 根据歌词关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusicByLyric(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String musicInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":1006,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject result = musicInfoJson.getJSONObject("result");
        if (JsonUtil.notEmpty(result)) {
            t = result.getIntValue("songCount");
            JSONArray songs = result.getJSONArray("songs");
            if (JsonUtil.notEmpty(songs)) {
                for (int i = 0, len = songs.size(); i < len; i++) {
                    JSONObject songJson = songs.getJSONObject(i);
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
//                        String lyricMatch = songJson.getJSONObject("lyrics").getString("txt").replace("\n", " / ");
                    JSONArray lyrics = songJson.getJSONArray("lyrics");
                    String lyricMatch = null;
                    if (JsonUtil.notEmpty(lyrics)) {
                        StringJoiner sj = new StringJoiner(" / ");
                        for (int j = 0, size = lyrics.size(); j < size; j++) {
                            sj.add(lyrics.getString(j));
                        }
                        lyricMatch = HtmlUtil.removeHtmlLabel(sj.toString());
                    }

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
                    musicInfo.setLyricMatch(lyricMatch);

                    r.add(musicInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 根据关键词获取声音
     */
    public CommonResult<NetMusicInfo> searchVoice(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        final int lim = Math.min(20, limit);
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String musicInfoBody = SdkCommon.ncRequest(Method.POST, SEARCH_VOICE_NC_API,
                        String.format("{\"keyword\":\"%s\",\"scene\":\"normal\",\"offset\":%s,\"limit\":%s}", keyword, (page - 1) * lim, lim), options)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        int to = data.getIntValue("totalCount");
        t = PageUtil.totalPage(to, lim) * limit;
        JSONArray songArray = data.getJSONArray("resources");
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject programJson = songArray.getJSONObject(i).getJSONObject("baseInfo");
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String artistId = djJson.getString("userId");
                String albumName = radioJson.getString("name");
                String albumId = radioJson.getString("id");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
