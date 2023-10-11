package net.doge.sdk.entity.music.menu;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetPlaylistInfo;
import net.doge.model.entity.NetRadioInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MusicMenuReq {
    // 相似歌曲 API
    private final String SIMILAR_SONG_API = "https://music.163.com/weapi/v1/discovery/simiSong";
    // 相似歌曲 API (猫耳)
    private final String SIMILAR_SONG_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲相关歌单 API
    private final String RELATED_PLAYLIST_API = "https://music.163.com/weapi/discovery/simiPlaylist";

    // 歌曲推荐电台 API (猫耳)
    private final String SONG_REC_RADIO_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifini.com/thread-%s.htm";
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
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_SONG_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                    .executeAsync()
                    .body();
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

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            // 先根据 mid 获取 id
            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

            musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                            "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"simsongs\":{\"module\":\"rcmusic.similarSongRadioServer\"," +
                            "\"method\":\"get_simsongs\",\"param\":{\"songid\":%s}}}", id))
                    .executeAsync()
                    .body();
            musicInfoJson = JSONObject.parseObject(musicInfoBody).getJSONObject("simsongs").getJSONObject("data");
            JSONArray songArray = musicInfoJson.getJSONArray("songInfoList");
            t = songArray.size();
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");

                String songId = songJson.getString("mid");
                String songName = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("title");
                String albumId = albumJson.getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

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

                res.add(musicInfo);
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsync()
                    .body();
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
                    .executeAsync()
                    .body();
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
                    .executeAsync()
                    .body();
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
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, RELATED_PLAYLIST_API, String.format("{\"songid\":\"%s\",\"offset\":0,\"limit\":50}", id), options)
                    .executeAsync()
                    .body();
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
                    .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                    .executeAsync()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

            String playlistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                            "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"gedan\":{\"module\":\"music.mb_gedan_recommend_svr\"," +
                            "\"method\":\"get_related_gedan\",\"param\":{\"sin\":0,\"last_id\":0,\"song_type\":1,\"song_id\":%s}}}", id))
                    .executeAsync()
                    .body();
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
                    .executeAsync()
                    .body();
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
