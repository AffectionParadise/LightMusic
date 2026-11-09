package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QsPlaylistInfoReq {
    private static QsPlaylistInfoReq instance;

    private QsPlaylistInfoReq() {
    }

    public static QsPlaylistInfoReq getInstance() {
        if (instance == null) instance = new QsPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (汽水)
    private final String PLAYLIST_DETAIL_QS_API = "https://api.qishui.com/luna/pc/playlist/detail?playlist_id=%s";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QS_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");
        if (JsonUtil.notEmpty(playlistJson)) {
            JSONObject owner = playlistJson.getJSONObject("owner");
            JSONObject stats = playlistJson.getJSONObject("stats");

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("title");
            String creator = JsonUtil.isEmpty(owner) ? "" : owner.getString("nickname");
            String creatorId = JsonUtil.isEmpty(owner) ? "" : owner.getString("id");
            Long playCount = stats.getLong("count_visible");
            Integer trackCount = playlistJson.getIntValue("count_tracks");
            String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(playlistJson);

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.QS);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setTrackCount(trackCount);
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
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QS_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");

        String coverImgUrl = SdkUtil.parseQsCoverImgUrl(playlistJson);

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription("");
        playlistInfo.setTag("");
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QS_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray songArray = playlistInfoJson.getJSONArray("media_resources");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i).getJSONObject("entity").getJSONObject("track_wrapper").getJSONObject("track");
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
