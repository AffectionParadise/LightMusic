package net.doge.sdk.service.music.info;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.service.music.info.lyrichero.kg.KgLyricHero;
import net.doge.sdk.service.music.info.lyrichero.kw.KwLyricHero;
import net.doge.sdk.service.music.info.lyrichero.mg.MgLyricHero;
import net.doge.sdk.service.music.info.lyrichero.nc.NcLyricHero;
import net.doge.sdk.service.music.info.lyrichero.qq.QqLyricHero;
import net.doge.sdk.util.SdkUtil;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.HttpResponse;
import net.doge.sdk.util.http.constant.Header;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.core.*;
import net.doge.util.os.FileUtil;
import net.doge.util.ui.ImageUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MusicInfoReq {
    private static MusicInfoReq instance;

    private MusicInfoReq() {
    }

    public static MusicInfoReq getInstance() {
        if (instance == null) instance = new MusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (单首)
    private final String SINGLE_SONG_DETAIL_API = "https://music.163.com/api/v3/song/detail";
    // 节目信息 API
    private final String SINGLE_PROGRAM_DETAIL_API = "https://music.163.com/api/dj/program/detail";
    // 歌曲信息 API (酷狗)
//    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    private final String SINGLE_SONG_DETAIL_KG_API_V2 = "/v2/get_res_privilege/lite";
    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";
    // 歌曲信息 API (酷我)
    private final String SINGLE_SONG_DETAIL_KW_API = "https://kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";
    // 歌曲信息 API (咪咕) (下面那个接口能获取无版权音乐的信息)
//    private final String SINGLE_SONG_DETAIL_MG_API = "https://music.migu.cn/v3/api/music/audioPlayer/songs?copyrightId=%s";
    private final String SINGLE_SONG_DETAIL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";
    // 歌曲信息 API (喜马拉雅)
    private final String SINGLE_SONG_DETAIL_XM_API = "https://www.ximalaya.com/revision/track/simple?trackId=%s";
    // 歌曲信息 API (千千)
    private final String SINGLE_SONG_DETAIL_QI_API = "https://music.91q.com/v1/song/info?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifiti.com/thread-%s.htm";
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
    // 歌曲信息 API (发姐)
    private final String SINGLE_SONG_DETAIL_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";
    // 歌曲信息 API (李志)
    private final String SINGLE_SONG_DETAIL_LZ_API = "https://www.lizhinb.com/?audioigniter_playlist_id=%s";

    // 歌曲 URL 获取 API (千千)
    private final String GET_SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";
    // 歌词 API (5sing)
    private final String LYRIC_FS_API = "http://5sing.kugou.com/fm/m/json/lrc?songType=%s&songId=%s";
    // 弹幕 API (猫耳)
    private final String DM_ME_API = "https://www.missevan.com/sound/getdm?soundid=%s";
    // 歌词 API (哔哩哔哩)
    private final String LYRIC_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/lyric?sid=%s";
    // 歌词 API (发姐)
    private final String LYRIC_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&scope=remote_lyric&id=%s";

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
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
            if (!musicInfo.hasDuration()) musicInfo.setDuration(DurationUtil.toSeconds(data.getString("length")));
        }
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的
        if (musicInfo.isIntegrated()) return;

        String id = musicInfo.getId();
        String hash = musicInfo.getHash();
        int source = musicInfo.getSource();
        boolean isProgram = musicInfo.isProgram();

        // 网易云
        if (source == NetMusicSource.NC) {
            if (isProgram) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_PROGRAM_DETAIL_API, String.format("{\"id\":\"%s\"}", musicInfo.getProgramId()), options)
                        .executeAsStr();
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
                String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_SONG_DETAIL_API, String.format("{\"c\":\"[{'id':'%s'}]\"}", id), options)
                        .executeAsStr();
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
//            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, id))
//                    .cookie(SdkCommon.KG_COOKIE)
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
//            if (JsonUtil.notEmpty(data)) {
//                // 时长是毫秒，转为秒
//                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("timelength") / 1000);
//                if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
//                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
//                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album_name"));
//                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("album_id"));
//                if (!musicInfo.hasAlbumImage()) {
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("img"));
//                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
//                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
//                        musicInfo.callback();
//                    });
//                }
////                if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyrics"));
//            } else {
            // 歌曲信息接口有时返回为空，直接用 V2 版本接口，不过由于部分信息不完整，作为备选
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(SINGLE_SONG_DETAIL_KG_API_V2);
            String dat = String.format("{\"appid\":%s,\"area_code\":1,\"behavior\":\"play\",\"clientver\":%s,\"need_hash_offset\":1,\"relate\":1," +
                            "\"support_verify\":1,\"resource\":[{\"type\":\"audio\",\"page_id\":0,\"hash\":\"%s\",\"album_id\":0}]}",
                    KugouReqBuilder.appid, KugouReqBuilder.clientver, hash);
            String songBody = SdkCommon.kgRequest(null, dat, options)
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header("x-router", "media.store.kugou.com")
                    .executeAsStr();
            JSONObject songData = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);
            JSONObject info = songData.getJSONObject("info");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDouble("duration") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(songData.getString("singername"));
