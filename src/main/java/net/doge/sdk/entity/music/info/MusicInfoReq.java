package net.doge.sdk.entity.music.info;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MusicInfoReq {
    // 歌曲信息 API (单首)
    private final String SINGLE_SONG_DETAIL_API = SdkCommon.prefix + "/song/detail?ids=%s";
    // 节目信息 API
    private final String SINGLE_PROGRAM_DETAIL_API = SdkCommon.prefix + "/dj/program/detail?id=%s";
    // 歌曲信息 API (酷狗)
    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    // 歌曲信息 API (QQ)
    private final String SINGLE_SONG_DETAIL_QQ_API = SdkCommon.prefixQQ33 + "/song?songmid=%s";
    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";
    // 歌曲信息 API (酷我)
    private final String SINGLE_SONG_DETAIL_KW_API = "http://www.kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";
    // 歌曲信息 API (咪咕)
    private final String SINGLE_SONG_DETAIL_MG_API = SdkCommon.prefixMg + "/song?cid=%s";
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
//    private final String LYRIC_API = prefix + "/lyric/new?id=%s";
    private final String LYRIC_API = SdkCommon.prefix + "/lyric?id=%s";
    // 歌词 API (酷狗)
//    private final String LYRIC_KG_API = "http://lyrics.kugou.com/download?ver=1&client=pc&id=%s&accesskey=%s&fmt=lrc&charset=utf8";
    // 歌词 API (QQ)
//    private final String LYRIC_QQ_API = prefixQQ33 + "/lyric?songmid=%s";
    private final String LYRIC_QQ_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0";
    // 歌词 API (酷我)
    private final String LYRIC_KW_API = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=%s&httpsStatus=1";
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
    // 专辑信息 API (咪咕)
    private final String ALBUM_DETAIL_MG_API = SdkCommon.prefixMg + "/album?id=%s";

    /**
     * 补充 NetMusicInfo 歌曲时长
     */
    public void fillDuration(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String songId = musicInfo.getId();

        // 咪咕
        if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDoubleValue("duration"));
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

        List<Future<?>> taskList = new LinkedList<>();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (isProgram) {
                    String songBody = HttpRequest.get(String.format(SINGLE_PROGRAM_DETAIL_API, musicInfo.getProgramId()))
                            .execute()
                            .body();
                    JSONObject songJson = JSONObject.parseObject(songBody).getJSONObject("program");

                    if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("duration") / 1000);
                    if (!musicInfo.hasArtist())
                        musicInfo.setArtist(songJson.getJSONObject("dj").getString("nickname"));
                    if (!musicInfo.hasArtistId())
                        musicInfo.setArtistId(songJson.getJSONObject("dj").getString("userId"));
                    if (!musicInfo.hasAlbumName())
                        musicInfo.setAlbumName(songJson.getJSONObject("radio").getString("name"));
                    if (!musicInfo.hasAlbumId())
                        musicInfo.setAlbumId(songJson.getJSONObject("radio").getString("id"));
                    if (!musicInfo.hasAlbumImage()) {
                        GlobalExecutors.imageExecutor.submit(() -> {
                            BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("coverUrl"));
                            ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                            musicInfo.callback();
                        });
                    }
                } else {
                    String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_API, songId))
                            .execute()
                            .body();
                    JSONArray array = JSONObject.parseObject(songBody).getJSONArray("songs");
                    if (array != null) {
                        JSONObject songJson = array.getJSONObject(0);
                        if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("dt") / 1000);
                        if (!musicInfo.hasArtist())
                            musicInfo.setArtist(SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD));
                        if (!musicInfo.hasArtistId())
                            musicInfo.setArtistId(songJson.getJSONArray("ar").getJSONObject(0).getString("id"));
                        if (!musicInfo.hasAlbumName())
                            musicInfo.setAlbumName(songJson.getJSONObject("al").getString("name"));
                        if (!musicInfo.hasAlbumId())
                            musicInfo.setAlbumId(songJson.getJSONObject("al").getString("id"));
                        if (!musicInfo.hasAlbumImage()) {
                            GlobalExecutors.imageExecutor.submit(() -> {
                                BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getJSONObject("al").getString("picUrl"));
                                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                                musicInfo.callback();
                            });
                        }
                    }
                }
            }));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                // 酷狗接口请求需要带上 cookie ！
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, songId))
                        .cookie(SdkCommon.COOKIE)
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
                // 时长是毫秒，转为秒
                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDoubleValue("timelength") / 1000);
                if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtists(data, NetMusicSource.KG));
                if (!musicInfo.hasArtistId()) {
                    JSONArray artistArray = data.getJSONArray("authors");
                    if (artistArray != null && !artistArray.isEmpty())
                        musicInfo.setArtistId(artistArray.getJSONObject(0).getString("author_id"));
                }
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album_name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("album_id"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("img"));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
                if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyrics"));
            }));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
                JSONObject trackInfo = data.getJSONObject("track_info");
                JSONArray singer = trackInfo.getJSONArray("singer");
                JSONObject album = trackInfo.getJSONObject("album");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("interval"));
                if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtists(trackInfo, NetMusicSource.QQ));
                if (!musicInfo.hasArtistId())
                    musicInfo.setArtistId(singer.getJSONObject(0).getString("mid"));
                if (!musicInfo.hasAlbumName())
                    musicInfo.setAlbumName(album.getString("name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(album.getString("mid"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        // QQ 的歌曲专辑图片需要额外请求接口获得！
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("mid")));
                        // 有的歌曲没有专辑，先找备份专辑图片，如果还没有就将歌手的图片作为封面
                        if (albumImage == null)
                            albumImage = SdkUtil.getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, album.getString("pmid")));
                        if (albumImage == null)
                            albumImage = SdkUtil.getImageFromUrl(String.format(ARTIST_IMG_QQ_API, singer.getJSONObject(0).getString("mid")));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = SdkCommon.kwRequest(String.format(SINGLE_SONG_DETAIL_KW_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
                if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("artist"));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("artistid"));
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumid"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("pic"));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");

            String albumId = data.getJSONObject("album").getString("id");
            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtists(data, NetMusicSource.MG));
            if (!musicInfo.hasArtistId())
                musicInfo.setArtistId(data.getJSONArray("artists").getJSONObject(0).getString("id"));
            // 咪咕的专辑名称需要额外请求专辑信息接口！
            if (!musicInfo.hasAlbumName()) {
                String albumBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumId))
                        .execute()
                        .body();
                JSONObject albumData = JSONObject.parseObject(albumBody).getJSONObject("data");
                musicInfo.setAlbumName(albumData.getString("name"));
            }
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumId);
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("picUrl"));
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyric"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String songBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(SINGLE_SONG_DETAIL_QI_API, songId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtists(data, NetMusicSource.QI));
            if (!musicInfo.hasArtistId()) {
                JSONArray artistArray = data.getJSONArray("artist");
                if (artistArray != null && !artistArray.isEmpty())
                    musicInfo.setArtistId(artistArray.getJSONObject(0).getString("artistCode"));
            }
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("albumTitle"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("albumAssetCode"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("pic"));
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, songId))
                        .cookie(SdkCommon.HF_COOKIE)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(songBody);
                String dataStr = ReUtil.get("music: \\[.*?(\\{.*?\\}).*?\\]", doc.html(), 1);
                // json 字段带引号
                if (StringUtil.notEmpty(dataStr)) dataStr = dataStr.replaceAll(" (\\w+):", "'$1':");
                JSONObject data = JSONObject.parseObject(dataStr);

                Elements a = doc.select(".m-3.text-center h5 a");

                if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(ReUtil.get("user-(\\d+)\\.htm", a.attr("href"), 1));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        String picUrl = data.getString("pic");
                        if (picUrl.contains("music.126.net"))
                            picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                        else if (picUrl.contains("y.gtimg.cn"))
                            picUrl = picUrl.replaceFirst("300x300", "500x500");
                        if (!picUrl.startsWith("http")) picUrl = "https://www.hifini.com/" + picUrl;
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, songId))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(songBody);
                String dataStr = ReUtil.get("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html(), 1);
                if (StringUtil.notEmpty(dataStr)) {
                    dataStr = dataStr.replaceFirst("base64_decode\\(\"(.*?)\"\\)", "\"\"");
                    // json 字段带引号
                    dataStr = dataStr.replaceAll(" (\\w+):", "'$1':");
                }
                JSONObject data = JSONObject.parseObject(dataStr);

                Elements a = doc.select(".m-3.text-center h5 a");

                if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(ReUtil.get("user-(\\d+)\\.htm", a.attr("href"), 1));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        String picUrl = data.getString("cover");
                        if (StringUtil.isEmpty(picUrl)) picUrl = data.getString("pic");
                        if (picUrl.contains("music.126.net"))
                            picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                        else if (picUrl.contains("y.gtimg.cn"))
                            picUrl = picUrl.replaceFirst("300x300", "500x500");
                        if (!picUrl.startsWith("http")) picUrl = "http://www.gggmusic.com/" + picUrl;
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_FS_API, StringUtil.encode(songId.replace("_", "$"))))
                    .execute()
                    .body();
            JSONObject data = JSONArray.parseArray(songBody).getJSONObject(0);

            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("nickname"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtist(data.getString("userid"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("avatar"));
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_XM_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
                JSONObject trackInfo = data.getJSONObject("trackInfo");
                JSONObject albumInfo = data.getJSONObject("albumInfo");

                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(trackInfo.getString("anchorUid"));
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumInfo.getString("title"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumInfo.getString("albumId"));
                if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("duration"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl("https:" + trackInfo.getString("coverPath"));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
            musicInfo.setLrc("");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
                // 时长是毫秒，转为秒
                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration") / 1000);
                if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("username"));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("user_id"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("front_cover"));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
            // 猫耳的专辑需要单独请求专辑信息接口！
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String albumBody = HttpRequest.get(String.format(SONG_ALBUM_DETAIL_ME_API, songId))
                        .execute()
                        .body();
                String infoStr = JSONObject.parseObject(albumBody).getString("info");
                // 可能是字符串也可能是 json 对象，先判断
                if (!JSON.isValidObject(infoStr)) return;
                JSONObject info = JSONObject.parseObject(infoStr);
                JSONObject albumData = info.getJSONObject("drama");
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumData.getString("name"));
                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumData.getString("id"));
            }));
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_BI_API, songId))
                        .setFollowRedirects(true)
                        .cookie(SdkCommon.BI_COOKIE)
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
                // 时长是毫秒，转为秒
                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
                if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("uname"));
                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("uid"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("cover"));
                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
                    });
                }
            }));
        }

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

    /**
     * 根据为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLrc(NetMusicInfo netMusicInfo) {
        if (netMusicInfo.isLrcIntegrated()) return;

        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();
//        String hash = netMusicInfo.getHash();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_API, id))
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            JSONObject lrc = lrcJson.getJSONObject("lrc");
            JSONObject tLrc = lrcJson.getJSONObject("tlyric");
            JSONObject romaLrc = lrcJson.getJSONObject("romalrc");
            if (lrc != null) netMusicInfo.setLrc(lrc.getString("lyric"));
            if (tLrc != null) netMusicInfo.setTrans(tLrc.getString("lyric"));
            if (romaLrc != null) netMusicInfo.setRoma(romaLrc.getString("lyric"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String songBody = HttpRequest.get(String.format(LYRIC_KG_API, id, hash))
//                    .execute()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody);
//            String lyric = StringUtils.base64Decode(data.getString("content"));
//            netMusicInfo.setLrc(lyric);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            String lyric = lrcJson.getString("lyric");
            String trans = lrcJson.getString("trans");
            netMusicInfo.setLrc(StringUtil.removeHTMLLabel(StringUtil.base64Decode(lyric)));
            netMusicInfo.setTrans(StringUtil.removeHTMLLabel(StringUtil.base64Decode(trans)));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String lrcBody = SdkCommon.kwRequest(String.format(LYRIC_KW_API, id))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(lrcBody).getJSONObject("data");
            if (data == null) {
                netMusicInfo.setLrc(null);
                netMusicInfo.setTrans(null);
                return;
            }
            try {
                // 酷我歌词返回的是数组，需要先处理成字符串！
                // lrclist 可能是数组也可能为 null ！
                JSONArray lrcArray = data.getJSONArray("lrclist");
                if (lrcArray != null) {
                    StringBuffer sb = new StringBuffer();
                    boolean hasTrans = false;
                    for (int i = 0, len = lrcArray.size(); i < len; i++) {
                        JSONObject sentenceJson = lrcArray.getJSONObject(i);
                        JSONObject nextSentenceJson = i + 1 < len ? lrcArray.getJSONObject(i + 1) : null;
                        // 歌词中带有翻译时，最后一句是翻译直接跳过
                        if (hasTrans && nextSentenceJson == null) break;
                        String time = TimeUtil.formatToLrcTime(sentenceJson.getDouble("time"));
                        String nextTime = null;
                        if (nextSentenceJson != null)
                            nextTime = TimeUtil.formatToLrcTime(nextSentenceJson.getDouble("time"));
                        // 歌词中带有翻译，有多个 time 相同的歌词时取不重复的第二个
                        if (!time.equals(nextTime)) {
                            sb.append(time);
                            String lineLyric = StringUtil.removeHTMLLabel(sentenceJson.getString("lineLyric"));
                            sb.append(lineLyric);
                            sb.append("\n");
                        } else hasTrans = true;
                    }
                    netMusicInfo.setLrc(sb.toString());
                } else netMusicInfo.setLrc(null);

                // 酷我歌词返回的是数组，需要先处理成字符串！
                // lrclist 可能是数组也可能为 null ！
                if (lrcArray != null) {
                    StringBuffer sb = new StringBuffer();
                    boolean hasTrans = false;
                    String lastTime = null;
                    for (int i = 0, len = lrcArray.size(); i < len; i++) {
                        JSONObject sentenceJson = lrcArray.getJSONObject(i);
                        JSONObject nextSentenceJson = i + 1 < len ? lrcArray.getJSONObject(i + 1) : null;
                        String time = TimeUtil.formatToLrcTime(sentenceJson.getDouble("time"));
                        String nextTime = null;
                        if (nextSentenceJson != null)
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
                    netMusicInfo.setTrans(sb.toString());
                } else netMusicInfo.setTrans(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
//            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, id))
//                    .execute()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
//            netMusicInfo.setLrc(data.getString("lyric"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(GET_SONG_URL_QI_API, id, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            String lrcUrl = urlJson.getJSONObject("data").getString("lyric");
            netMusicInfo.setLrc(StringUtil.notEmpty(lrcUrl) ? HttpRequest.get(lrcUrl).execute().body() : "");
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .cookie(SdkCommon.HF_COOKIE)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select("p:not(.text-center)");
            StringBuffer sb = new StringBuffer();
            for (Element p : ps) {
                String lrc = p.text().trim();
                if (StringUtil.isEmpty(lrc)) continue;
                sb.append(lrc);
                sb.append('\n');
            }
            netMusicInfo.setLrc(sb.toString());
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(songBody);
            Elements ps = doc.select(".message.break-all p");
            StringBuffer sb = new StringBuffer();
            for (Element p : ps) {
                String lrc = p.text().trim();
                if (StringUtil.isEmpty(lrc)) continue;
                sb.append(lrc);
                sb.append('\n');
            }
            netMusicInfo.setLrc(sb.toString());
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String[] sp = id.split("_");
            String lrcBody = HttpRequest.get(String.format(LYRIC_FS_API, sp[0], sp[1]))
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            netMusicInfo.setLrc(lrcJson.getString("txt"));
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }

        // 猫耳(弹幕)
        else if (source == NetMusicSource.ME) {
            String dmBody = HttpRequest.get(String.format(DM_ME_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(dmBody);
            Elements elements = doc.select("d");
            // 限制弹幕数量，避免引发性能问题
            final int dmLimit = 300;
            List<Element> ds = elements.subList(0, Math.min(elements.size(), dmLimit));
            StringBuffer sb = new StringBuffer();
            for (Element d : ds) {
                Double time = Double.parseDouble(d.attr("p").split(",", 2)[0]);
                sb.append(TimeUtil.formatToLrcTime(time));
                sb.append(d.text());
                sb.append("\n");
            }
            netMusicInfo.setLrc(sb.toString());
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_BI_API, id))
                    .setFollowRedirects(true)
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            netMusicInfo.setLrc(lrcJson.getString("data"));
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        } else {
            netMusicInfo.setLrc("");
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }
    }
}
