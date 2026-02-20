package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiPlaylistInfoReq {
    private static QiPlaylistInfoReq instance;

    private QiPlaylistInfoReq() {
    }

    public static QiPlaylistInfoReq getInstance() {
        if (instance == null) instance = new QiPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (千千)
    private final String PLAYLIST_DETAIL_QI_API = "https://music.91q.com/v1/tracklist/info?appid=16073360&id=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, 1, 1, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(playlistJson)) {
            String playlistId = playlistJson.getString("id");
            String name = playlistJson.getString("title");
            Integer trackCount = playlistJson.getIntValue("trackCount");
            String coverImgThumbUrl = playlistJson.getString("pic");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.QI);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(name);
            playlistInfo.setTrackCount(trackCount);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(playlistInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        String id = playlistInfo.getId();
        String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, 1, 1, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("data");

        String coverImgUrl = playlistJson.getString("pic");
        String description = playlistJson.getString("desc");

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(playlistJson));
        if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(playlistJson.getIntValue("trackCount"));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, page, limit, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        total = data.getIntValue("trackCount");
        JSONArray songArray = data.getJSONArray("trackList");
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
