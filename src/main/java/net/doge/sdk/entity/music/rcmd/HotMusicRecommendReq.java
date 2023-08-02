package net.doge.sdk.entity.music.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.entity.playlist.info.PlaylistInfoReq;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class HotMusicRecommendReq {
    // 曲风歌曲(最热) API
    private final String STYLE_HOT_SONG_API = "https://music.163.com/api/style-tag/home/song";
    // 飙升榜 API (酷狗)
    private final String UP_MUSIC_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=6666&page=%s&pagesize=%s";
    // TOP500 API (酷狗)
    private final String TOP500_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=8888&page=%s&pagesize=%s";
    // 飙升榜 API (酷我)
    private final String UP_MUSIC_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=93&pn=%s&rn=%s&httpsStatus=1";
    // 热歌榜 API (酷我)
    private final String HOT_MUSIC_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";
    // 尖叫热歌榜 API (咪咕)
    private final String HOT_MUSIC_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=27186466";
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

    /**
     * 获取飙升歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusicRecommend(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> res = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.hotSongTag.get(tag);

        // 网易云(榜单就是歌单，固定榜单 id 直接请求歌单音乐接口，接口分页)
        PlaylistInfoReq playlistInfoReq = new PlaylistInfoReq();
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusic = () -> playlistInfoReq.getMusicInfoInPlaylist(String.valueOf(19723756), NetMusicSource.NET_CLOUD, limit, page);
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusic = () -> playlistInfoReq.getMusicInfoInPlaylist(String.valueOf(3778678), NetMusicSource.NET_CLOUD, limit, page);
        // 曲风歌曲(最热)
        Callable<CommonResult<NetMusicInfo>> getStyleHotSong = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
                String musicInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_HOT_SONG_API,
                                String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", s[0], (page - 1) * limit, limit), options)
                        .execute()
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

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);
                    r.add(musicInfo);
                }
            }

            return new CommonResult<>(r, t);
        };

        // 酷狗
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(UP_MUSIC_KG_API, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String name = songJson.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
//                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                JSONArray mvdata = songJson.getJSONArray("mvdata");
                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };
        // TOP500
        Callable<CommonResult<NetMusicInfo>> getTop500Kg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(TOP500_KG_API, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String name = songJson.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
//                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                JSONArray mvdata = songJson.getJSONArray("mvdata");
                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        // 流行指数榜
        Callable<CommonResult<NetMusicInfo>> getPopularMusicQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                            "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", 4, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
            t = data.getJSONObject("data").getIntValue("totalNum");
            JSONArray songArray = data.getJSONArray("songInfoList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("title");
                String albumId = albumJson.getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

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
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
            t = data.getJSONObject("data").getIntValue("totalNum");
            JSONArray songArray = data.getJSONArray("songInfoList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("title");
                String albumId = albumJson.getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

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

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKw = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(UP_MUSIC_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getIntValue("num");
                JSONArray songArray = data.getJSONArray("musicList");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

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

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicKw = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(HOT_MUSIC_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getIntValue("num");
                JSONArray songArray = data.getJSONArray("musicList");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

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

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咪咕
        // 尖叫热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicMg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(HOT_MUSIC_MG_API)
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
            t = data.getIntValue("contentsCount");
            JSONArray songArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");

                String songId = songJson.getString("copyrightId");
                // 过滤掉不是歌曲的 objectInfo
                if (StringUtil.isEmpty(songId)) continue;
                String name = songJson.getString("songName");
                String artist = songJson.getString("singer");
                String artistId = songJson.getString("singerId");
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumId");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 音乐磁场
        Callable<CommonResult<NetMusicInfo>> getHotMusicHf = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_HF_API, s[1], page))
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

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咕咕咕音乐
        Callable<CommonResult<NetMusicInfo>> getHotMusicGg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_GG_API, s[2], page))
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
                    .execute()
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
                    .execute()
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
                    .execute()
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
                    .execute()
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
                    .execute()
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
                    .execute()
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

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusic));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusic));
            }
            if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
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
        } else {
            if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
                taskList.add(GlobalExecutors.requestExecutor.submit(getStyleHotSong));
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
