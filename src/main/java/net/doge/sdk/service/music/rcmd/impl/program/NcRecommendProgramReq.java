package net.doge.sdk.service.music.rcmd.impl.program;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.http.constant.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRecommendProgramReq {
    private static NcRecommendProgramReq instance;

    private NcRecommendProgramReq() {
    }

    public static NcRecommendProgramReq getInstance() {
        if (instance == null) instance = new NcRecommendProgramReq();
        return instance;
    }

    // 推荐节目 API (网易云)
    private final String RECOMMEND_PROGRAM_NC_API = "https://music.163.com/weapi/program/recommend/v1";
    // 推荐个性节目 API (网易云)
    private final String PERSONALIZED_PROGRAM_NC_API = "https://music.163.com/weapi/personalized/djprogram";
    // 24 小时节目榜 API (网易云)
    private final String PROGRAM_24_HOURS_TOPLIST_NC_API = "https://music.163.com/api/djprogram/toplist/hours";
    // 节目榜 API (网易云)
    private final String PROGRAM_TOPLIST_NC_API = "https://music.163.com/api/program/toplist/v1";

    /**
     * 获取推荐节目
     */
    public CommonResult<NetMusicInfo> getRecommendPrograms(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String programInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_PROGRAM_NC_API, "{\"cateId\":\"\",\"offset\":0,\"limit\":10}", options)
                .executeAsStr();
        JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
        JSONArray programArray = programInfoJson.getJSONArray("programs");
        t = programArray.size();
        for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = programArray.getJSONObject(i);
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
        return new CommonResult<>(r, t);
    }

    /**
     * 推荐个性节目
     */
    public CommonResult<NetMusicInfo> getPersonalizedPrograms(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String programInfoBody = SdkCommon.ncRequest(Method.POST, PERSONALIZED_PROGRAM_NC_API, "{}", options)
                .executeAsStr();
        JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
        JSONArray programArray = programInfoJson.getJSONArray("result");
        t = programArray.size();
        for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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
        return new CommonResult<>(r, t);
    }

    /**
     * 24 小时节目榜
     */
    public CommonResult<NetMusicInfo> get24HoursPrograms(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String programInfoBody = SdkCommon.ncRequest(Method.POST, PROGRAM_24_HOURS_TOPLIST_NC_API, "{\"limit\":100}", options)
                .executeAsStr();
        JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
        JSONArray programArray = programInfoJson.getJSONObject("data").getJSONArray("list");
        t = programArray.size();
        for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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
        return new CommonResult<>(r, t);
    }

    /**
     * 节目榜
     */
    public CommonResult<NetMusicInfo> getProgramsRanking(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String programInfoBody = SdkCommon.ncRequest(Method.POST, PROGRAM_TOPLIST_NC_API, "{\"offset\":0,\"limit\":200}", options)
                .executeAsStr();
        JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
        JSONArray programArray = programInfoJson.getJSONArray("toplist");
        t = programArray.size();
        for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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
        return new CommonResult<>(r, t);
    }
}
