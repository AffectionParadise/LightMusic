package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
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
    // 搜索歌曲兜底 API (汽水)
    private final String SEARCH_MUSIC_FALLBACK_QS_API = "https://api-vehicle.volcengine.com/v2/search/type";

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
        // 由于汽水搜索 API 鉴权问题，返回空 Body 用下游代替
        if (StringUtil.isEmpty(musicInfoBody)) return searchMusicFallback(keyword, page, limit);
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        if (JsonUtil.notEmpty(musicInfoJson)) {
            JSONArray resultGroups = musicInfoJson.getJSONArray("result_groups");
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
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray bitRates = songJson.getJSONArray("bit_rates");
                if (JsonUtil.hasObject(bitRates, "quality", "hi_res"))
                    qualityType = AudioQuality.HR;
                else if (JsonUtil.hasObject(bitRates, "quality", "lossless"))
                    qualityType = AudioQuality.SQ;
                else if (JsonUtil.hasObject(bitRates, "quality", "highest"))
                    qualityType = AudioQuality.HQ;
                else if (JsonUtil.hasObject(bitRates, "quality", "higher"))
                    qualityType = AudioQuality.MQ;
                else if (JsonUtil.hasObject(bitRates, "quality", "medium"))
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

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 根据关键词获取歌曲(兜底)
     */
    public CommonResult<NetMusicInfo> searchMusicFallback(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        // real_offset 参数无效，程序分页
        final int lim = 200;
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("search_type", "music");
        params.put("real_offset", 0);
        params.put("limit", lim);
        params.put("search_source", "qishui");
        String musicInfoBody = HttpRequest.get(SEARCH_MUSIC_FALLBACK_QS_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        JSONArray songArray = data.getJSONArray("list");
        t = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("item_id");
            String songName = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            Double duration = songJson.getDouble("duration");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QS);
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setDuration(duration);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
