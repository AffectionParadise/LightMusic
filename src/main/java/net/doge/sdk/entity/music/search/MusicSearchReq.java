package net.doge.sdk.entity.music.search;

import cn.hutool.http.*;
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
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class MusicSearchReq {
    // 关键词搜索歌曲/声音/歌词 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    private final String SEARCH_VOICE_API = "https://music.163.com/api/search/voice/get";
    // 关键词搜索歌曲 API (酷狗)
    private final String SEARCH_MUSIC_KG_API = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%s&page=%s&pagesize=%s&showtype=1";
    // 关键词搜索歌曲 API (搜歌词) (酷狗)
    private final String SEARCH_MUSIC_BY_LYRIC_KG_API = "http://mobileservice.kugou.com/api/v3/lyric/search?keyword=%s&page=%s&pagesize=%s";
    // 关键词搜索歌曲 API (酷我)
    private final String SEARCH_MUSIC_KW_API = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索歌曲 API (咪咕)
    private final String SEARCH_MUSIC_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=2&keyword=%s&pgc=%s&rows=%s";
    // 关键词搜索歌曲 API (搜歌词) (咪咕)
    private final String SEARCH_MUSIC_BY_LYRIC_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=7&keyword=%s&pgc=%s&rows=%s";
    // 关键词搜索歌曲 API (千千)
    private final String SEARCH_MUSIC_QI_API = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=1&word=%s";
    // 关键词搜索歌曲 API (音乐磁场)
    private final String SEARCH_MUSIC_HF_API = "https://www.hifini.com/search-%s-1-%s.htm";
    // 关键词搜索歌曲 API (咕咕咕音乐)
    private final String SEARCH_MUSIC_GG_API = "http://www.gggmusic.com/search-%s-1-%s.htm";
    // 关键词搜索歌曲 API (5sing)
    private final String SEARCH_MUSIC_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=0&type=0";
    // 关键词搜索节目 API (喜马拉雅)
    private final String SEARCH_MUSIC_XM_API
            = "https://www.ximalaya.com/revision/search/main?kw=%s&page=%s&spellchecker=true&condition=relation&rows=%s&device=iPhone&core=track&fq=&paidFilter=false";
    // 关键词搜索节目 API (猫耳)
    private final String SEARCH_PROGRAM_ME_API = "https://www.missevan.com/sound/getsearch?cid=%s&s=%s&p=%s&type=3&page_size=%s";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(int src, int type, String subType, String keyword, int limit, int page) {
        AtomicReference<Integer> total = new AtomicReference<>(0);
        List<NetMusicInfo> res = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncodeAll(keyword);

        boolean dt = "默认".equals(subType);
        String[] s = Tags.programSearchTag.get(subType);

        // 网易云
        // 搜歌曲
        Callable<CommonResult<NetMusicInfo>> searchMusic = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eApi("/api/cloudsearch/pc");
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject result = musicInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("songCount");
                JSONArray songArray = result.getJSONArray("songs");
                if (JsonUtil.notEmpty(songArray)) {
                    for (int i = 0, len = songArray.size(); i < len; i++) {
                        JSONObject songJson = songArray.getJSONObject(i);

                        String songId = songJson.getString("id");
                        String songName = songJson.getString("name").trim();
                        String artist = SdkUtil.parseArtist(songJson);
                        String artistId = SdkUtil.parseArtistId(songJson);
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

                        r.add(musicInfo);
                    }
                }
            }
            return new CommonResult<>(r, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyric = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eApi("/api/cloudsearch/pc");
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1006,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject result = musicInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("songCount");
                JSONArray songs = result.getJSONArray("songs");
                if (JsonUtil.notEmpty(songs)) {
                    for (int i = 0, len = songs.size(); i < len; i++) {
                        JSONObject songJson = songs.getJSONObject(i);

                        String songId = songJson.getString("id");
                        String songName = songJson.getString("name").trim();
                        String artist = SdkUtil.parseArtist(songJson);
                        String artistId = SdkUtil.parseArtistId(songJson);
                        String albumName = songJson.getJSONObject("al").getString("name");
                        String albumId = songJson.getJSONObject("al").getString("id");
                        Double duration = songJson.getDouble("dt") / 1000;
                        String mvId = songJson.getString("mv");
//                        String lrcMatch = songJson.getJSONObject("lyrics").getString("txt").replace("\n", " / ");
                        JSONArray lyrics = songJson.getJSONArray("lyrics");
                        String lrcMatch = null;
                        if (JsonUtil.notEmpty(lyrics)) {
                            StringJoiner sj = new StringJoiner(" / ");
                            for (int j = 0, size = lyrics.size(); j < size; j++) {
                                sj.add(lyrics.getString(j));
                            }
                            lrcMatch = StringUtil.removeHTMLLabel(sj.toString());
                        }

                        NetMusicInfo musicInfo = new NetMusicInfo();
                        musicInfo.setId(songId);
                        musicInfo.setName(songName);
                        musicInfo.setArtist(artist);
                        musicInfo.setArtistId(artistId);
                        musicInfo.setAlbumName(albumName);
                        musicInfo.setAlbumId(albumId);
                        musicInfo.setDuration(duration);
                        musicInfo.setMvId(mvId);
                        musicInfo.setLrcMatch(lrcMatch);

                        r.add(musicInfo);
                    }
                }
            }
            return new CommonResult<>(r, t);
        };
        // 搜声音
        Callable<CommonResult<NetMusicInfo>> searchVoice = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, SEARCH_VOICE_API,
                            String.format("{\"keyword\":\"%s\",\"scene\":\"normal\",\"offset\":%s,\"limit\":%s}", keyword, (page - 1) * lim, lim), options)
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            int to = data.getIntValue("totalCount");
            t = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
            JSONArray songArray = data.getJSONArray("resources");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject programJson = songArray.getJSONObject(i).getJSONObject("baseInfo");
                    JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                    JSONObject djJson = programJson.getJSONObject("dj");
                    JSONObject radioJson = programJson.getJSONObject("radio");

                    String programId = programJson.getString("id");
                    String songId = mainSongJson.getString("id");
                    String name = mainSongJson.getString("name");
                    String artist = djJson.getString("nickname");
                    String artistId = djJson.getString("userId");
                    String albumName = radioJson.getString("name");
                    String albumId = radioJson.getString("id");
                    Double duration = mainSongJson.getDouble("duration") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setProgramId(programId);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        // 搜单曲
        Callable<CommonResult<NetMusicInfo>> searchMusicKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String songName = songJson.getString("songname");
                String artist = songJson.getString("singername");
                String albumName = songJson.getString("album_name");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");
                String lrcMatch = songJson.getString("lyric");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setLrcMatch(lrcMatch);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyricKg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] split = songJson.getString("filename").split(" - ");
                String songName = split[split.length == 1 ? 0 : 1];
                String artist = songJson.getString("singername");
