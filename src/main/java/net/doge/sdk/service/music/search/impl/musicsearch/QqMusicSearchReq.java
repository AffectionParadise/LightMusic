package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class QqMusicSearchReq {
    private static QqMusicSearchReq instance;

    private QqMusicSearchReq() {
    }

    public static QqMusicSearchReq getInstance() {
        if (instance == null) instance = new QqMusicSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 0))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray songArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
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

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 根据歌词关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusicByLyric(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 7))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray songArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
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
            String mvId = songJson.getJSONObject("mv").getString("id");
            int qualityType = AudioQuality.UNKNOWN;
            if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
            else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
            else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
            else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;
            String lyricMatch = songJson.getString("content").replace("\n", " / ");

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
            musicInfo.setLyricMatch(lyricMatch);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
