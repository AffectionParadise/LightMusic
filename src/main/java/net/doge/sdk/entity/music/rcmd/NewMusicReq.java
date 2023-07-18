package net.doge.sdk.entity.music.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class NewMusicReq {
    // 推荐新歌 API
    private final String RECOMMEND_NEW_SONG_API = SdkCommon.prefix + "/personalized/newsong?limit=100";
    // 曲风歌曲(最新) API
    private final String STYLE_NEW_SONG_API = SdkCommon.prefix + "/style/song?tagId=%s&sort=1&cursor=%s&size=%s";
    // 新歌速递 API
    private final String FAST_NEW_SONG_API = SdkCommon.prefix + "/top/song?type=%s";
    // 推荐新歌(华语) API (酷狗)
    private final String RECOMMEND_NEW_SONG_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/newsong?version=9108&type=%s&page=%s&pagesize=%s";
    // 推荐新歌 API (QQ)
    private final String RECOMMEND_NEW_SONG_QQ_API = SdkCommon.prefixQQ33 + "/new/songs?type=%s";
    // 新歌榜 API (酷我)
    private final String NEW_SONG_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";
    // 推荐新歌 API (咪咕)
    private final String RECOMMEND_NEW_SONG_MG_API = SdkCommon.prefixMg + "/new/songs?pageNo=%s&pageSize=%s";
    // 推荐新歌 API (千千)
    private final String RECOMMEND_NEW_SONG_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 推荐新歌 API (音乐磁场)
    private final String RECOMMEND_NEW_MUSIC_HF_API = "https://www.hifini.com/%s-%s.htm?orderby=tid";
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
    public CommonResult<NetMusicInfo> getNewMusic(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.newSongTag.get(tag);

        // 网易云(程序分页)
        // 推荐新歌
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSong = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(RECOMMEND_NEW_SONG_API)
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("result");
            t = songsArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                JSONObject jsonObject = songsArray.getJSONObject(i);
                JSONObject songJson;
                if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
                else songJson = jsonObject;
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
            return new CommonResult<>(res, t);
        };
        // 新歌速递
        Callable<CommonResult<NetMusicInfo>> getFastNewSong = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                String musicInfoBody = HttpRequest.get(String.format(FAST_NEW_SONG_API, s[0]))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONArray songsArray = musicInfoJson.getJSONArray("data");
                t = songsArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                    JSONObject jsonObject = songsArray.getJSONObject(i);
                    JSONObject songJson;
                    if (jsonObject.containsKey("song")) songJson = jsonObject.getJSONObject("song");
                    else songJson = jsonObject;
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
            return new CommonResult<>(res, t);
        };
        // 曲风歌曲(最新)
        Callable<CommonResult<NetMusicInfo>> getStyleNewSong = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String musicInfoBody = HttpRequest.get(String.format(STYLE_NEW_SONG_API, s[1], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                JSONArray songsArray = data.getJSONArray("songs");
                t = data.getJSONObject("page").getIntValue("total");
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name").trim();
                    String artist = SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String artistId = songJson.getJSONArray("ar").getJSONObject(0).getString("id");
                    String albumName = songJson.getJSONObject("al").getString("name");
                    String albumId = songJson.getJSONObject("al").getString("id");
                    Double duration = songJson.getDouble("dt") / 1000;
                    String mvId = songJson.getString("mv");

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

            return new CommonResult<>(res, t);
        };

        // 酷狗
        // 华语新歌(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_KG_API, s[2], page, limit))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray songsArray = data.getJSONArray("info");
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String hash = songJson.getString("hash");
                    String songId = songJson.getString("album_audio_id");
                    String name = songJson.getString("songname");
                    String artists = SdkUtil.parseArtists(songJson, NetMusicSource.KG);
                    JSONArray artistArray = songJson.getJSONArray("authors");
                    String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("author_id") : "";
                    String albumId = songJson.getString("album_id");
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getString("mvhash");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KG);
                    musicInfo.setHash(hash);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artists);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_QQ_API, s[3]))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONArray songsArray = musicInfoJson.getJSONObject("data").getJSONArray("list");
                t = songsArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
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
            return new CommonResult<>(res, t);
        };

        // 酷我(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKw = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(NEW_SONG_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getIntValue("num");
                JSONArray songsArray = data.getJSONArray("musicList");
                t = Math.max(t, songsArray.size());
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String id = songJson.getString("rid");
                    String name = songJson.getString("name");
                    String artist = songJson.getString("artist").replace("&", "、");
                    String artistId = songJson.getString("artistid");
                    String albumName = songJson.getString("album");
                    String albumId = songJson.getString("albumid");
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getIntValue("hasmv") == 0 ? "" : id;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KW);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongMg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_MG_API, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray songsArray = data.getJSONArray("list");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("cid");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtists(songJson, NetMusicSource.MG);
                String artistId = songJson.getJSONArray("artists").getJSONObject(0).getString("id");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQi = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(RECOMMEND_NEW_SONG_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONArray("data").getJSONObject(2);
            t = data.getIntValue("module_nums");
            JSONArray songsArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String songName = songJson.getString("title");
                String artist = SdkUtil.parseArtists(songJson, NetMusicSource.QI);
                JSONArray artistArray = songJson.getJSONArray("artist");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 音乐磁场
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongHf = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_MUSIC_HF_API, s[4], page))
                        .cookie(SdkCommon.HF_COOKIE)
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咕咕咕音乐
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongGg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_MUSIC_GG_API, s[5], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 5sing
        // 最新上传(原唱)
        Callable<CommonResult<NetMusicInfo>> getLatestYcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(LATEST_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 网站推荐(原唱)
        Callable<CommonResult<NetMusicInfo>> getWebsiteRecYcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 候选推荐(原唱)
        Callable<CommonResult<NetMusicInfo>> getCandiRecYcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新上传(翻唱)
        Callable<CommonResult<NetMusicInfo>> getLatestFcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(LATEST_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 网站推荐(翻唱)
        Callable<CommonResult<NetMusicInfo>> getWebsiteRecFcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 候选推荐(翻唱)
        Callable<CommonResult<NetMusicInfo>> getCandiRecFcSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                        .execute()
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

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 所有伴奏(伴奏)
        Callable<CommonResult<NetMusicInfo>> getAllBzSongFs = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(ALL_BZ_MUSIC_FS_API, page))
                    .execute()
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

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSong));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFastNewSong));
            if (!dt) taskList.add(GlobalExecutors.requestExecutor.submit(getStyleNewSong));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongKg));
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
        musicInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(musicInfos, total.get());
    }
}
