package net.doge.sdk.service.rank.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class KgRankInfoReq {
    private static KgRankInfoReq instance;

    private KgRankInfoReq() {
    }

    public static KgRankInfoReq getInstance() {
        if (instance == null) instance = new KgRankInfoReq();
        return instance;
    }

    // 榜单信息 API (酷狗)
    private final String RANK_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=%s&page=%s&pagesize=%s";

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankInfoBody = HttpRequest.get(String.format(RANK_DETAIL_KG_API, id, page, limit))
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("info");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String hash = songJson.getString("hash");
            String songId = songJson.getString("album_audio_id");
            String name = songJson.getString("songname");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("remark");
            String albumId = songJson.getString("album_id");
            Double duration = songJson.getDouble("duration");
            JSONArray mvdata = songJson.getJSONArray("mvdata");
            String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
            int qualityType = AudioQuality.UNKNOWN;
            if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
            else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
            else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
            else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.KG);
            musicInfo.setHash(hash);
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

        return new CommonResult<>(res, total);
    }
}
