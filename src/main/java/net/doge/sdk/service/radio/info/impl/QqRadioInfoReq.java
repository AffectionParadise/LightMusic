package net.doge.sdk.service.radio.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class QqRadioInfoReq {
    private static QqRadioInfoReq instance;

    private QqRadioInfoReq() {
    }

    public static QqRadioInfoReq getInstance() {
        if (instance == null) instance = new QqRadioInfoReq();
        return instance;
    }

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
        int total = 0;

        String id = radioInfo.getId();
        String radioInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"songlist\":{\"module\":\"mb_track_radio_svr\",\"method\":\"get_radio_track\"," +
                        "\"param\":{\"id\":%s,\"firstplay\":1,\"num\":15}},\"radiolist\":{\"module\":\"pf.radiosvr\"," +
                        "\"method\":\"GetRadiolist\",\"param\":{\"ct\":\"24\"}},\"comm\":{\"ct\":24,\"cv\":0}}", id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray songArray = radioInfoJson.getJSONObject("songlist").getJSONObject("data").getJSONArray("tracks");
        if (JsonUtil.notEmpty(songArray)) {
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");
                JSONObject fileJson = songJson.getJSONObject("file");

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("title");
                String albumId = albumJson.getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");
                int qualityType = AudioQuality.UNKNOWN;
                if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
                else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
                else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
                else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
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
