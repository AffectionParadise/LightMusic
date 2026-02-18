package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwPlaylistInfoReq {
    private static KwPlaylistInfoReq instance;

    private KwPlaylistInfoReq() {
    }

    public static KwPlaylistInfoReq getInstance() {
        if (instance == null) instance = new KwPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (酷我)
//    private final String PLAYLIST_DETAIL_KW_API = "https://kuwo.cn/api/www/playlist/playListInfo?pid=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String PLAYLIST_DETAIL_KW_API = "http://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pid=%s&pn=%s&rn=%s&encode=utf8&keyset=pl2012&identity=kuwo&pcmp4=1&vipver=MUSIC_9.0.5.0_W1&newver=1";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        //            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, 1, 1))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
//            if (JsonUtil.notEmpty(playlistJson)) {
//                String playlistId = playlistJson.getString("id");
//                String name = playlistJson.getString("name");
//                String creator = playlistJson.getString("userName");
//                Long playCount = playlistJson.getLong("listencnt");
//                Integer trackCount = playlistJson.getIntValue("total");
//                String coverImgThumbUrl = playlistJson.getString("img");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.KW);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(name);
//                playlistInfo.setCreator(creator);
//                playlistInfo.setTrackCount(trackCount);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(playlistInfo);
//            }

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, 0, 1))
                .executeAsStr();
        JSONObject playlistJson = JSONObject.parseObject(playlistInfoBody);
        if (JsonUtil.notEmpty(playlistJson)) {
            String playlistId = playlistJson.getString("id");
            String name = playlistJson.getString("title");
            String creator = playlistJson.getString("uname");
            String creatorId = playlistJson.getString("uid");
            Long playCount = playlistJson.getLong("playnum");
            Integer trackCount = playlistJson.getIntValue("total");
            String coverImgThumbUrl = playlistJson.getString("pic");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.KW);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(name);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setTrackCount(trackCount);
            playlistInfo.setPlayCount(playCount);
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
        //            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, 1, 1))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("img500");
//            String description = data.getString("info");
//
//            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
//            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
//            playlistInfo.setDescription(description);
//            if (!playlistInfo.hasTag()) playlistInfo.setTag(data.getString("tag").replace(",", "、"));

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, 0, 1))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(playlistInfoBody);

        String coverImgUrl = data.getString("pic");
        String description = data.getString("info");

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(data.getString("tag").replace(",", "、"));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        //            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//            JSONArray songArray = data.getJSONArray("musicList");
//            total.set(data.getIntValue("total"));
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String songId = songJson.getString("rid");
//                String name = songJson.getString("name");
//                String artist = songJson.getString("artist").replace("&", "、");
//                String artistId = songJson.getString("artistid");
//                String albumName = songJson.getString("album");
//                String albumId = songJson.getString("albumid");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KW);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//
//                res.add(musicInfo);
//            }

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, page - 1, limit))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(playlistInfoBody);
        JSONArray songArray = data.getJSONArray("musiclist");
        total = data.getIntValue("total");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("name");
            String artist = songJson.getString("artist").replace("&", "、");
            String artistId = songJson.getString("artistid");
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumid");
            Double duration = songJson.getDouble("duration");
            String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
            String formats = songJson.getString("formats");
            int qualityType = AudioQuality.UNKNOWN;
            if (formats.contains("HIRFLAC")) qualityType = AudioQuality.HR;
            else if (formats.contains("ALFLAC")) qualityType = AudioQuality.SQ;
            else if (formats.contains("MP3H")) qualityType = AudioQuality.HQ;
            else if (formats.contains("MP3128")) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KW);
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
