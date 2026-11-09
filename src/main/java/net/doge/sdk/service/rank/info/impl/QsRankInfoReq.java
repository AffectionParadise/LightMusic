package net.doge.sdk.service.rank.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class QsRankInfoReq {
    private static QsRankInfoReq instance;

    private QsRankInfoReq() {
    }

    public static QsRankInfoReq getInstance() {
        if (instance == null) instance = new QsRankInfoReq();
        return instance;
    }

    // 榜单信息 API (汽水)
    private final String RANK_DETAIL_QS_API = "https://api.qishui.com/luna/pc/charts/%s";

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankInfoBody = SdkCommon.qsRequest(String.format(RANK_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject rankJson = rankInfoJson.getJSONObject("chart");
        total = rankJson.getIntValue("count_tracks");
        JSONArray songArray = rankJson.getJSONArray("track_ranks");
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i).getJSONObject("track");
            JSONObject albumJson = songJson.getJSONObject("album");

            String songId = songJson.getString("id");
            String songName = songJson.getString("name");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("name");
            String albumId = albumJson.getString("id");
            Double duration = songJson.getDouble("duration") / 1000;
            int qualityType = AudioQuality.UNKNOWN;
            JSONArray bitRates = songJson.getJSONArray("bit_rates");
            if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "hi_res")))
                qualityType = AudioQuality.HR;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "lossless")))
                qualityType = AudioQuality.SQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "highest")))
                qualityType = AudioQuality.HQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "higher")))
                qualityType = AudioQuality.MQ;
            else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "medium")))
                qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QS);
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
