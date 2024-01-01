package net.doge.sdk.entity.music.rcmd;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.AudioQuality;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.entity.playlist.info.PlaylistInfoReq;
import net.doge.sdk.entity.ranking.info.RankingInfoReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class HotMusicRecommendReq {
    private static HotMusicRecommendReq instance;

    private HotMusicRecommendReq() {
    }

    public static HotMusicRecommendReq getInstance() {
        if (instance == null) instance = new HotMusicRecommendReq();
        return instance;
    }

    // 曲风歌曲(最热) API
    private final String STYLE_HOT_SONG_API = "https://music.163.com/api/style-tag/home/song";
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
    // 飙升榜 API (酷我)
//    private final String UP_MUSIC_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=93&pn=%s&rn=%s&httpsStatus=1";
    // 热歌榜 API (酷我)
//    private final String HOT_MUSIC_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";
    // 尖叫热歌榜 API (咪咕)
//    private final String HOT_MUSIC_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=27186466";
    // 热歌 API (音乐磁场)
    private final String HOT_MUSIC_HF_API = "https://www.hifini.com/%s-%s.htm";
    // 热歌 API (咕咕咕音乐)
    private final String HOT_MUSIC_GG_API = "http://www.gggmusic.com/%s-%s.htm";
    // 传播最快(原唱) API (5sing)
    private final String SPREAD_YC_MUSIC_FS_API = "http://5sing.kugou.com/yc/spread/more_%s.shtml";
    // 分享最多(原唱) API (5sing)
    private final String SHARE_YC_MUSIC_FS_API = "http://5sing.kugou.com/yc/share/more_%s.shtml";
    // 传播最快(翻唱) API (5sing)
    private final String SPREAD_FC_MUSIC_FS_API = "http://5sing.kugou.com/fc/spread/more_%s.shtml";
    // 分享最多(翻唱) API (5sing)
    private final String SHARE_FC_MUSIC_FS_API = "http://5sing.kugou.com/fc/share/more_%s.shtml";
    // 热门伴奏(伴奏) API (5sing)
    private final String HOT_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/rmsong/more_%s.shtml";
    // 下载排行(伴奏) API (5sing)
    private final String RANK_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/xzsong/more_%s.shtml";
    // 发姐歌曲 API (发姐)
    private final String HOT_MUSIC_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";

    /**
     * 获取飙升歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusicRecommend(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> res = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.hotSongTag.get(tag);

        // 网易云(榜单就是歌单，固定榜单 id 直接请求歌单音乐接口，接口分页)
        PlaylistInfoReq playlistInfoReq = PlaylistInfoReq.getInstance();
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusic = () -> playlistInfoReq.getMusicInfoInPlaylist(String.valueOf(19723756), NetMusicSource.NC, limit, page);
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusic = () -> playlistInfoReq.getMusicInfoInPlaylist(String.valueOf(3778678), NetMusicSource.NC, limit, page);
        // 曲风歌曲(最热)
        Callable<CommonResult<NetMusicInfo>> getStyleHotSong = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String musicInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_HOT_SONG_API,
                                String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", s[0], (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                JSONArray songArray = data.getJSONArray("songs");
                t = data.getJSONObject("page").getIntValue("total");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);
                    JSONObject albumJson = songJson.getJSONObject("al");

                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name").trim();
                    String artist = SdkUtil.parseArtist(songJson);
                    String artistId = SdkUtil.parseArtistId(songJson);
                    String albumName = albumJson.getString("name");
                    String albumId = albumJson.getString("id");
                    Double duration = songJson.getDouble("dt") / 1000;
                    String mvId = songJson.getString("mv");
                    int qualityType = AudioQuality.UNKNOWN;
                    if (JsonUtil.notEmpty(songJson.getJSONObject("hr"))) qualityType = AudioQuality.HR;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("sq"))) qualityType = AudioQuality.SQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("h"))) qualityType = AudioQuality.HQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("m"))) qualityType = AudioQuality.MQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("l"))) qualityType = AudioQuality.LQ;

                    NetMusicInfo musicInfo = new NetMusicInfo();
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

            return new CommonResult<>(r, t);
        };

        // 酷狗
        // 歌曲推荐
        Callable<CommonResult<NetMusicInfo>> getCardSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

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
                        .executeAsync()
                        .body();
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
        };
        // 主题歌曲
        Callable<CommonResult<NetMusicInfo>> getThemeSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(THEME_SONG_KG_API);
                long ct = System.currentTimeMillis() / 1000;
                String dat = String.format("{\"platform\":\"android\",\"clienttime\":%s,\"theme_category_id\":\"%s\"," +
                        "\"show_theme_category_id\":0,\"userid\":0,\"module_id\":508}", ct, s[2]);
                String musicInfoBody = SdkCommon.kgRequest(null, dat, options)
                        .executeAsync()
                        .body();
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
        };
        // 频道歌曲
        Callable<CommonResult<NetMusicInfo>> getFmSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

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
                        .executeAsync()
                        .body();
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
        };
        // 编辑精选歌曲
        Callable<CommonResult<NetMusicInfo>> getIpSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_SONG_KG_API);
                String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", s[4], page, limit);
                String musicInfoBody = SdkCommon.kgRequest(null, dat, options)
                        .executeAsync()
                        .body();
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
        };
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKg = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(6666), NetMusicSource.KG, limit, page);

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
        };
        // TOP500
        Callable<CommonResult<NetMusicInfo>> getTop500Kg = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(8888), NetMusicSource.KG, limit, page);

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
        };

        // QQ
        // 流行指数榜
        Callable<CommonResult<NetMusicInfo>> getPopularMusicQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                            "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", 4, (page - 1) * limit, limit))
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
            t = data.getJSONObject("data").getIntValue("totalNum");
            JSONArray songArray = data.getJSONArray("songInfoList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");
                JSONObject fileJson = songJson.getJSONObject("file");

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
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
                musicInfo.setId(id);
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
        };
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                            "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", 26, (page - 1) * limit, limit))
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
            t = data.getJSONObject("data").getIntValue("totalNum");
            JSONArray songArray = data.getJSONArray("songInfoList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");
                JSONObject fileJson = songJson.getJSONObject("file");

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
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
                musicInfo.setId(id);
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
        };

        // 酷我
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKw = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(93), NetMusicSource.KW, limit, page);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(UP_MUSIC_KW_API, page, limit)).executeAsync();
//            if (resp.getStatus() == HttpStatus.HTTP_OK) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String id = songJson.getString("rid");
//                    String name = songJson.getString("name");
//                    String artist = songJson.getString("artist").replace("&", "、");
//                    String artistId = songJson.getString("artistid");
//                    String albumName = songJson.getString("album");
//                    String albumId = songJson.getString("albumid");
//                    Double duration = songJson.getDouble("duration");
//                    String mvId = songJson.getIntValue("hasmv") == 0 ? "" : id;
//                    String formats = songJson.getString("formats");
//                    int qualityType = AudioQuality.UNKNOWN;
//                    if (formats.contains("HIRFLAC")) qualityType = AudioQuality.HR;
//                    else if (formats.contains("ALFLAC")) qualityType = AudioQuality.SQ;
//                    else if (formats.contains("MP3H")) qualityType = AudioQuality.HQ;
//                    else if (formats.contains("MP3128")) qualityType = AudioQuality.LQ;
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.KW);
//                    musicInfo.setId(id);
//                    musicInfo.setName(name);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setDuration(duration);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
        };
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicKw = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(16), NetMusicSource.KW, limit, page);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(HOT_MUSIC_KW_API, page, limit)).executeAsync();
//            if (resp.getStatus() == HttpStatus.HTTP_OK) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String id = songJson.getString("rid");
//                    String name = songJson.getString("name");
//                    String artist = songJson.getString("artist").replace("&", "、");
//                    String artistId = songJson.getString("artistid");
//                    String albumName = songJson.getString("album");
//                    String albumId = songJson.getString("albumid");
//                    Double duration = songJson.getDouble("duration");
//                    String mvId = songJson.getIntValue("hasmv") == 0 ? "" : id;
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.KW);
//                    musicInfo.setId(id);
//                    musicInfo.setName(name);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setDuration(duration);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
        };

        // 咪咕
        // 尖叫热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicMg = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(27186466), NetMusicSource.MG, limit, page);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String rankingInfoBody = HttpRequest.get(HOT_MUSIC_MG_API)
//                    .executeAsync()
//                    .body();
//            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
//            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
//            t = data.getIntValue("contentsCount");
//            JSONArray songArray = data.getJSONArray("contents");
//            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");
//
//                String songId = songJson.getString("copyrightId");
//                // 过滤掉不是歌曲的 objectInfo
//                if (StringUtil.isEmpty(songId)) continue;
//                String name = songJson.getString("songName");
//                String artist = songJson.getString("singer");
//                String artistId = songJson.getString("singerId");
//                String albumName = songJson.getString("album");
//                String albumId = songJson.getString("albumId");
//                Double duration = TimeUtil.toSeconds(songJson.getString("length"));
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.MG);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//
//                r.add(musicInfo);
//            }
//            return new CommonResult<>(r, t);
        };

        // 音乐磁场
        Callable<CommonResult<NetMusicInfo>> getHotMusicHf = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_HF_API, s[5], page))
                        .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                        .cookie(SdkCommon.HF_COOKIE)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".media.thread.tap");
                Elements ap = doc.select("a.page-link");
                String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
                if (StringUtil.isEmpty(ts))
                    ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
                boolean hasTs = StringUtil.notEmpty(ts);
                if (hasTs) t = Integer.parseInt(ts) * limit;
                else t = songs.size();
                for (int i = hasTs ? 0 : (page - 1) * limit, len = hasTs ? songs.size() : Math.min(songs.size(), page * limit); i < len; i++) {
                    Element song = songs.get(i);

                    Elements a = song.select(".subject.break-all a");
                    Element span = song.select(".username.text-grey.mr-1").first();

                    String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
                    String songName = a.text();
                    String artist = span.text();
                    String artistId = span.attr("uid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.HF);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咕咕咕音乐
        Callable<CommonResult<NetMusicInfo>> getHotMusicGg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_GG_API, s[6], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".media.thread.tap");
                Elements ap = doc.select("a.page-link");
                String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
                if (StringUtil.isEmpty(ts))
                    ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
                boolean hasTs = StringUtil.notEmpty(ts);
                if (hasTs) t = Integer.parseInt(ts) * limit;
                else t = songs.size();
                for (int i = hasTs ? 0 : (page - 1) * limit, len = hasTs ? songs.size() : Math.min(songs.size(), page * limit); i < len; i++) {
                    Element song = songs.get(i);

                    Elements a = song.select(".subject.break-all a");

                    String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
                    String songName = a.text();

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.GG);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 5sing
        // 传播最快(原唱)
        Callable<CommonResult<NetMusicInfo>> getSpreadYcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SPREAD_YC_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(em.text()) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements a = song.select("h3 a");
                    Elements pa = song.select("p.m_z a");

                    String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 分享最多(原唱)
        Callable<CommonResult<NetMusicInfo>> getShareYcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SHARE_YC_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(em.text()) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements a = song.select("h3 a");
                    Elements pa = song.select("p.m_z a");

                    String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 传播最快(翻唱)
        Callable<CommonResult<NetMusicInfo>> getSpreadFcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SPREAD_FC_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(em.text()) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements a = song.select("h3 a");
                    Elements pa = song.select("p.m_z a");

                    String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 分享最多(翻唱)
        Callable<CommonResult<NetMusicInfo>> getShareFcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SHARE_FC_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(em.text()) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements a = song.select("h3 a");
                    Elements pa = song.select("p.m_z a");

                    String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 热门伴奏(伴奏)
        Callable<CommonResult<NetMusicInfo>> getHotBzSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(HOT_BZ_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select("tr");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(RegexUtil.getGroup1("\\d+/(\\d+)", em.text())) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements td = song.select("td");
                    // 排除表头
                    if (td.isEmpty()) continue;

                    Elements a = song.select(".aleft a");
                    Elements pa = td.get(2).select("a");

                    String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = pa.text();
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 下载排行(伴奏)
        Callable<CommonResult<NetMusicInfo>> getRankBzSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(RANK_BZ_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select("tr");
            if (!songs.isEmpty()) {
                Elements em = doc.select(".page_num em");
                t = Integer.parseInt(RegexUtil.getGroup1("\\d+/(\\d+)", em.text())) * limit;
                for (int i = 0, len = songs.size(); i < len; i++) {
                    Element song = songs.get(i);
                    Elements td = song.select("td");
                    // 排除表头
                    if (td.isEmpty()) continue;

                    Elements a = song.select(".aleft a");
                    Elements pa = td.get(2).select("a");

                    String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                    String songName = a.text();
                    String artist = pa.text();
                    String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 发姐
        Callable<CommonResult<NetMusicInfo>> getHotMusicFa = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            // 获取发姐请求参数
            String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(body);
            Elements ap = doc.select("#aplayer2");
            String musicSet = StringUtil.urlEncodeAll(ap.attr("data-songs"));
            String _nonce = ap.attr("data-_nonce");

            String songInfoBody = HttpRequest.get(String.format(HOT_MUSIC_FA_API, musicSet, _nonce))
                    .executeAsync()
                    .body();
            JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
            JSONObject data = songInfoJson.getJSONObject("msg");
            JSONArray songArray = data.getJSONArray("songs");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("title");
                String artist = songJson.getString("author");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FA);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusic));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusic));
            }
            if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCardSongKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusicKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(getTop500Kg));
            }
            if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getPopularMusicQq));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicQq));
            }
            if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusicKw));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicKw));
            }
            if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicMg));
            }
            if (src == NetMusicSource.HF || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicHf));
            }
            if (src == NetMusicSource.GG || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicGg));
            }
            if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getSpreadYcSongFs));
                taskList.add(GlobalExecutors.requestExecutor.submit(getShareYcSongFs));
                taskList.add(GlobalExecutors.requestExecutor.submit(getSpreadFcSongFs));
                taskList.add(GlobalExecutors.requestExecutor.submit(getShareFcSongFs));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotBzSongFs));
                taskList.add(GlobalExecutors.requestExecutor.submit(getRankBzSongFs));
            }
            if (src == NetMusicSource.FA || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicFa));
            }
        } else {
            if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
                taskList.add(GlobalExecutors.requestExecutor.submit(getStyleHotSong));
            if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCardSongKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(getThemeSongKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(getFmSongKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(getIpSongKg));
            }
            if (src == NetMusicSource.HF || src == NetMusicSource.ALL)
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicHf));
            if (src == NetMusicSource.GG || src == NetMusicSource.ALL)
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicGg));
        }

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get());
    }
}
