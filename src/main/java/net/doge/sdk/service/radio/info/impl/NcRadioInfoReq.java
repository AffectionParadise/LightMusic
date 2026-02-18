package net.doge.sdk.service.radio.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRadioInfoReq {
    private static NcRadioInfoReq instance;

    private NcRadioInfoReq() {
    }

    public static NcRadioInfoReq getInstance() {
        if (instance == null) instance = new NcRadioInfoReq();
        return instance;
    }

    // 电台信息 API (网易云)
    private final String RADIO_DETAIL_NC_API = "https://music.163.com/api/djradio/v2/get";
    // 电台节目信息 API (网易云)
    private final String RADIO_PROGRAM_DETAIL_NC_API = "https://music.163.com/weapi/dj/program/byradio";

    /**
     * 根据电台 id 获取电台
     */
    public CommonResult<NetRadioInfo> getRadioInfo(String id) {
        List<NetRadioInfo> res = new LinkedList<>();
        Integer t = 1;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject radioJson = radioInfoJson.getJSONObject("data");
        JSONObject djJson = radioJson.getJSONObject("dj");

        String radioId = radioJson.getString("id");
        String radioName = radioJson.getString("name");
        String dj = djJson.getString("nickname");
        String djId = djJson.getString("userId");
//                Long playCount = radioJson.getLong("playCount");
        Integer trackCount = radioJson.getIntValue("programCount");
        String category = radioJson.getString("category");
        if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
        String coverImgThumbUrl = radioJson.getString("picUrl");

        NetRadioInfo radioInfo = new NetRadioInfo();
        radioInfo.setId(radioId);
        radioInfo.setName(radioName);
        radioInfo.setDj(dj);
        radioInfo.setDjId(djId);
        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
        radioInfo.setTrackCount(trackCount);
        radioInfo.setCategory(category);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            radioInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(radioInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        String id = radioInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject radioJson = radioInfoJson.getJSONObject("data");
        JSONObject dj = radioJson.getJSONObject("dj");

        String coverImgUrl = radioJson.getString("picUrl");
        String description = radioJson.getString("desc");

        if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        radioInfo.setDescription(description);
        if (!radioInfo.hasDj()) radioInfo.setDj(dj.getString("nickname"));
        if (!radioInfo.hasDjId()) radioInfo.setDjId(dj.getString("userId"));
        String category = radioJson.getString("category");
        if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
        if (!radioInfo.hasCategory()) radioInfo.setCategory(category);
        if (!radioInfo.hasTag()) radioInfo.setTag(category);
        if (!radioInfo.hasTrackCount()) radioInfo.setTrackCount(radioJson.getIntValue("programCount"));
        if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(radioJson.getLong("playCount"));
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = radioInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_PROGRAM_DETAIL_NC_API,
                        String.format("{\"radioId\":\"%s\",\"offset\":%s,\"limit\":%s,\"asc\":false}", id, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        total = radioInfoJson.getIntValue("count");
        JSONArray songArray = radioInfoJson.getJSONArray("programs");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject programJson = songArray.getJSONObject(i);
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
            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
