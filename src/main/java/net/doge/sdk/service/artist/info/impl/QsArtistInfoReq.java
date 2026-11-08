package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsArtistInfoReq {
    private static QsArtistInfoReq instance;

    private QsArtistInfoReq() {
    }

    public static QsArtistInfoReq getInstance() {
        if (instance == null) instance = new QsArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (汽水)
    private final String ARTIST_DETAIL_QS_API = "https://api.qishui.com/luna/pc/artists/%s";
    // 歌手歌曲 API (汽水)
    private final String ARTIST_SONGS_QS_API = "https://api.qishui.com/luna/pc/artists/%s/tracks";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        String artistInfoBody = SdkCommon.qsRequest(String.format(ARTIST_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("artist_info");

        String artistId = artistJson.getString("id");
        String name = artistJson.getString("name");
        String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(artistJson);
        Integer songNum = artistJson.getIntValue("count_tracks");
        Integer albumNum = artistJson.getIntValue("count_albums");

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetResourceSource.QS);
        artistInfo.setId(artistId);
        artistInfo.setName(name);
        artistInfo.setSongNum(songNum);
        artistInfo.setAlbumNum(albumNum);
        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            artistInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(artistInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        String artistInfoBody = SdkCommon.qsRequest(String.format(ARTIST_DETAIL_QS_API, id), null)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("artist_info");
        JSONObject profileJson = artistJson.getJSONObject("artist_profile");

        String description = profileJson.getString("intro");
        String coverImgUrl = SdkUtil.parseQsCoverImgUrl(artistJson);

        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistInfoJson.getIntValue("count_tracks"));
        if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(artistInfoJson.getIntValue("count_albums"));
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("cursor", (page - 1) * limit);
        params.put("count", limit);
        String artistInfoBody = SdkCommon.qsRequest(String.format(ARTIST_SONGS_QS_API, id), params)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray songArray = artistInfoJson.getJSONArray("tracks");
        total = page * limit + (artistInfoJson.getBooleanValue("has_more") ? 1 : 0);
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
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

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
