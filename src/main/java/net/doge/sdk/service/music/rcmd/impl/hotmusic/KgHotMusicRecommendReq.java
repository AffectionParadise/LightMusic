package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.service.ranking.info.RankingInfoReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Header;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgHotMusicRecommendReq {
    private static KgHotMusicRecommendReq instance;

    private KgHotMusicRecommendReq() {
    }

    public static KgHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new KgHotMusicRecommendReq();
        return instance;
    }

    // 歌曲推荐 API (酷狗)
    private final String CARD_SONG_KG_API = "/singlecardrec.service/v1/single_card_recommend";
    // 主题歌曲 API (酷狗)
    private final String THEME_SONG_KG_API = "/everydayrec.service/v1/theme_category_recommend";
    // 频道歌曲 API (酷狗)
    private final String FM_SONG_KG_API = "/v1/app_song_list_offset";
    // 编辑精选歌曲 API (酷狗)
    private final String IP_SONG_KG_API = "/openapi/v1/ip/audios";
    // 飙升榜 API (酷狗)
//    private final String UP_MUSIC_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=6666&page=%s&pagesize=%s";
    // TOP500 API (酷狗)
//    private final String TOP500_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=8888&page=%s&pagesize=%s";

    /**
     * 歌曲推荐
     */
    public CommonResult<NetMusicInfo> getCardSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(CARD_SONG_KG_API);
            Map<String, Object> params = new TreeMap<>();
            params.put("card_id", s[1]);
            params.put("fakem", "60f7ebf1f812edbac3c63a7310001701760f");
            params.put("area_code", 1);
            params.put("platform", "android");
            String ct = String.valueOf(System.currentTimeMillis() / 1000);
            String dat = String.format("{\"appid\":%s,\"clientver\":%s,\"platform\":\"android\",\"clienttime\":%s," +
                            "\"userid\":%s,\"key\":\"%s\",\"fakem\":\"60f7ebf1f812edbac3c63a7310001701760f\"," +
                            "\"area_code\":1,\"mid\":\"%s\",\"uuid\":\"15e772e1213bdd0718d0c1d10d64e06f\"," +
                            "\"client_playlist\":[],\"u_info\":\"a0c35cd40af564444b5584c2754dedec\"}",
                    KugouReqBuilder.appid, KugouReqBuilder.clientver, ct, KugouReqBuilder.userid, KugouReqBuilder.signParamsKey(ct), KugouReqBuilder.mid);
            String musicInfoBody = SdkCommon.kgRequest(params, dat, options)
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

    /**
     * 主题歌曲
     */
    public CommonResult<NetMusicInfo> getThemeSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTag.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(THEME_SONG_KG_API);
            long ct = System.currentTimeMillis() / 1000;
            String dat = String.format("{\"platform\":\"android\",\"clienttime\":%s,\"theme_category_id\":\"%s\"," +
                    "\"show_theme_category_id\":0,\"userid\":0,\"module_id\":508}", ct, s[2]);
            String musicInfoBody = SdkCommon.kgRequest(null, dat, options)
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

    /**
     * 频道歌曲
     */
    public CommonResult<NetMusicInfo> getFmSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTag.get(tag);

        if (StringUtil.notEmpty(s[3])) {
            String[] sp = s[3].split(" ");
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(FM_SONG_KG_API);
            String ct = String.valueOf(System.currentTimeMillis() / 1000);
            String dat = String.format("{\"appid\":%s,\"area_code\":1,\"clienttime\":%s,\"clientver\":%s," +
                            "\"data\":[{\"fmid\":\"%s\",\"fmtype\":%s,\"offset\":\"0\",\"size\":\"20\",\"singername\":\"\"}],\"get_tracker\":1," +
                            "\"key\":\"%s\",\"mid\":\"%s\"}",
                    KugouReqBuilder.appid, ct, KugouReqBuilder.clientver, sp[0], sp[1], KugouReqBuilder.signParamsKey(ct), KugouReqBuilder.mid);
            String musicInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header("x-router", "fm.service.kugou.com")
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONArray("data").getJSONObject(0);
            JSONArray songArray = data.getJSONArray("songs");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] spl = songJson.getString("name").split(" - ");
                String name = spl[1];
                String artist = spl[0];
                String albumName = songJson.getString("topic_remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("time") / 1000;
                String mvId = songJson.getString("mvhash");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
                else if (songJson.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("320size") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("size") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
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
     * 编辑精选歌曲
     */
    public CommonResult<NetMusicInfo> getIpSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTag.get(tag);

        if (StringUtil.notEmpty(s[4])) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_SONG_KG_API);
            String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", s[4], page, limit);
            String musicInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            t = musicInfoJson.getIntValue("total");
            JSONArray songArray = musicInfoJson.getJSONArray("data");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject audioInfo = songJson.getJSONObject("audio_info");
                JSONObject base = songJson.getJSONObject("base");

                String hash = audioInfo.getString("hash");
                String songId = base.getString("album_audio_id");
                String name = base.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = base.getString("album_name");
                String albumId = base.getString("album_id");
                Double duration = audioInfo.getDouble("timelength") / 1000;
                String mvId = songJson.getJSONObject("landscape_mv").getString("video_hash");
                int qualityType = AudioQuality.UNKNOWN;
                if (audioInfo.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
                else if (audioInfo.getLong("filesize_flac") != 0) qualityType = AudioQuality.SQ;
                else if (audioInfo.getLong("filesize_320") != 0) qualityType = AudioQuality.HQ;
                else if (audioInfo.getLong("filesize") != 0) qualityType = AudioQuality.LQ;

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
     * 飙升榜
     */
    public CommonResult<NetMusicInfo> getUpMusic(int page, int limit) {
        return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(6666), NetMusicSource.KG, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String rankingInfoBody = HttpRequest.get(String.format(UP_MUSIC_KG_API, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
//            JSONObject data = rankingInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("info");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("hash");
//                String songId = songJson.getString("album_audio_id");
//                String name = songJson.getString("songname");
//                String artist = SdkUtil.parseArtist(songJson);
//                String artistId = SdkUtil.parseArtistId(songJson);
//                String albumName = songJson.getString("remark");
//                String albumId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                JSONArray mvdata = songJson.getJSONArray("mvdata");
//                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
//                else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
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
//            return new CommonResult<>(r, t);
    }

    /**
     * TOP500
     */
    public CommonResult<NetMusicInfo> getTop500(int page, int limit) {
        return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(8888), NetMusicSource.KG, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String rankingInfoBody = HttpRequest.get(String.format(TOP500_KG_API, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
//            JSONObject data = rankingInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray songArray = data.getJSONArray("info");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String hash = songJson.getString("hash");
//                String songId = songJson.getString("album_audio_id");
//                String name = songJson.getString("songname");
//                String artist = SdkUtil.parseArtist(songJson);
//                String artistId = SdkUtil.parseArtistId(songJson);
//                String albumName = songJson.getString("remark");
//                String albumId = songJson.getString("album_id");
//                Double duration = songJson.getDouble("duration");
//                JSONArray mvdata = songJson.getJSONArray("mvdata");
//                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
//                int qualityType = AudioQuality.UNKNOWN;
//                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
//                else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
//                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
//                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KG);
//                musicInfo.setHash(hash);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
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
//            return new CommonResult<>(r, t);
    }
}
