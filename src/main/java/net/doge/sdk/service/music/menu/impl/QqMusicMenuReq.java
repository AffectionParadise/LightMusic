package net.doge.sdk.service.music.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqMusicMenuReq {
    private static QqMusicMenuReq instance;

    private QqMusicMenuReq() {
    }

    public static QqMusicMenuReq getInstance() {
        if (instance == null) instance = new QqMusicMenuReq();
        return instance;
    }

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        List<NetMusicInfo> res = new LinkedList<>();
        int t;

        String id = netMusicInfo.getId();
        // 先根据 mid 获取 id
        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

        musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                        "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"simsongs\":{\"module\":\"rcmusic.similarSongRadioServer\"," +
                        "\"method\":\"get_simsongs\",\"param\":{\"songid\":%s}}}", id))
                .executeAsStr();
        musicInfoJson = JSONObject.parseObject(musicInfoBody).getJSONObject("simsongs").getJSONObject("data");
        JSONArray songArray = musicInfoJson.getJSONArray("songInfoList");
        t = songArray.size();
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("album");
            JSONObject fileJson = songJson.getJSONObject("file");

            String songId = songJson.getString("mid");
            String songName = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("title");
            String albumId = albumJson.getString("mid");
            Double duration = songJson.getDouble("interval");
            String mvId = songJson.getJSONObject("mv").getString("vid");
            int qualityType = AudioQuality.UNKNOWN;
            if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
            else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
            else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
            else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.QQ);
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

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        int t;

        String id = musicInfo.getId();
        // 先根据 mid 获取 id
        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

        String playlistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                        "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"gedan\":{\"module\":\"music.mb_gedan_recommend_svr\"," +
                        "\"method\":\"get_related_gedan\",\"param\":{\"sin\":0,\"last_id\":0,\"song_type\":1,\"song_id\":%s}}}", id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONObject("gedan").getJSONObject("data").getJSONArray("vec_gedan");
        t = playlistArray.size();
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("tid");
            String playlistName = playlistJson.getString("dissname");
            String creator = playlistJson.getString("creator");
            Long playCount = playlistJson.getLong("listen_num");
            Integer trackCount = playlistJson.getIntValue("song_num");
            String coverImgThumbUrl = playlistJson.getString("imgurl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.QQ);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
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
}
