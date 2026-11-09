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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QiMusicSearchReq {
    private static QiMusicSearchReq instance;

    private QiMusicSearchReq() {
    }

    public static QiMusicSearchReq getInstance() {
        if (instance == null) instance = new QiMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (千千)
    private final String SEARCH_MUSIC_QI_API = "https://music.91q.com/v1/search";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageNo", page);
        params.put("pageSize", limit);
        params.put("type", 1);
        params.put("word", keyword);
        String musicInfoBody = SdkCommon.qiRequest(SEARCH_MUSIC_QI_API, params)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(data)) {
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("typeTrack");
            for (int i = 0, len = songArray.size(); i < len; i++) {
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
        }
        return new CommonResult<>(r, t);
    }
}
