package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsMusicSearchReq {
    private static QsMusicSearchReq instance;

    private QsMusicSearchReq() {
    }

    public static QsMusicSearchReq getInstance() {
        if (instance == null) instance = new QsMusicSearchReq();
        return instance;
    }

    // 搜索歌曲 API (汽水)
    private final String SEARCH_MUSIC_QS_API = "https://api.qishui.com/luna/search/track";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        // 每页固定 20 条
        int lim = Math.min(20, limit);
        Map<String, Object> params = new HashMap<>();
        params.put("q", keyword);
        params.put("cursor", (page - 1) * lim);
        params.put("count", lim);
        String musicInfoBody = SdkCommon.qsRequest(SEARCH_MUSIC_QS_API, params)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray resultGroups = musicInfoJson.getJSONArray("result_groups");
        if (JsonUtil.notEmpty(resultGroups)) {
            JSONObject result = resultGroups.getJSONObject(0);
            JSONArray songArray = result.getJSONArray("data");
            t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("entity").getJSONObject("track");
                JSONObject albumJson = songJson.getJSONObject("album");

                String songId = songJson.getString("id");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("duration") / 1000;
                String mvId = songJson.getString("vid");
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
                musicInfo.setMvId(mvId);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