//                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(songData.getString("albumname"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(songData.getString("recommend_album_id"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(info.getString("image").replace("/{size}", ""));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
//            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String songBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                    .executeAsStr();
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
                    if (albumImage == ImageConstants.DEFAULT_IMG)
                        albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("pmid")));
                    if (albumImage == ImageConstants.DEFAULT_IMG)
                        albumImage = SdkUtil.getImageFromUrl(String.format(ARTIST_IMG_QQ_API, SdkUtil.parseArtistId(trackInfo)));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(SINGLE_SONG_DETAIL_KW_API, id)).execute();
            if (!resp.isSuccessful()) return;
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
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, id))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumId"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(DurationUtil.toSeconds(data.getString("length")));
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
            String songBody = SdkCommon.qiRequest(String.format(SINGLE_SONG_DETAIL_QI_API, id, System.currentTimeMillis()))
                    .executeAsStr();
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
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsStr();
            Document doc = Jsoup.parse(songBody);
            String dataStr = RegexUtil.getGroup1("audio:\\[.*?(\\{.*?\\}).*?\\]", doc.html());
            // json 字段带引号
            if (StringUtil.notEmpty(dataStr)) dataStr = dataStr.replaceAll("(\\w+):'(.*?)'", "'$1':'$2'");
            JSONObject data = JSONObject.parseObject(dataStr);

            Elements a = doc.select(".m-3.text-center h5 a");

            if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
            if (!musicInfo.hasArtistId())
                musicInfo.setArtistId(RegexUtil.getGroup1("user-(\\d+)\\.htm", a.attr("href")));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    String picUrl = data.getString("cover");
                    if (picUrl.contains("music.126.net"))
                        picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                    else if (picUrl.contains("y.gtimg.cn"))
                        picUrl = picUrl.replaceFirst("300x300", "500x500");
                    if (!picUrl.startsWith("http")) picUrl = "https://www.hifiti.com/" + picUrl;
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .executeAsStr();
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
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_FS_API, UrlUtil.encodeAll(id.replace("_", "$"))))
                    .executeAsStr();
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
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_XM_API, id))
                    .executeAsStr();
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
            musicInfo.setLyric("");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            // 歌曲信息
            Runnable fillMusicInfo = () -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, id))
                        .executeAsStr();
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
                String albumBody = HttpRequest.get(String.format(SONG_ALBUM_DETAIL_ME_API, id))
                        .executeAsStr();
                String infoStr = JSONObject.parseObject(albumBody).getString("info");
                // 可能是字符串也可能是 json 对象，先判断
                if (!JsonUtil.isValidObject(infoStr)) return;
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
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_BI_API, id))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
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

        // 发姐
        else if (source == NetMusicSource.FA) {
            // 获取发姐请求参数
            String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                    .executeAsStr();
            Document doc = Jsoup.parse(body);
            Elements ap = doc.select("#aplayer1");
            String musicSet = UrlUtil.encodeAll(ap.attr("data-songs"));
            String _nonce = ap.attr("data-_nonce");

            String songInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_FA_API, musicSet, _nonce))
                    .executeAsStr();
            JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
            JSONObject data = songInfoJson.getJSONObject("msg");
            JSONArray songArray = data.getJSONArray("songs");
            for (int i = 0, s = songArray.size(); i < s; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                if (!id.equals(songJson.getString("id"))) continue;
                if (!musicInfo.hasArtist()) musicInfo.setArtist(songJson.getString("author"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("pic"));
                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
                break;
            }
        }

        // 李志
        else if (source == NetMusicSource.LZ) {
            String[] sp = id.split("_");
            String albumSongBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_LZ_API, sp[0]))
                    .executeAsStr();
            JSONArray songArray = JSONArray.parseArray(albumSongBody);
            JSONObject songJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("cover"));
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
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;

        int source = musicInfo.getSource();
        String id = musicInfo.getId();

        // 网易云
        if (source == NetMusicSource.NC) {
            NcLyricHero.getInstance().fillLyric(musicInfo);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            KgLyricHero.getInstance().fillLyric(musicInfo);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            QqLyricHero.getInstance().fillLyric(musicInfo);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            KwLyricHero.getInstance().fillLyric(musicInfo);
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            MgLyricHero.getInstance().fillLyric(musicInfo);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = SdkCommon.qiRequest(String.format(GET_SONG_URL_QI_API, id, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            String lyricUrl = urlJson.getJSONObject("data").getString("lyric");
            musicInfo.setLyric(StringUtil.notEmpty(lyricUrl) ? HttpRequest.get(lyricUrl).executeAsStr() : "");
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsStr();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select("p:not(.text-center)");
            StringBuilder sb = new StringBuilder();
            for (Element p : ps) {
                List<Node> nodes = p.childNodes();
                for (Node node : nodes) {
                    if (!(node instanceof TextNode)) continue;
                    String lyric = node.toString();
                    if (StringUtil.isEmpty(lyric)) continue;
                    sb.append(lyric);
                    sb.append('\n');
                }
            }
            musicInfo.setLyric(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select(".message.break-all p");
            StringBuilder sb = new StringBuilder();
            for (Element p : ps) {
                String lyric = p.text().trim();
                if (StringUtil.isEmpty(lyric)) continue;
                sb.append(lyric);
                sb.append('\n');
            }
            musicInfo.setLyric(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String[] sp = id.split("_");
            String lyricBody = HttpRequest.get(String.format(LYRIC_FS_API, sp[0], sp[1]))
                    .executeAsStr();
            JSONObject lyricJson = JSONObject.parseObject(lyricBody);
            musicInfo.setLyric(lyricJson.getString("txt"));
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 猫耳(弹幕)
        else if (source == NetMusicSource.ME) {
            String dmBody = HttpRequest.get(String.format(DM_ME_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(dmBody);
            Elements elements = doc.select("d");
            // 限制弹幕数量，避免引发性能问题
            final int dmLimit = 300;
            List<Element> ds = elements.subList(0, Math.min(elements.size(), dmLimit));
            StringBuilder sb = new StringBuilder();
            for (Element d : ds) {
                double time = Double.parseDouble(d.attr("p").split(",", 2)[0]);
                sb.append(DurationUtil.formatToLyricTime(time));
                sb.append(d.text());
                sb.append("\n");
            }
            musicInfo.setLyric(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String lyricBody = HttpRequest.get(String.format(LYRIC_BI_API, id))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject lyricJson = JSONObject.parseObject(lyricBody);
            musicInfo.setLyric(lyricJson.getString("data"));
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 发姐
        else if (source == NetMusicSource.FA) {
            String lyricBody = HttpRequest.get(String.format(LYRIC_FA_API, id))
                    .executeAsStr();
            musicInfo.setLyric(lyricBody);
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 李志
        else if (source == NetMusicSource.LZ) {
            String[] sp = id.split("_");
            String albumSongBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_LZ_API, sp[0]))
                    .executeAsStr();
            JSONArray songArray = JSONArray.parseArray(albumSongBody);
            JSONObject lyricJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
            String lyric = lyricJson.getString("lyrics").replace("\r\n", "\n");
            musicInfo.setLyric(lyric);
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // 其他
        else {
            musicInfo.setLyric("");
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }
    }
}
