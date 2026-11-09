package net.doge.sdk.service.music.rcmd.impl.newmusic;

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
import java.util.Map;
import java.util.TreeMap;

public class QiNewMusicReq {
    private static QiNewMusicReq instance;

    private QiNewMusicReq() {
    }

    public static QiNewMusicReq getInstance() {
        if (instance == null) instance = new QiNewMusicReq();
        return instance;
    }

    // 推荐新歌 API (千千)
    private final String RECOMMEND_NEW_SONG_QI_API = "https://music.91q.com/v1/index";

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageSize", 12);
        params.put("type", "song");
        String musicInfoBody = SdkCommon.qiRequest(RECOMMEND_NEW_SONG_QI_API, params)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray dataArray = musicInfoJson.getJSONArray("data");
        JSONObject data = SdkUtil.findFeatureObj(dataArray, "type", "song");
        t = data.getIntValue("module_nums");
        JSONArray songArray = data.getJSONArray("result");
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("TSID");
            String songName = songJson.getString("title");
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
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setQualityType(qualityType);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
