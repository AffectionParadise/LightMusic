package net.doge.sdk.entity.music.rcmd;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.core.AudioQuality;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
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

public class NewMusicReq {
    private static NewMusicReq instance;

    private NewMusicReq() {
    }

    public static NewMusicReq getInstance() {
        if (instance == null) instance = new NewMusicReq();
        return instance;
    }

    // 推荐新歌 API
    private final String RECOMMEND_NEW_SONG_API = "https://music.163.com/api/personalized/newsong";
    // 曲风歌曲(最新) API
    private final String STYLE_NEW_SONG_API = "https://music.163.com/api/style-tag/home/song";
    // 新歌速递 API
    private final String FAST_NEW_SONG_API = "https://music.163.com/weapi/v1/discovery/new/songs";
    // 新歌速递 API (酷狗)
    private final String RECOMMEND_NEW_SONG_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/newsong?version=9108&type=%s&page=%s&pagesize=%s";
    //    private final String RECOMMEND_NEW_SONG_KG_API = "/musicadservice/container/v1/newsong_publish";
    // 每日推荐歌曲 API (酷狗)
    private final String EVERYDAY_SONG_KG_API = "/everyday_song_recommend";
    // 风格歌曲 API (酷狗)
    private final String STYLE_SONG_KG_API = "/everydayrec.service/everyday_style_recommend";
    // 新歌榜 API (酷我)
    //    private final String NEW_SONG_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";
    // 推荐新歌 API (咪咕)
    private final String RECOMMEND_NEW_SONG_MG_API = "http://m.music.migu.cn/migu/remoting/cms_list_tag?nid=23853978&pageNo=%s&pageSize=%s";
    // 推荐新歌 API (千千)
    private final String RECOMMEND_NEW_SONG_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 推荐新歌 API (音乐磁场)
    private final String RECOMMEND_NEW_MUSIC_HF_API = "https://www.hifiti.com/%s-%s.htm?orderby=tid";
    // 推荐新歌 API (咕咕咕音乐)
    private final String RECOMMEND_NEW_MUSIC_GG_API = "http://www.gggmusic.com/%s-%s.htm?orderby=tid";
    // 最新上传(原唱) API (5sing)
    private final String LATEST_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=-1&s=%s&l=%s&p=%s";
    // 网站推荐(原唱) API (5sing)
    private final String WEBSITE_REC_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=1&s=%s&l=%s&p=%s";
    // 候选推荐(原唱) API (5sing)
    private final String CANDI_REC_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=2&s=%s&l=%s&p=%s";
    // 最新上传(翻唱) API (5sing)
    private final String LATEST_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=-1&s=%s&l=%s&p=%s";
    // 网站推荐(翻唱) API (5sing)
    private final String WEBSITE_REC_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=1&s=%s&l=%s&p=%s";
    // 候选推荐(翻唱) API (5sing)
    private final String CANDI_REC_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=2&s=%s&l=%s&p=%s";
    // 所有伴奏(伴奏) API (5sing)
    private final String ALL_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/bzsong/more_%s.shtml";

    /**
     * 获取推荐歌曲 + 新歌速递
     */
    public CommonResult<NetMusicInfo> getNewMusic(int src, String tag, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> res = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.newSongTag.get(tag);

        // 网易云(程序分页)
        // 推荐新歌
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSong = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_NEW_SONG_API, "{\"type\":\"recommend\",\"limit\":100,\"areaId\":0}", options)
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songArray = musicInfoJson.getJSONArray("result");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject jsonObject = songArray.getJSONObject(i);
                JSONObject songJson;
                if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
                else songJson = jsonObject;
                JSONObject albumJson = songJson.getJSONObject("album");

