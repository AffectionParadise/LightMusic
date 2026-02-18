package net.doge.sdk.service.ranking.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.media.DurationUtil;

import java.util.LinkedList;
import java.util.List;

public class MgRankingInfoReq {
    private static MgRankingInfoReq instance;

    private MgRankingInfoReq() {
    }

    public static MgRankingInfoReq getInstance() {
        if (instance == null) instance = new MgRankingInfoReq();
        return instance;
    }

    // 榜单信息 API (咪咕)
    private final String RANKING_DETAIL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=%s";

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankingInfo(NetRankingInfo rankingInfo) {
        String id = rankingInfo.getId();
        String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONObject data = rankingInfoJson.getJSONObject("columnInfo");

        if (!rankingInfo.hasPlayCount())
            rankingInfo.setPlayCount(data.getJSONObject("opNumItem").getLong("playNum"));
        if (!rankingInfo.hasUpdateTime()) rankingInfo.setUpdateTime(data.getString("columnUpdateTime"));
        GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        // 咪咕需要额外补全榜单描述
        rankingInfo.setDescription(data.getString("columnDes"));
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
        total = data.getIntValue("contentsCount");
        JSONArray songArray = data.getJSONArray("contents");
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");

            String songId = songJson.getString("copyrightId");
            // 过滤掉不是歌曲的 objectInfo
            if (StringUtil.isEmpty(songId)) continue;
            String name = songJson.getString("songName");
            String artist = songJson.getString("singer");
            String artistId = songJson.getString("singerId");
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumId");
            Double duration = DurationUtil.toSeconds(songJson.getString("length"));
            int qualityType = AudioQuality.UNKNOWN;
            JSONArray newRateFormats = songJson.getJSONArray("newRateFormats");
            for (int k = newRateFormats.size() - 1; k >= 0; k--) {
                String formatType = newRateFormats.getJSONObject(k).getString("formatType");
                if ("ZQ".equals(formatType)) qualityType = AudioQuality.HR;
                else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                else if ("PQ".equals(formatType)) qualityType = AudioQuality.MQ;
                if (qualityType != AudioQuality.UNKNOWN) break;
            }

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.MG);
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
