package net.doge.sdk.entity.music.info;

import cn.hutool.http.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.*;
import net.doge.util.system.FileUtil;
import net.doge.util.ui.ImageUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MusicInfoReq {
    // 歌曲信息 API (单首)
    private final String SINGLE_SONG_DETAIL_API = "https://music.163.com/api/v3/song/detail";
    // 节目信息 API
    private final String SINGLE_PROGRAM_DETAIL_API = "https://music.163.com/api/dj/program/detail";
    // 歌曲信息 API (酷狗)
    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";
    // 歌曲信息 API (酷我)
    private final String SINGLE_SONG_DETAIL_KW_API = "http://www.kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";
    // 歌曲信息 API (咪咕) (下面那个接口能获取无版权音乐的信息)
//    private final String SINGLE_SONG_DETAIL_MG_API = "https://music.migu.cn/v3/api/music/audioPlayer/songs?copyrightId=%s";
    private final String SINGLE_SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";
    // 歌曲信息 API (喜马拉雅)
    private final String SINGLE_SONG_DETAIL_XM_API = "https://www.ximalaya.com/revision/track/simple?trackId=%s";
    // 歌曲信息 API (千千)
    private final String SINGLE_SONG_DETAIL_QI_API = "https://music.91q.com/v1/song/info?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifini.com/thread-%s.htm";
    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";
    // 歌曲信息 API (5sing)
    private final String SINGLE_SONG_DETAIL_FS_API = "http://service.5sing.kugou.com/song/find?songinfo=%s";
    // 歌曲信息 API (猫耳)
    private final String SINGLE_SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";
    // 歌曲专辑信息 API (猫耳)
    private final String SONG_ALBUM_DETAIL_ME_API = "https://www.missevan.com/dramaapi/getdramabysound?sound_id=%s";
    // 歌曲信息 API (哔哩哔哩)
    private final String SINGLE_SONG_DETAIL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/info?sid=%s";

    // 歌词 API
    private final String LYRIC_API = "https://interface3.music.163.com/eapi/song/lyric/v1";
    // 歌词 API (酷狗)
//    private final String LYRIC_KG_API = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=%s&hash=%s&timelength=%s";
//    private final String LYRIC_KG_API_2 = "http://lyrics.kugou.com/download?ver=1&client=pc&id=%s&accesskey=%s&fmt=krc&charset=utf8";
    // 歌词 API (QQ)
    private final String LYRIC_QQ_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0";
    // 歌词 API (酷我)
    private final String LYRIC_KW_API = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=%s&httpsStatus=1";
    //    private final String LYRIC_KW_API = "http://newlyric.kuwo.cn/newlyric.lrc?";
    // 歌曲 URL 获取 API (千千)
    private final String GET_SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";
    // 歌词 API (5sing)
    private final String LYRIC_FS_API = "http://5sing.kugou.com/fm/m/json/lrc?songType=%s&songId=%s";
    // 弹幕 API (猫耳)
    private final String DM_ME_API = "https://www.missevan.com/sound/getdm?soundid=%s";
    // 歌词 API (哔哩哔哩)
    private final String LYRIC_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/lyric?sid=%s";

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 补充 NetMusicInfo 歌曲时长
     */
    public void fillDuration(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String songId = musicInfo.getId();

        // 咪咕
        if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
            if (!musicInfo.hasDuration()) musicInfo.setDuration(TimeUtil.toSeconds(data.getString("length")));
        }
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的
        if (musicInfo.isIntegrated()) return;

        String songId = musicInfo.getId();
        int source = musicInfo.getSource();
        boolean isProgram = musicInfo.isProgram();

        // 网易云
        if (source == NetMusicSource.NC) {
            if (isProgram) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_PROGRAM_DETAIL_API, String.format("{\"id\":\"%s\"}", musicInfo.getProgramId()), options)
                        .executeAsync()
                        .body();
                JSONObject songJson = JSONObject.parseObject(songBody).getJSONObject("program");
                JSONObject dj = songJson.getJSONObject("dj");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("duration") / 1000);
                if (!musicInfo.hasArtist()) musicInfo.setArtist(dj.getString("nickname"));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(dj.getString("userId"));
                if (!musicInfo.hasAlbumName())
                    musicInfo.setAlbumName(songJson.getJSONObject("radio").getString("name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(songJson.getJSONObject("radio").getString("id"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("coverUrl"));
                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            } else {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_SONG_DETAIL_API, String.format("{\"c\":\"[{'id':'%s'}]\"}", songId), options)
                        .executeAsync()
                        .body();
                JSONArray array = JSONObject.parseObject(songBody).getJSONArray("songs");
                if (JsonUtil.isEmpty(array)) return;
                JSONObject songJson = array.getJSONObject(0);
                JSONObject albumJson = songJson.getJSONObject("al");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("dt") / 1000);
                if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(songJson));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(songJson));
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumJson.getString("name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumJson.getString("id"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(albumJson.getString("picUrl"));
                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            // 酷狗接口请求需要带上 cookie ！
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, songId))
                    .cookie(SdkCommon.COOKIE)
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDoubleValue("timelength") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album_name"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("album_id"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("img"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyrics"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String songBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", songId))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("songinfo").getJSONObject("data");
            JSONObject trackInfo = data.getJSONObject("track_info");
            JSONObject album = trackInfo.getJSONObject("album");

            if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("interval"));
            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(trackInfo));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(trackInfo));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(album.getString("name"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(album.getString("mid"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    // QQ 的歌曲专辑图片需要额外请求接口获得！
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("mid")));
                    // 有的歌曲没有专辑，先找备份专辑图片，如果还没有就将歌手的图片作为封面
                    if (albumImage == null)
                        albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("pmid")));
                    if (albumImage == null)
                        albumImage = SdkUtil.getImageFromUrl(String.format(ARTIST_IMG_QQ_API, SdkUtil.parseArtistId(trackInfo)));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(SINGLE_SONG_DETAIL_KW_API, songId)).executeAsync();
            if (resp.getStatus() != HttpStatus.HTTP_OK) return;
            String songBody = resp.body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");

            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("artist").replace("&", "、"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("artistid"));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumid"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("pic"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumId"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(TimeUtil.toSeconds(data.getString("length")));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    JSONArray imgArray = data.getJSONArray("albumImgs");
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(JsonUtil.isEmpty(imgArray) ? "" : imgArray.getJSONObject(0).getString("img"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String songBody = SdkCommon.qiRequest(String.format(SINGLE_SONG_DETAIL_QI_API, songId, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("albumTitle"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumAssetCode"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("pic"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, songId))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(songBody);
            String dataStr = RegexUtil.getGroup1("music: \\[.*?(\\{.*?\\}).*?\\]", doc.html());
            // json 字段带引号
            if (StringUtil.notEmpty(dataStr)) dataStr = dataStr.replaceAll(" (\\w+):", "'$1':");
            JSONObject data = JSONObject.parseObject(dataStr);

            Elements a = doc.select(".m-3.text-center h5 a");

            if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
            if (!musicInfo.hasArtistId())
                musicInfo.setArtistId(RegexUtil.getGroup1("user-(\\d+)\\.htm", a.attr("href")));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    String picUrl = data.getString("pic");
                    if (picUrl.contains("music.126.net"))
                        picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                    else if (picUrl.contains("y.gtimg.cn"))
                        picUrl = picUrl.replaceFirst("300x300", "500x500");
                    if (!picUrl.startsWith("http")) picUrl = "https://www.hifini.com/" + picUrl;
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, songId))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(songBody);
            String dataStr = RegexUtil.getGroup1("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html());
            if (StringUtil.notEmpty(dataStr)) {
                dataStr = dataStr.replaceFirst("base64_decode\\(\"(.*?)\"\\)", "\"\"");
                // json 字段带引号
                dataStr = dataStr.replaceAll(" (\\w+):", "'$1':");
            }
            JSONObject data = JSONObject.parseObject(dataStr);

            Elements a = doc.select(".m-3.text-center h5 a");

            if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
            if (!musicInfo.hasArtistId())
                musicInfo.setArtistId(RegexUtil.getGroup1("user-(\\d+)\\.htm", a.attr("href")));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    String picUrl = data.getString("cover");
                    if (StringUtil.isEmpty(picUrl)) picUrl = data.getString("pic");
                    if (StringUtil.notEmpty(picUrl)) {
                        if (picUrl.contains("music.126.net"))
                            picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                        else if (picUrl.contains("y.gtimg.cn"))
                            picUrl = picUrl.replaceFirst("300x300", "500x500");
                        if (!picUrl.startsWith("http")) picUrl = "http://www.gggmusic.com/" + picUrl;
                    }
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_FS_API, StringUtil.urlEncodeAll(songId.replace("_", "$"))))
                    .executeAsync()
                    .body();
            JSONObject data = JSONArray.parseArray(songBody).getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("nickname"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtist(data.getString("userid"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("avatar"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_XM_API, songId))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            JSONObject trackInfo = data.getJSONObject("trackInfo");
            JSONObject albumInfo = data.getJSONObject("albumInfo");

            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(trackInfo.getString("anchorUid"));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumInfo.getString("title"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumInfo.getString("albumId"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl("https:" + trackInfo.getString("coverPath"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            musicInfo.setLrc("");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            // 歌曲信息
            Runnable fillMusicInfo = () -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, songId))
                        .executeAsync()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
                // 时长是毫秒，转为秒
                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration") / 1000);
                if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("username"));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("user_id"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("front_cover"));
                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            };
            // 专辑信息
            Runnable fillAlbumInfo = () -> {
                String albumBody = HttpRequest.get(String.format(SONG_ALBUM_DETAIL_ME_API, songId))
                        .executeAsync()
                        .body();
                String infoStr = JSONObject.parseObject(albumBody).getString("info");
                // 可能是字符串也可能是 json 对象，先判断
                if (!JSON.isValidObject(infoStr)) return;
                JSONObject info = JSONObject.parseObject(infoStr);
                JSONObject albumData = info.getJSONObject("drama");
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumData.getString("name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumData.getString("id"));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(fillMusicInfo));
            taskList.add(GlobalExecutors.requestExecutor.submit(fillAlbumInfo));

            // 阻塞等待所有请求完成
            taskList.forEach(task -> {
                try {
                    task.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_BI_API, songId))
                    .setFollowRedirects(true)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("uname"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("uid"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("cover"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLrc(NetMusicInfo musicInfo) {
        if (musicInfo.isLrcIntegrated()) return;

        int source = musicInfo.getSource();
        String id = musicInfo.getId();
        String hash = musicInfo.getHash();
        String name = musicInfo.getName();
        double duration = musicInfo.getDuration();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/song/lyric/v1");
            String lrcBody = SdkCommon.ncRequest(Method.POST, LYRIC_API,
                            String.format("{\"id\":\"%s\",\"cp\":false,\"tv\":0,\"lv\":0,\"rv\":0,\"kv\":0,\"yv\":0,\"ytv\":0,\"yrv\":0}", id), options)
                    .executeAsync()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            JSONObject lrc = lrcJson.getJSONObject("lrc");
            JSONObject tLrc = lrcJson.getJSONObject("tlyric");
            JSONObject romaLrc = lrcJson.getJSONObject("romalrc");
            if (JsonUtil.notEmpty(lrc)) {
                // 网易云歌词中包含部分 json 数据需要解析
                String lyric = lrc.getString("lyric");
                if (StringUtil.isEmpty(lyric)) musicInfo.setLrc("");
                else {
                    String[] lsp = lyric.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (String l : lsp) {
                        if (JSON.isValidObject(l)) {
                            JSONObject obj = JSONObject.parseObject(l);
                            Double t = obj.getDouble("t");
                            if (t != null) sb.append(TimeUtil.formatToLrcTime(t / 1000));
                            JSONArray cArray = obj.getJSONArray("c");
                            for (int i = 0, s = cArray.size(); i < s; i++)
                                sb.append(cArray.getJSONObject(i).getString("tx"));
                        } else sb.append(l);
                        sb.append("\n");
                    }
                    musicInfo.setLrc(sb.toString());
                }
            }
            if (JsonUtil.notEmpty(tLrc)) musicInfo.setTrans(tLrc.getString("lyric"));
            if (JsonUtil.notEmpty(romaLrc)) musicInfo.setRoma(romaLrc.getString("lyric"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String lBody = HttpRequest.get(String.format(LYRIC_KG_API, StringUtil.urlEncodeAll(name), hash, duration))
//                    .header(Header.USER_AGENT, "KuGou2012-9020-ExpandSearchManager")
//                    .header("KG-RC", "1")
//                    .header("KG-THash", "expand_search_manager.cpp:852736169:451")
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(lBody);
//            JSONObject info = data.getJSONArray("candidates").getJSONObject(0);
//
//            String lrcBody = HttpRequest.get(String.format(LYRIC_KG_API_2, info.getString("id"), info.getString("accesskey")))
//                    .header(Header.USER_AGENT, "KuGou2012-9020-ExpandSearchManager")
//                    .header("KG-RC", "1")
//                    .header("KG-THash", "expand_search_manager.cpp:852736169:451")
//                    .executeAsync()
//                    .body();
//            JSONObject lrcData = JSONObject.parseObject(lrcBody);
//            String content = lrcData.getString("content");
//            if (StringUtil.isEmpty(content)) {
//                musicInfo.setLrc("");
//                musicInfo.setTrans("");
//                musicInfo.setRoma("");
//                return;
//            }
//            byte[] encKey = new byte[]{0x40, 0x47, 0x61, 0x77, 0x5e, 0x32, 0x74, 0x47, 0x51, 0x36, 0x31, 0x2d, (byte) 0xce, (byte) 0xd2, 0x6e, 0x69};
//            byte[] contentBytes = CryptoUtil.base64DecodeToBytes(content);
//            contentBytes = Arrays.copyOfRange(contentBytes, 4, contentBytes.length);
//            for (int i = 0, len = contentBytes.length; i < len; i++)
//                contentBytes[i] = (byte) (contentBytes[i] ^ encKey[i % 16]);
//            String result = new String(CryptoUtil.decompress(contentBytes), StandardCharsets.UTF_8);
//
//            // 提取酷狗歌词
//            String headExp = "^.*\\[id:\\$\\w+\\]\\n";
//            result = result.replace("\r", "");
//            if (RegexUtil.test(headExp, result)) result = result.replaceAll(headExp, "");
//            String trans = RegexUtil.getGroup0("\\[language:([\\w=\\\\/+]+)\\]", result);
//            String lrc, tlrc;
//            if (StringUtil.notEmpty(trans)) {
//                result = result.replaceAll("\\[language:[\\w=\\\\/+]+\\]\\n", "");
//            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                    .executeAsync()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            String lyric = lrcJson.getString("lyric");
            String trans = lrcJson.getString("trans");
            musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
            musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            byte[] keyBytes = "yeelion".getBytes(StandardCharsets.UTF_8);
//            int keyLen = keyBytes.length;
//            String params = "user=12345,web,web,web&requester=localhost&req=1&rid=MUSIC_" + id + "&lrcx=1";
//            byte[] paramsBytes = params.getBytes(StandardCharsets.UTF_8);
//            int paramsLen = paramsBytes.length;
//            byte[] output = new byte[paramsLen];
//            int i = 0;
//            while (i < paramsLen) {
//                int j = 0;
//                while (j < keyLen && i < paramsLen) {
//                    output[i] = (byte) (keyBytes[j] ^ paramsBytes[i]);
//                    i++;
//                    j++;
//                }
//            }
//            byte[] bodyBytes = HttpRequest.get(LYRIC_KW_API + CryptoUtil.base64Encode(output))
//                    .executeAsync()
//                    .bodyBytes();
//            if (!"tp=content".equals(new String(bodyBytes, 0, 10))) return;
//            int index = ArrayUtil.indexOf(bodyBytes, "\r\n\r\n".getBytes(StandardCharsets.UTF_8)) + 4;
//            byte[] nBytes = Arrays.copyOfRange(bodyBytes, index, bodyBytes.length);
//            byte[] lrcData = CryptoUtil.decompress(nBytes);
////            String lrcStr = new String(lrcData, Charset.forName("gb18030"));
//            String lrcDataStr = new String(lrcData, StandardCharsets.UTF_8);
//            byte[] lrcBytes = CryptoUtil.base64DecodeToBytes(lrcDataStr);
//            int lrcLen = lrcBytes.length;
//            output = new byte[lrcLen];
//            i = 0;
//            while (i < lrcLen) {
//                int j = 0;
//                while (j < keyLen && i < lrcLen) {
//                    output[i] = (byte) (lrcBytes[i] ^ keyBytes[j]);
//                    i++;
//                    j++;
//                }
//            }
//            String lrcStr = new String(output, Charset.forName("gb18030"));

            String lrcBody = SdkCommon.kwRequest(String.format(LYRIC_KW_API, id))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(lrcBody).getJSONObject("data");
            if (JsonUtil.isEmpty(data)) {
                musicInfo.setLrc(null);
                musicInfo.setTrans(null);
                return;
            }
            try {
                // 酷我歌词返回的是数组，需要先处理成字符串！
                // lrclist 可能是数组也可能为 null ！
                JSONArray lrcArray = data.getJSONArray("lrclist");
                if (JsonUtil.notEmpty(lrcArray)) {
                    StringBuilder sb = new StringBuilder();
                    boolean hasTrans = false;
                    for (int i = 0, len = lrcArray.size(); i < len; i++) {
                        JSONObject sentenceJson = lrcArray.getJSONObject(i);
                        JSONObject nextSentenceJson = i + 1 < len ? lrcArray.getJSONObject(i + 1) : null;
                        // 歌词中带有翻译时，最后一句是翻译直接跳过
                        if (hasTrans && JsonUtil.isEmpty(nextSentenceJson)) break;
                        String time = TimeUtil.formatToLrcTime(sentenceJson.getDouble("time"));
                        String nextTime = null;
                        if (JsonUtil.notEmpty(nextSentenceJson))
                            nextTime = TimeUtil.formatToLrcTime(nextSentenceJson.getDouble("time"));
                        // 歌词中带有翻译，有多个 time 相同的歌词时取不重复的第二个
                        if (!time.equals(nextTime)) {
                            sb.append(time);
                            String lineLyric = StringUtil.removeHTMLLabel(sentenceJson.getString("lineLyric"));
                            sb.append(lineLyric);
                            sb.append("\n");
                        } else hasTrans = true;
                    }
                    musicInfo.setLrc(sb.toString());
                } else musicInfo.setLrc(null);

                // 酷我歌词返回的是数组，需要先处理成字符串！
                // lrclist 可能是数组也可能为 null ！
                if (JsonUtil.notEmpty(lrcArray)) {
                    StringBuilder sb = new StringBuilder();
                    boolean hasTrans = false;
                    String lastTime = null;
                    for (int i = 0, len = lrcArray.size(); i < len; i++) {
                        JSONObject sentenceJson = lrcArray.getJSONObject(i);
                        JSONObject nextSentenceJson = i + 1 < len ? lrcArray.getJSONObject(i + 1) : null;
                        String time = TimeUtil.formatToLrcTime(sentenceJson.getDouble("time"));
                        String nextTime = null;
                        if (JsonUtil.notEmpty(nextSentenceJson))
                            nextTime = TimeUtil.formatToLrcTime(nextSentenceJson.getDouble("time"));
                        // 歌词中带有翻译，有多个 time 相同的歌词时取重复的第一个；最后一句也是翻译
                        if (hasTrans && nextTime == null || time.equals(nextTime)) {
                            sb.append(lastTime);
                            String lineLyric = StringUtil.removeHTMLLabel(sentenceJson.getString("lineLyric"));
                            sb.append(lineLyric);
                            sb.append("\n");
                            hasTrans = true;
                        }
                        lastTime = time;
                    }
                    musicInfo.setTrans(sb.toString());
                } else musicInfo.setTrans(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, id))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
            // 先获取歌词 url，再获取歌词
            String lrcUrl = data.getString("lrcUrl");
            String lrcStr = HttpRequest.get(lrcUrl).executeAsync().body();
            musicInfo.setLrc(lrcStr);
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = SdkCommon.qiRequest(String.format(GET_SONG_URL_QI_API, id, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            String lrcUrl = urlJson.getJSONObject("data").getString("lyric");
            musicInfo.setLrc(StringUtil.notEmpty(lrcUrl) ? HttpRequest.get(lrcUrl).executeAsync().body() : "");
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select("p:not(.text-center)");
            StringBuilder sb = new StringBuilder();
            for (Element p : ps) {
                String lrc = p.text().trim();
                if (StringUtil.isEmpty(lrc)) continue;
                sb.append(lrc);
                sb.append('\n');
            }
            musicInfo.setLrc(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select(".message.break-all p");
            StringBuilder sb = new StringBuilder();
            for (Element p : ps) {
                String lrc = p.text().trim();
                if (StringUtil.isEmpty(lrc)) continue;
                sb.append(lrc);
                sb.append('\n');
            }
            musicInfo.setLrc(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String[] sp = id.split("_");
            String lrcBody = HttpRequest.get(String.format(LYRIC_FS_API, sp[0], sp[1]))
                    .executeAsync()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            musicInfo.setLrc(lrcJson.getString("txt"));
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 猫耳(弹幕)
        else if (source == NetMusicSource.ME) {
            String dmBody = HttpRequest.get(String.format(DM_ME_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(dmBody);
            Elements elements = doc.select("d");
            // 限制弹幕数量，避免引发性能问题
            final int dmLimit = 300;
            List<Element> ds = elements.subList(0, Math.min(elements.size(), dmLimit));
            StringBuilder sb = new StringBuilder();
            for (Element d : ds) {
                double time = Double.parseDouble(d.attr("p").split(",", 2)[0]);
                sb.append(TimeUtil.formatToLrcTime(time));
                sb.append(d.text());
                sb.append("\n");
            }
            musicInfo.setLrc(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_BI_API, id))
                    .setFollowRedirects(true)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            musicInfo.setLrc(lrcJson.getString("data"));
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        } else {
            musicInfo.setLrc("");
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }
    }
}
