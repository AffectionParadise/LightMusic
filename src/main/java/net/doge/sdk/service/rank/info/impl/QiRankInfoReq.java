package net.doge.sdk.service.rank.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.util.LinkedList;
import java.util.List;

public class QiRankInfoReq {
    private static QiRankInfoReq instance;

    private QiRankInfoReq() {
    }

    public static QiRankInfoReq getInstance() {
        if (instance == null) instance = new QiRankInfoReq();
        return instance;
    }

    // 榜单信息 API (千千)
    private final String RANK_DETAIL_QI_API = "https://music.91q.com/v1/bd/list?appid=16073360&bdid=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankInfoBody = SdkCommon.qiRequest(String.format(RANK_DETAIL_QI_API, id, page, limit, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("result");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("TSID");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("albumTitle");
            String albumId = songJson.getString("albumAssetCode");
            Double duration = songJson.getDouble("duration");
            int qualityType = AudioQuality.UNKNOWN;
            String allRate = songJson.getJSONArray("allRate").toString();
            if (allRate.contains("3000")) qualityType = AudioQuality.SQ;
            else if (allRate.contains("320")) qualityType = AudioQuality.HQ;
            else if (allRate.contains("128")) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QI);
            musicInfo.setId(songId);
            musicInfo.setName(name);
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
