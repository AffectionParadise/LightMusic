package net.doge.sdk.service.radio.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class MgRadioInfoReq {
    private static MgRadioInfoReq instance;

    private MgRadioInfoReq() {
    }

    public static MgRadioInfoReq getInstance() {
        if (instance == null) instance = new MgRadioInfoReq();
        return instance;
    }

    // 电台歌曲 API (猫耳)
    private final String RADIO_SONGS_MG_API = "https://app.c.nf.migu.cn/pc/column/music-radio/scene/song/v3.0?remain=0&actionId=1&radioId=%s&pageNo=%s";

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(radioInfo.getCoverImgUrl())));
        radioInfo.setTag("");
        radioInfo.setDescription("");
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = radioInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(RADIO_SONGS_MG_API, id, page))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");
        JSONArray songArray = data.getJSONArray("songItems");
        // 咪咕电台歌曲接口没有分页参数，永远有下一页
        total = page * limit + 1;
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("copyrightId");
                String name = songJson.getString("songName");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumId");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvId");
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray audioFormats = songJson.getJSONArray("audioFormats");
                for (int k = audioFormats.size() - 1; k >= 0; k--) {
                    String formatType = audioFormats.getJSONObject(k).getString("formatType");
                    if ("ZQ24".equals(formatType)) qualityType = AudioQuality.HR;
                    else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                    else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                    else if ("PQ".equals(formatType)) qualityType = AudioQuality.MQ;
                    if (qualityType != AudioQuality.UNKNOWN) break;
                }

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetResourceSource.MG);
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
        }

        return new CommonResult<>(res, total);
    }
}
