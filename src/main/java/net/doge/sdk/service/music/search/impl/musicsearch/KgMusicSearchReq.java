package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgMusicSearchReq {
    private static KgMusicSearchReq instance;

    private KgMusicSearchReq() {
    }

    public static KgMusicSearchReq getInstance() {
        if (instance == null) instance = new KgMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (酷狗)
//    private final String SEARCH_MUSIC_KG_API = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%s&page=%s&pagesize=%s&showtype=1";
    private final String SEARCH_MUSIC_KG_API = "https://songsearch.kugou.com/song_search_v2?keyword=%s&page=%s&pagesize=%s&userid=0&clientver=&platform=WebFilter&filter=2&iscorrection=1&privilege_filter=0";
    //    private final String SEARCH_MUSIC_KG_API = "/v2/search/song";
    // 关键词搜索歌曲 API (搜歌词) (酷狗)
//    private final String SEARCH_MUSIC_BY_LYRIC_KG_API = "http://mobileservice.kugou.com/api/v3/lyric/search?keyword=%s&page=%s&pagesize=%s";
    private final String SEARCH_MUSIC_BY_LYRIC_KG_API = "/v1/search/lyric";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
//            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//            JSONObject data = musicInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("info");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("hash");
//                String songId = songJson.getString("album_audio_id");
//                String songName = songJson.getString("songname");
//                String artist = songJson.getString("singername");
//                String albumName = songJson.getString("album_name");
//                String albumId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getString("mvhash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(songName);
//                musicInfo.setArtist(artist);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//
//                r.add(musicInfo);
//            }

        String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_KG_API, encodedKeyword, page, limit))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("lists");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String hash = songJson.getString("FileHash");
            String songId = songJson.getString("ID");
            String songName = songJson.getString("SongName");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("AlbumName");
            String albumId = songJson.getString("AlbumID");
            Double duration = songJson.getDouble("Duration");
            String mvId = songJson.getString("MvHash");
            int qualityType = AudioQuality.UNKNOWN;
            if (songJson.getLong("ResFileSize") != 0) qualityType = AudioQuality.HR;
            else if (songJson.getLong("SQFileSize") != 0) qualityType = AudioQuality.SQ;
            else if (songJson.getLong("HQFileSize") != 0) qualityType = AudioQuality.HQ;
            else if (songJson.getLong("FileSize") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KG);
            musicInfo.setHash(hash);
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

//            Map<String, Object> params = new TreeMap<>();
//            params.put("platform", "AndroidFilter");
//            params.put("keyword", keyword);
//            params.put("page", page);
//            params.put("pagesize", limit);
//            params.put("albumhide", 0);
//            params.put("iscorrection", 1);
//            params.put("nocollect", 0);
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_MUSIC_KG_API);
//            String musicInfoBody = SdkCommon.kgRequest(params, null, options)
//                    .header("x-router", "complexsearch.kugou.com")
//                    .executeAsync()
//                    .body();
//            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//            JSONObject data = musicInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("lists");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("FileHash");
//                String songId = songJson.getString("ID");
//                String songName = songJson.getString("SongName");
//                String artist = SdkUtil.parseArtist(songJson);
//                String artistId = SdkUtil.parseArtistId(songJson);
//                String albumName = songJson.getString("AlbumName");
//                String albumId = songJson.getString("AlbumID");
//                Double duration = songJson.getDouble("Duration");
//                String mvId = songJson.getString("MvHash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("ResFileSize") != 0) qualityType = AudioQuality.HR;
//                else if (songJson.getLong("SQFileSize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("HQFileSize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("FileSize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(songName);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//
//                r.add(musicInfo);
//            }

        return new CommonResult<>(r, t);
    }

    /**
     * 根据歌词关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusicByLyric(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

//            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//            JSONObject data = musicInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("info");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("hash");
//                String songId = songJson.getString("album_audio_id");
//                String[] split = songJson.getString("filename").split(" - ");
//                String songName = split[split.length == 1 ? 0 : 1];
//                String artist = songJson.getString("singername");
//                String albumName = songJson.getString("remark");
//                String albumId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getString("mvhash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//                String lyricMatch = songJson.getString("lyric");
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(songName);
//                musicInfo.setArtist(artist);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//                musicInfo.setLyricMatch(lyricMatch);
//
//                r.add(musicInfo);
//            }

        Map<String, Object> params = new TreeMap<>();
        params.put("platform", "AndroidFilter");
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pagesize", limit);
        params.put("category", 1);
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_MUSIC_BY_LYRIC_KG_API);
        String musicInfoBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "complexsearch.kugou.com")
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("lists");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String hash = songJson.getString("FileHash");
            String songId = songJson.getString("MixSongID");
            String songName = songJson.getString("SongName");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("AlbumName");
            String albumId = songJson.getString("AlbumID");
            Double duration = songJson.getDouble("TimeLength");
            String mvId = songJson.getString("MvHash");
            int qualityType = AudioQuality.UNKNOWN;
            if (songJson.getLong("SQSize") != 0) qualityType = AudioQuality.SQ;
            else if (songJson.getLong("320Size") != 0) qualityType = AudioQuality.HQ;
            else if (songJson.getLong("FileSize") != 0) qualityType = AudioQuality.LQ;
            String lyricMatch = songJson.getString("Lyric");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KG);
            musicInfo.setHash(hash);
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
