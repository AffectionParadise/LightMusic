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

public class MgMusicSearchReq {
    private static MgMusicSearchReq instance;

    private MgMusicSearchReq() {
    }

    public static MgMusicSearchReq getInstance() {
        if (instance == null) instance = new MgMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (咪咕)
//    private final String SEARCH_MUSIC_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=2&keyword=%s&pgc=%s&rows=%s";
    // 关键词搜索歌曲 API (搜歌词) (咪咕)
//    private final String SEARCH_MUSIC_BY_LYRIC_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=7&keyword=%s&pgc=%s&rows=%s";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        //            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_MG_API, encodedKeyword, page, limit))
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
//                    .executeAsync()
//                    .body();
//            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//            t = musicInfoJson.getIntValue("pgt");
//            JSONArray songArray = musicInfoJson.getJSONArray("musics");
//            if (JsonUtil.notEmpty(songArray)) {
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String songId = songJson.getString("copyrightId");
//                    String songName = songJson.getString("songName");
//                    String artist = songJson.getString("singerName").replace(", ", "、");
//                    String artistId = songJson.getString("singerId").split(", ")[0];
//                    String albumName = songJson.getString("albumName");
//                    String albumId = songJson.getString("albumId");
//                    String mvId = songJson.getString("mvCopyrightId");
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.MG);
//                    musicInfo.setId(songId);
//                    musicInfo.setName(songName);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//
//            return new CommonResult<>(r, t);
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = SdkCommon.mgSearchRequest("song", keyword, page, limit)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("songResultData");
        t = data.getIntValue("totalCount");
        JSONArray songArray = data.getJSONArray("resultList");
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONArray innerArray = songArray.getJSONArray(i);
                for (int j = 0, size = innerArray.size(); j < size; j++) {
                    JSONObject songJson = innerArray.getJSONObject(j);

                    String songId = songJson.getString("copyrightId");
                    String songName = songJson.getString("songName");
                    String artist = SdkUtil.parseArtist(songJson);
                    String artistId = SdkUtil.parseArtistId(songJson);
                    String albumName = songJson.getString("album");
                    String albumId = songJson.getString("albumId");
                    double duration = songJson.getDouble("duration");
                    String mvId = songJson.getString("mvId");
                    int qualityType = AudioQuality.UNKNOWN;
                    JSONArray audioFormats = songJson.getJSONArray("audioFormats");
                    for (int k = audioFormats.size() - 1; k >= 0; k--) {
                        String formatType = audioFormats.getJSONObject(k).getString("formatType");
                        if ("ZQ24".equals(formatType)) qualityType = AudioQuality.HR;
                        else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                        else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                        else if ("PQ".equals(formatType)) qualityType = AudioQuality.LQ;
                        if (qualityType != AudioQuality.UNKNOWN) break;
                    }

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetResourceSource.MG);
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
            }
        }

        return new CommonResult<>(r, t);
    }

    /**
     * 根据歌词关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusicByLyric(String keyword, int page, int limit) {
        //            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_MG_API, encodedKeyword, page, limit))
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
//                    .executeAsync()
//                    .body();
//            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//            t = musicInfoJson.getIntValue("pgt");
//            JSONArray songArray = musicInfoJson.getJSONArray("songs");
//            if (JsonUtil.notEmpty(songArray)) {
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String songId = songJson.getString("copyrightId");
//                    String songName = songJson.getString("songName");
//                    String artist = songJson.getString("singerName").replace(", ", "、");
//                    String artistId = songJson.getString("singerId").split(", ")[0];
//                    String albumName = songJson.getString("albumName");
//                    String albumId = songJson.getString("albumId");
//                    String mvId = songJson.getString("mvCopyrightId");
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.MG);
//                    musicInfo.setId(songId);
//                    musicInfo.setName(songName);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//
//            return new CommonResult<>(r, t);
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = SdkCommon.mgSearchRequest("lyric", keyword, page, limit)
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("lyricResultData");
        t = data.getIntValue("totalCount");
        JSONArray songArray = data.getJSONArray("result");
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("copyrightId");
                String songName = songJson.getString("songName");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumId");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvId");
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray audioFormats = songJson.getJSONArray("audioFormats");
                for (int k = audioFormats.size() - 1; k >= 0; k--) {
                    String formatType = audioFormats.getJSONObject(k).getString("formatType");
                    if ("ZQ24".equals(formatType)) qualityType = AudioQuality.HR;
                    else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                    else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                    else if ("PQ".equals(formatType)) qualityType = AudioQuality.LQ;
                    if (qualityType != AudioQuality.UNKNOWN) break;
                }
                String lyricMatch = songJson.getString("multiLyricStr").replace("\n", " / ");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetResourceSource.MG);
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
        }

        return new CommonResult<>(r, t);
    }
}
