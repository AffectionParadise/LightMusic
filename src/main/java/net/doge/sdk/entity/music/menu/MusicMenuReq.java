package net.doge.sdk.entity.music.menu;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetPlaylistInfo;
import net.doge.model.entity.NetRadioInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class MusicMenuReq {
    // 相似歌曲 API
    private final String SIMILAR_SONG_API = SdkCommon.prefix + "/simi/song?id=%s";
    // 相似歌曲 API (QQ)
    private final String SIMILAR_SONG_QQ_API = SdkCommon.prefixQQ33 + "/song/similar?id=%s";
    // 相似歌曲 API (猫耳)
    private final String SIMILAR_SONG_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲相关歌单 API
    private final String RELATED_PLAYLIST_API = SdkCommon.prefix + "/simi/playlist?id=%s";
    // 相关歌单 API (QQ)
    private final String RELATED_PLAYLIST_QQ_API = SdkCommon.prefixQQ33 + "/song/playlist?id=%s";

    // 歌曲推荐电台 API (猫耳)
    private final String SONG_REC_RADIO_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    // 歌曲信息 API (QQ)
    private final String SINGLE_SONG_DETAIL_QQ_API = SdkCommon.prefixQQ33 + "/song?songmid=%s";
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

        LinkedList<NetMusicInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("songs");
            t = songsArray.size();
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String songName = songJson.getString("name").trim();
                String artist = SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String artistId = songJson.getJSONArray("artists").getJSONObject(0).getString("id");
                String albumName = songJson.getJSONObject("album").getString("name");
                String albumId = songJson.getJSONObject("album").getString("id");
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
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("data").getJSONObject("track_info").getString("id");

            musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_QQ_API, id))
                    .execute()
                    .body();
            musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("data");
            t = songsArray.size();
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtists(songJson, NetMusicSource.QQ);
                JSONArray singerArray = songJson.getJSONArray("singer");
                String artistId = singerArray.isEmpty() ? "" : singerArray.getJSONObject(0).getString("mid");
                String albumName = songJson.getJSONObject("album").getString("name");
                String albumId = songJson.getJSONObject("album").getString("mid");
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
                    .cookie(SdkCommon.HF_COOKIE)
                    .execute()
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
                    .execute()
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
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONObject("info").getJSONArray("sounds");
            t = songsArray.size();
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

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
    public CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo netMusicInfo) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(RELATED_PLAYLIST_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
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
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("data").getJSONObject("track_info").getString("id");

            String playlistInfoBody = HttpRequest.get(String.format(RELATED_PLAYLIST_QQ_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("data");
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

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;

        // 猫耳
        if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(SONG_REC_RADIO_ME_API, id))
                    .execute()
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
