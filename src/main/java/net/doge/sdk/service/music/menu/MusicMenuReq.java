package net.doge.sdk.service.music.menu;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.http.HttpRequest;
import net.doge.util.http.constant.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MusicMenuReq {
    private static MusicMenuReq instance;

    private MusicMenuReq() {
    }

    public static MusicMenuReq getInstance() {
        if (instance == null) instance = new MusicMenuReq();
        return instance;
    }

    // 相似歌曲 API
    private final String SIMILAR_SONG_API = "https://music.163.com/weapi/v1/discovery/simiSong";
    // 相似歌曲 API (猫耳)
    private final String SIMILAR_SONG_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲相关歌单 API
    private final String RELATED_PLAYLIST_API = "https://music.163.com/weapi/discovery/simiPlaylist";

    // 歌曲推荐电台 API (猫耳)
    private final String SONG_REC_RADIO_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifiti.com/thread-%s.htm";
    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        List<NetMusicInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_SONG_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songArray = musicInfoJson.getJSONArray("songs");
            t = songArray.size();
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
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

                res.add(musicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
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
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".relate_post a");
            t = songs.size();
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);

                String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", song.attr("href"));
                String songName = song.text();

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.HF);
                musicInfo.setId(songId);
                musicInfo.setName(songName);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select("ul.text-middle.break-all li a");
            t = songs.size();
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);

                String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", song.attr("href"));
                String songName = song.text();

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.GG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_ME_API, id))
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songArray = musicInfoJson.getJSONObject("info").getJSONArray("sounds");
            t = songArray.size();
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String songName = songJson.getString("soundstr");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(songName);

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String id = musicInfo.getId();

        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, RELATED_PLAYLIST_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);
                JSONObject creatorJson = playlistJson.getJSONObject("creator");

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = creatorJson.getString("nickname");
                String creatorId = creatorJson.getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
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
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
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
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取推荐电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getRecRadios(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String id = musicInfo.getId();

        List<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;

        // 猫耳
        if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(SONG_REC_RADIO_ME_API, id))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            JSONArray radioArray = data.getJSONArray("dramas");
            t = radioArray.size();
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String coverImgThumbUrl = radioJson.getString("front_cover");
                Long playCount = radioJson.getLong("view_count");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setPlayCount(playCount);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
