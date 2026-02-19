package net.doge.sdk.service.music.rcmd.impl.newmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgNewMusicReq {
    private static KgNewMusicReq instance;

    private KgNewMusicReq() {
    }

    public static KgNewMusicReq getInstance() {
        if (instance == null) instance = new KgNewMusicReq();
        return instance;
    }

    // 新歌速递 API (酷狗)
    private final String RECOMMEND_NEW_SONG_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/newsong?version=9108&type=%s&page=%s&pagesize=%s";
    //    private final String RECOMMEND_NEW_SONG_KG_API = "/musicadservice/container/v1/newsong_publish";
    // 每日推荐歌曲 API (酷狗)
    private final String EVERYDAY_SONG_KG_API = "/everyday_song_recommend";
    // 风格歌曲 API (酷狗)
    private final String STYLE_SONG_KG_API = "/everydayrec.service/everyday_style_recommend";

    /**
     * 每日推荐歌曲
     */
    public CommonResult<NetMusicInfo> getEverydaySong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(EVERYDAY_SONG_KG_API);
        Map<String, Object> params = new TreeMap<>();
        params.put("platform", "android");
        params.put("userid", "0");
        String musicInfoBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "everydayrec.service.kugou.com")
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("data");
        JSONArray songArray = data.getJSONArray("song_list");
        t = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String hash = songJson.getString("hash");
            String songId = songJson.getString("album_audio_id");
            String name = songJson.getString("songname");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("album_name");
            String albumId = songJson.getString("album_id");
            Double duration = songJson.getDouble("time_length");
            String mvId = songJson.getString("mv_hash");
            int qualityType = AudioQuality.UNKNOWN;
            if (songJson.getLong("filesize_other") != 0) qualityType = AudioQuality.HR;
            else if (songJson.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
            else if (songJson.getLong("filesize_320") != 0) qualityType = AudioQuality.HQ;
            else if (songJson.getLong("filesize_128") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KG);
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

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 华语新歌(接口分页)
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_KG_API, s[2], page, limit))
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
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
                String mvId = songJson.getString("mvhash");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
                else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
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

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 风格歌曲
     */
    public CommonResult<NetMusicInfo> getStyleSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        if (StringUtil.notEmpty(s[3])) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(STYLE_SONG_KG_API);
            Map<String, Object> params = new TreeMap<>();
            params.put("tagids", s[3]);
            String musicInfoBody = SdkCommon.kgRequest(params, "{}", options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("song_list");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String name = songJson.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("album_name");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("time_length");
                String mvId = songJson.getString("mv_hash");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("filesize_other") != 0) qualityType = AudioQuality.HR;
                else if (songJson.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("filesize_320") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("filesize_128") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
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

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