                String songId = songJson.getString("id");
                String songName = songJson.getString("name").trim();
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("duration") / 1000;
                String mvId = songJson.getString("mvid");
                int qualityType = AudioQuality.UNKNOWN;
                if (JsonUtil.notEmpty(songJson.getJSONObject("hrMusic"))) qualityType = AudioQuality.HR;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("sqMusic"))) qualityType = AudioQuality.SQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("hMusic"))) qualityType = AudioQuality.HQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("mMusic"))) qualityType = AudioQuality.MQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("lMusic"))) qualityType = AudioQuality.LQ;

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
            return new CommonResult<>(r, t);
        };
        // 新歌速递
        Callable<CommonResult<NetMusicInfo>> getFastNewSong = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String musicInfoBody = SdkCommon.ncRequest(Method.POST, FAST_NEW_SONG_API, String.format("{\"areaId\":\"%s\",\"total\":true}", s[0]), options)
                        .executeAsync()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONArray songArray = musicInfoJson.getJSONArray("data");
                t = songArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject jsonObject = songArray.getJSONObject(i);
                    JSONObject songJson;
                    if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
                    else songJson = jsonObject;
                    JSONObject albumJson = songJson.getJSONObject("album");

                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name").trim();
                    String artist = SdkUtil.parseArtist(songJson);
                    String artistId = SdkUtil.parseArtistId(songJson);
                    String albumName = albumJson.getString("name");
                    String albumId = albumJson.getString("id");
                    Double duration = songJson.getDouble("duration") / 1000;
                    String mvId = songJson.getString("mvid");
                    int qualityType = AudioQuality.UNKNOWN;
                    if (JsonUtil.notEmpty(songJson.getJSONObject("hrMusic"))) qualityType = AudioQuality.HR;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("sqMusic"))) qualityType = AudioQuality.SQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("hMusic"))) qualityType = AudioQuality.HQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("mMusic"))) qualityType = AudioQuality.MQ;
                    else if (JsonUtil.notEmpty(songJson.getJSONObject("lMusic"))) qualityType = AudioQuality.LQ;

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
        // 曲风歌曲(最新)
        Callable<CommonResult<NetMusicInfo>> getStyleNewSong = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String musicInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_NEW_SONG_API,
                                String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":1}", s[1], (page - 1) * limit, limit), options)
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
        // 每日推荐歌曲
        Callable<CommonResult<NetMusicInfo>> getEverydaySongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(EVERYDAY_SONG_KG_API);
            Map<String, Object> params = new TreeMap<>();
            params.put("platform", "android");
            params.put("userid", "0");
            String musicInfoBody = SdkCommon.kgRequest(params, null, options)
                    .header("x-router", "everydayrec.service.kugou.com")
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
            return new CommonResult<>(r, t);
        };
        // 华语新歌(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_KG_API, s[2], page, limit))
                        .executeAsync()
                        .body();
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
        };
        // 风格歌曲
        Callable<CommonResult<NetMusicInfo>> getStyleSongKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(STYLE_SONG_KG_API);
                Map<String, Object> params = new TreeMap<>();
                params.put("tagids", s[3]);
                String musicInfoBody = SdkCommon.kgRequest(params, "{}", options)
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

        // QQ(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                        .body(String.format("{\"comm\":{\"ct\":24},\"new_song\":{\"module\":\"newsong.NewSongServer\"," +
                                "\"method\":\"get_new_song_info\",\"param\":{\"type\":%s}}}", s[4]))
                        .executeAsync()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONArray songArray = musicInfoJson.getJSONObject("new_song").getJSONObject("data").getJSONArray("songlist");
                t = songArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
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
            }
            return new CommonResult<>(r, t);
        };

        // 酷我(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKw = () -> {
            return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(16), NetMusicSource.KW, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(NEW_SONG_KW_API, page, limit)).executeAsync();
//            if (resp.getStatus() == HttpStatus.HTTP_OK) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                t = Math.max(t, songArray.size());
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

        // 咪咕(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongMg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_MG_API, page - 1, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .setFollowRedirects(true)
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("result");
            t = data.getIntValue("totalCount");
            JSONArray songArray = data.getJSONArray("results");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("songData");

                String songId = songJson.getString("songId");
                String songName = songJson.getString("songName");
                String artist = SdkUtil.joinString(songJson.getJSONArray("singerName"));
                JSONArray singerIdArray = songJson.getJSONArray("singerId");
                String artistId = JsonUtil.isEmpty(singerIdArray) ? "" : singerIdArray.getString(0);
                int qualityType;
                if (songJson.getIntValue("has24Bitqq") == 1) qualityType = AudioQuality.HR;
                else if (songJson.getIntValue("hasSQqq") == 1) qualityType = AudioQuality.SQ;
                else if (songJson.getIntValue("hasHQqq") == 1) qualityType = AudioQuality.HQ;
                else qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 千千(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQi = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = SdkCommon.qiRequest(String.format(RECOMMEND_NEW_SONG_QI_API, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONArray("data").getJSONObject(3);
            t = data.getIntValue("module_nums");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String songName = songJson.getString("title");
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
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 音乐磁场
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongHf = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_MUSIC_HF_API, s[5], page))
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
                    Element span = song.select(".haya-post-info-username .username").first();

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
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongGg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_MUSIC_GG_API, s[6], page))
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
        // 最新上传(原唱)
        Callable<CommonResult<NetMusicInfo>> getLatestYcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(LATEST_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 网站推荐(原唱)
        Callable<CommonResult<NetMusicInfo>> getWebsiteRecYcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 候选推荐(原唱)
        Callable<CommonResult<NetMusicInfo>> getCandiRecYcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 最新上传(翻唱)
        Callable<CommonResult<NetMusicInfo>> getLatestFcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(LATEST_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 网站推荐(翻唱)
        Callable<CommonResult<NetMusicInfo>> getWebsiteRecFcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 候选推荐(翻唱)
        Callable<CommonResult<NetMusicInfo>> getCandiRecFcSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(musicInfoBody);
                Elements songs = doc.select(".lists dl dd.l_info");
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
        // 所有伴奏(伴奏)
        Callable<CommonResult<NetMusicInfo>> getAllBzSongFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(ALL_BZ_MUSIC_FS_API, page))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select("tr");
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
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSong));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFastNewSong));
            if (!dt) taskList.add(GlobalExecutors.requestExecutor.submit(getStyleNewSong));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getEverydaySongKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getStyleSongKg));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongQq));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongKw));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongMg));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongQi));
        }
        if (src == NetMusicSource.HF || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongHf));
        }
        if (src == NetMusicSource.GG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongGg));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getLatestYcSongFs));
            taskList.add(GlobalExecutors.requestExecutor.submit(getWebsiteRecYcSongFs));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCandiRecYcSongFs));
            taskList.add(GlobalExecutors.requestExecutor.submit(getLatestFcSongFs));
            taskList.add(GlobalExecutors.requestExecutor.submit(getWebsiteRecFcSongFs));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCandiRecFcSongFs));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getAllBzSongFs));
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