//                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");
                String lrcMatch = songJson.getString("lyric");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setLrcMatch(lrcMatch);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        // 搜歌曲
        Callable<CommonResult<NetMusicInfo>> searchMusicQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 0))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray songArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
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

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyricQq = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 7))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray songArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getJSONObject("album").getString("name");
                String albumId = songJson.getJSONObject("album").getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("id");
                String lrcMatch = StringUtil.removeHTMLLabel(songJson.getString("content")).replace("\\n", " / ");

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
                musicInfo.setLrcMatch(lrcMatch);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        Callable<CommonResult<NetMusicInfo>> searchMusicKw = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_MUSIC_KW_API, encodedKeyword, page, limit)).execute();
            // 有时候请求会崩，先判断是否请求成功
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                if (JsonUtil.notEmpty(data)) {
                    t = data.getIntValue("total");
                    JSONArray songArray = data.getJSONArray("list");
                    if (JsonUtil.notEmpty(songArray)) {
                        for (int i = 0, len = songArray.size(); i < len; i++) {
                            JSONObject songJson = songArray.getJSONObject(i);

                            String songId = songJson.getString("rid");
                            String songName = StringUtil.removeHTMLLabel(songJson.getString("name"));
                            String artist = StringUtil.removeHTMLLabel(songJson.getString("artist")).replace("&", "、");
                            String artistId = songJson.getString("artistid");
                            String albumName = StringUtil.removeHTMLLabel(songJson.getString("album"));
                            String albumId = songJson.getString("albumid");
                            Double duration = songJson.getDouble("duration");
                            String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;

                            NetMusicInfo musicInfo = new NetMusicInfo();
                            musicInfo.setSource(NetMusicSource.KW);
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
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咪咕
        // 搜歌曲
        Callable<CommonResult<NetMusicInfo>> searchMusicMg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_MG_API, encodedKeyword, page, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            t = musicInfoJson.getIntValue("pgt");
            JSONArray songArray = musicInfoJson.getJSONArray("musics");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("copyrightId");
                    String songName = songJson.getString("songName");
                    String artist = songJson.getString("singerName").replace(", ", "、");
                    String artistId = songJson.getString("singerId").split(", ")[0];
                    String albumName = songJson.getString("albumName");
                    String albumId = songJson.getString("albumId");
                    String mvId = songJson.getString("mvCopyrightId");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.MG);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setMvId(mvId);

                    r.add(musicInfo);
                }
            }

            return new CommonResult<>(r, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyricMg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_MG_API, encodedKeyword, page, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            t = musicInfoJson.getIntValue("pgt");
            JSONArray songArray = musicInfoJson.getJSONArray("songs");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("copyrightId");
                    String songName = songJson.getString("songName");
                    String artist = songJson.getString("singerName").replace(", ", "、");
                    String artistId = songJson.getString("singerId").split(", ")[0];
                    String albumName = songJson.getString("albumName");
                    String albumId = songJson.getString("albumId");
                    String mvId = songJson.getString("mvCopyrightId");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.MG);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setMvId(mvId);

                    r.add(musicInfo);
                }
            }

            return new CommonResult<>(r, t);
        };

        // 千千
        Callable<CommonResult<NetMusicInfo>> searchMusicQi = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(SEARCH_MUSIC_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword)))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                t = data.getIntValue("total");
                JSONArray songArray = data.getJSONArray("typeTrack");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("TSID");
                    String songName = songJson.getString("title");
                    String artist = SdkUtil.parseArtist(songJson);
                    String artistId = SdkUtil.parseArtistId(songJson);
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

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 音乐磁场
        Callable<CommonResult<NetMusicInfo>> searchMusicHf = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_HF_API, encodedKeyword.replace("%", "_"), page))
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
            for (int i = 0, len = songs.size(); i < len; i++) {
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
            return new CommonResult<>(r, t);
        };

        // 咕咕咕音乐
        Callable<CommonResult<NetMusicInfo>> searchMusicGg = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_GG_API, encodedKeyword.replace("%", "_"), page))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
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
            for (int i = 0, len = songs.size(); i < len; i++) {
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
            return new CommonResult<>(r, t);
        };

        // 5sing
        Callable<CommonResult<NetMusicInfo>> searchMusicFs = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_FS_API, encodedKeyword, page))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(musicInfoBody);
            t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
            JSONArray songArray = data.getJSONArray("list");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("songId");
                    String songType = songJson.getString("typeEname");
                    String songName = StringUtil.removeHTMLLabel(songJson.getString("songName"));
                    String artist = StringUtil.removeHTMLLabel(songJson.getString("singer"));
                    String artistId = songJson.getString("singerId");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(String.format("%s_%s", songType, songId));
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetMusicInfo>> searchMusicXm = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_XM_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data").getJSONObject("track");
            t = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("docs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("title");
                String artist = songJson.getString("nickname");
                String artistId = songJson.getString("uid");
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumId");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        Callable<CommonResult<NetMusicInfo>> searchProgramMe = () -> {
            List<NetMusicInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                String musicInfoBody = HttpRequest.get(String.format(SEARCH_PROGRAM_ME_API, s[0].trim(), encodedKeyword, page, limit))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("info");
                t = data.getJSONObject("pagination").getIntValue("count");
                JSONArray songArray = data.getJSONArray("Datas");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String name = StringUtil.removeHTMLLabel(songJson.getString("soundstr"));
                    String artist = songJson.getString("username");
                    String artistId = songJson.getString("user_id");
                    Double duration = songJson.getDouble("duration") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setDuration(duration);

                    r.add(musicInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        switch (type) {
            // 歌词
            case 1:
                if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyric));
                if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyricKg));
                if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyricQq));
                if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyricMg));
                break;
            // 节目
            case 2:
                if (dt) {
                    if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
                        taskList.add(GlobalExecutors.requestExecutor.submit(searchVoice));
                    if (src == NetMusicSource.XM || src == NetMusicSource.ALL)
                        taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicXm));
                }
                if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchProgramMe));
                break;
            // 常规
            default:
                if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusic));
                if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicKg));
                if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicQq));
                if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicKw));
                if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicMg));
                if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicQi));
                if (src == NetMusicSource.HF || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicHf));
                if (src == NetMusicSource.GG || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicGg));
                if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicFs));
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
