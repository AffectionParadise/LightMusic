package net.doge.sdk.service.playlist.info;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.exception.ExceptionUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.media.DurationUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaylistInfoReq {
    private static PlaylistInfoReq instance;

    private PlaylistInfoReq() {
    }

    public static PlaylistInfoReq getInstance() {
        if (instance == null) instance = new PlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API
    private final String PLAYLIST_DETAIL_API = "https://music.163.com/api/v6/playlist/detail";
    // 歌单歌曲 API
    private final String BATCH_SONGS_DETAIL_API = "https://music.163.com/api/v3/song/detail";
    // 歌单信息 API (酷狗)
    private final String PLAYLIST_DETAIL_KG_API = "https://mobiles.kugou.com/api/v5/special/info_v2?appid=1058&specialid=0&global_specialid=%s&format=jsonp&srcappid=2919&clientver=20000&clienttime=1586163242519&mid=1586163242519&uuid=1586163242519&dfid=-&signature=%s";
    // 歌单歌曲 API (酷狗)
    private final String PLAYLIST_SONGS_KG_API = "https://mobiles.kugou.com/api/v5/special/song_v2?appid=1058&global_specialid=%s&specialid=0&plat=0&version=8000&page=%s&pagesize=%s&srcappid=2919&clientver=20000&clienttime=1586163263991&mid=1586163263991&uuid=1586163263991&dfid=-&signature=%s";
    //    private final String PLAYLIST_DETAIL_KG_API = "https://m.kugou.com/plist/list/%s?json=true&page=%s";
    // 歌单信息 API (QQ)
    private final String PLAYLIST_DETAIL_QQ_API = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";
    // 歌单信息 API (酷我)
//    private final String PLAYLIST_DETAIL_KW_API = "https://kuwo.cn/api/www/playlist/playListInfo?pid=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String PLAYLIST_DETAIL_KW_API = "http://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pid=%s&pn=%s&rn=%s&encode=utf8&keyset=pl2012&identity=kuwo&pcmp4=1&vipver=MUSIC_9.0.5.0_W1&newver=1";
    // 歌单信息 API (咪咕)
//    private final String PLAYLIST_DETAIL_MG_API = PREFIX_MG + "/playlist?id=%s";
    private final String PLAYLIST_DETAIL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?needSimple=00&resourceType=2021&resourceId=%s";
    // 歌单歌曲 API (咪咕)
    private final String PLAYLIST_SONGS_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/user/queryMusicListSongs.do?musicListId=%s&pageNo=%s&pageSize=%s";
    // 歌单信息 API (千千)
    private final String PLAYLIST_DETAIL_QI_API = "https://music.91q.com/v1/tracklist/info?appid=16073360&id=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // 歌单信息 API (5sing)
    private final String PLAYLIST_DETAIL_FS_API = "http://5sing.kugou.com/%s/dj/%s.html";
    // 歌单信息 API (猫耳)
    private final String PLAYLIST_DETAIL_ME_API = "https://www.missevan.com/sound/soundAllList?albumid=%s";
    // 歌单信息 API (哔哩哔哩)
    private final String PLAYLIST_DETAIL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/menu/info?sid=%s";
    // 歌单歌曲 API (哔哩哔哩)
    private final String PLAYLIST_SONGS_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/of-menu?sid=%s&pn=%s&ps=%s";

    /**
     * 根据歌单 id 和 source 预加载歌单信息
     */
    public void preloadPlaylistInfo(NetPlaylistInfo playlistInfo) {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImgThumb(SdkUtil.extractCover(playlistInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(int source, String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");
            if (JsonUtil.notEmpty(playlistJson)) {
                JSONObject ct = playlistJson.getJSONObject("creator");

                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("name");
                String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                Integer trackCount = playlistJson.getIntValue("trackCount");
                Long playCount = playlistJson.getLong("playCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            HttpResponse resp = HttpRequest.get(String.format(PLAYLIST_DETAIL_KG_API, id,
                            CryptoUtil.md5("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwtappid=1058clienttime=1586163242519clientver=20000dfid=-format=jsonpglobal_specialid="
                                    + id + "mid=1586163242519specialid=0srcappid=2919uuid=1586163242519NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt")))
                    .header(Header.REFERER, "https://m3ws.kugou.com/share/index.php")
                    .header("mid", "1586163242519")
                    .header("dfid", "-")
                    .header("clienttime", "1586163242519")
                    .execute();
            if (resp.isSuccessful()) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
                if (JsonUtil.notEmpty(playlistJson)) {
                    String playlistId = playlistJson.getString("specialid");
                    String name = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Integer trackCount = playlistJson.getIntValue("songcount");
                    Long playCount = playlistJson.getLong("playcount");
                    String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setTrackCount(trackCount);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                    .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
            if (JsonUtil.notEmpty(cdlist)) {
                JSONObject playlistJson = cdlist.getJSONObject(0);
                String playlistId = playlistJson.getString("disstid");
                String name = playlistJson.getString("dissname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("visitnum");
                Integer trackCount = playlistJson.getIntValue("songnum");
                String coverImgThumbUrl = playlistJson.getString("logo");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QQ);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, 1, 1))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
//            if (JsonUtil.notEmpty(playlistJson)) {
//                String playlistId = playlistJson.getString("id");
//                String name = playlistJson.getString("name");
//                String creator = playlistJson.getString("userName");
//                Long playCount = playlistJson.getLong("listencnt");
//                Integer trackCount = playlistJson.getIntValue("total");
//                String coverImgThumbUrl = playlistJson.getString("img");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.KW);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(name);
//                playlistInfo.setCreator(creator);
//                playlistInfo.setTrackCount(trackCount);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(playlistInfo);
//            }

            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, 0, 1))
                    .executeAsStr();
            JSONObject playlistJson = JSONObject.parseObject(playlistInfoBody);
            if (JsonUtil.notEmpty(playlistJson)) {
                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("title");
                String creator = playlistJson.getString("uname");
                String creatorId = playlistJson.getString("uid");
                Long playCount = playlistJson.getLong("playnum");
                Integer trackCount = playlistJson.getIntValue("total");
                String coverImgThumbUrl = playlistJson.getString("pic");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KW);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray resource = playlistInfoJson.getJSONArray("resource");
            if (JsonUtil.notEmpty(resource)) {
                JSONObject playlistJson = resource.getJSONObject(0);

                String playlistId = playlistJson.getString("musicListId");
                String name = playlistJson.getString("title");
                String creator = playlistJson.getString("ownerName");
                String creatorId = playlistJson.getString("ownerId");
                Long playCount = playlistJson.getJSONObject("opNumItem").getLong("playNum");
                Integer trackCount = playlistJson.getIntValue("musicNum");
                String coverImgThumbUrl = playlistJson.getJSONObject("imgItem").getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, 1, 1, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(playlistJson)) {
                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("title");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("pic");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QI);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("info");
            if (JsonUtil.notEmpty(playlistJson)) {
                JSONObject album = playlistJson.getJSONObject("album");

                String playlistId = album.getString("id");
                String name = album.getString("title");
                String creator = album.getString("username");
                String creatorId = album.getString("user_id");
                Integer trackCount = album.getIntValue("music_count");
                Long playCount = album.getLong("view_count");
                String coverImgThumbUrl = album.getString("front_cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.ME);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_BI_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(playlistJson)) {
                JSONObject stat = playlistJson.getJSONObject("statistic");

                String playlistId = playlistJson.getString("menuId");
                String name = playlistJson.getString("title");
                String creator = playlistJson.getString("uname");
                String creatorId = playlistJson.getString("uid");
                Integer trackCount = playlistJson.getIntValue("snum", -1);
                Long playCount = stat.getLong("play");
                String coverImgThumbUrl = playlistJson.getString("cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.BI);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;

        int source = playlistInfo.getSource();
        String id = playlistInfo.getId();
        String creatorId = playlistInfo.getCreatorId();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");
            JSONObject ct = playlistJson.getJSONObject("creator");

            String coverImgUrl = playlistJson.getString("coverImgUrl");
            String description = playlistJson.getString("description");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(StringUtil.notEmpty(description) ? description : "");
            if (!playlistInfo.hasCreator())
                playlistInfo.setCreator(JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "");
            if (!playlistInfo.hasCreatorId())
                playlistInfo.setCreatorId(JsonUtil.notEmpty(ct) ? ct.getString("userId") : "");
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(playlistJson));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(playlistJson.getIntValue("trackCount"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KG_API, id,
                            CryptoUtil.md5("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwtappid=1058clienttime=1586163242519clientver=20000dfid=-format=jsonpglobal_specialid="
                                    + id + "mid=1586163242519specialid=0srcappid=2919uuid=1586163242519NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt")))
                    .header(Header.REFERER, "https://m3ws.kugou.com/share/index.php")
                    .header("mid", "1586163242519")
                    .header("dfid", "-")
                    .header("clienttime", "1586163242519")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            String description = data.getString("intro");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getIntValue("songcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                    .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
            if (JsonUtil.isEmpty(cdlist)) return;
            JSONObject data = cdlist.getJSONObject(0);

            String coverImgUrl = data.getString("logo");
            String description = data.getString("desc").replace("<br>", "\n");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getIntValue("songnum"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, 1, 1))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("img500");
//            String description = data.getString("info");
//
//            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
//            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
//            playlistInfo.setDescription(description);
//            if (!playlistInfo.hasTag()) playlistInfo.setTag(data.getString("tag").replace(",", "、"));

            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, 0, 1))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(playlistInfoBody);

            String coverImgUrl = data.getString("pic");
            String description = data.getString("info");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(data.getString("tag").replace(",", "、"));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONArray("resource").getJSONObject(0);

            String coverImgUrl = data.getJSONObject("imgItem").getString("img");
            String summary = data.getString("summary");
            String description = StringUtil.isEmpty(summary) ? "" : summary;

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, 1, 1, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = playlistJson.getString("pic");
            String description = playlistJson.getString("desc");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(playlistJson));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(playlistJson.getIntValue("trackCount"));
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_FS_API, creatorId, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(playlistInfoBody);

            String coverImgUrl = doc.select(".lt.w_30 img").attr("src");
            String description = doc.select("#normalIntro").first().ownText();
            StringJoiner sj = new StringJoiner("、");
            Elements elems = doc.select(".c_wap.tag_box label");
            elems.forEach(elem -> sj.add(elem.text()));
            String tag = sj.toString();

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(tag);
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject info = playlistInfoJson.getJSONObject("info");
            if (JsonUtil.isEmpty(info)) return;
            JSONObject album = info.getJSONObject("album");

            String coverImgUrl = album.getString("front_cover");
            String description = HtmlUtil.removeHtmlLabel(album.getString("intro"));

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(info));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(album.getIntValue("music_count"));
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_BI_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("cover");
            String description = data.getString("intro");

            if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            playlistInfo.setTag("");
        }
    }

    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(String id, int source, int page, int limit) {
        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
        playlistInfo.setSource(source);
        playlistInfo.setId(id);
        return getMusicInfoInPlaylist(playlistInfo, page, limit);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> res = new LinkedList<>();

        int source = playlistInfo.getSource();
        String id = playlistInfo.getId();
        String creatorId = playlistInfo.getCreatorId();

        // 网易云
        if (source == NetMusicSource.NC) {
            // 歌曲列表
            Runnable getMusicInfo = () -> {
                // 先获取 trackId 列表
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String trackIdBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                        .executeAsStr();
                JSONArray trackIdArray = JSONObject.parseObject(trackIdBody).getJSONObject("playlist").getJSONArray("trackIds");
                StringJoiner sj = new StringJoiner(",");
                for (int i = (page - 1) * limit, s = Math.min(trackIdArray.size(), page * limit); i < s; i++)
                    sj.add(String.format("{'id':'%s'}", trackIdArray.getJSONObject(i).getString("id")));
                String ids = sj.toString();

                String playlistInfoBody = SdkCommon.ncRequest(Method.POST, BATCH_SONGS_DETAIL_API, String.format("{\"c\":\"[%s]\"}", ids), options)
                        .executeAsStr();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONArray songArray = playlistInfoJson.getJSONArray("songs");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);
                    JSONObject albumJson = songJson.getJSONObject("al");

                    String songId = songJson.getString("id");
                    String name = songJson.getString("name").trim();
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
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);
                    musicInfo.setQualityType(qualityType);

                    res.add(musicInfo);
                }
            };
            // 歌曲总数
            Runnable getTotal = () -> {
                // 网易云获取歌单歌曲总数需要额外请求歌单详情接口！
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_DETAIL_API, String.format("{\"id\":\"%s\",\"n\":100000,\"s\":8}", id), options)
                        .executeAsStr();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                total.set(playlistInfoJson.getJSONObject("playlist").getIntValue("trackCount"));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getMusicInfo));
            taskList.add(GlobalExecutors.requestExecutor.submit(getTotal));

            taskList.forEach(task -> {
                try {
                    task.get();
                } catch (Exception e) {
                    ExceptionUtil.handleAsyncException(e);
                }
            });
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_KG_API, id, page, limit,
                            CryptoUtil.md5("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwtappid=1058clienttime=1586163263991" +
                                    "clientver=20000dfid=-global_specialid=" + id + "mid=1586163263991page=" + page + "pagesize=" + limit +
                                    "plat=0specialid=0srcappid=2919uuid=1586163263991version=8000NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt")))
                    .header("mid", "1586163263991")
                    .header("Referer", "https://m3ws.kugou.com/share/index.php")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                    .header("dfid", "-")
                    .header("clienttime", "1586163263991")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getIntValue("total"));
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artist = s[0];
                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
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
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                    .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
            if (JsonUtil.notEmpty(cdlist)) {
                JSONObject data = cdlist.getJSONObject(0);
                total.set(data.getIntValue("songnum"));
                JSONArray songArray = data.getJSONArray("songlist");
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("songmid");
                    String name = songJson.getString("songname");
                    String artist = SdkUtil.parseArtist(songJson);
                    String artistId = SdkUtil.parseArtistId(songJson);
                    String albumName = songJson.getString("albumname");
                    String albumId = songJson.getString("albummid");
                    Double duration = songJson.getDouble("interval");
                    String mvId = songJson.getString("vid");
                    int qualityType = AudioQuality.UNKNOWN;
                    if (songJson.getLong("size5_1") != 0) qualityType = AudioQuality.HR;
                    else if (songJson.getLong("sizeflac") != 0) qualityType = AudioQuality.SQ;
                    else if (songJson.getLong("size320") != 0) qualityType = AudioQuality.HQ;
                    else if (songJson.getLong("size128") != 0) qualityType = AudioQuality.LQ;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.QQ);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
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
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            String playlistInfoBody = SdkCommon.kwRequest(String.format(PLAYLIST_DETAIL_KW_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//            JSONArray songArray = data.getJSONArray("musicList");
//            total.set(data.getIntValue("total"));
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String songId = songJson.getString("rid");
//                String name = songJson.getString("name");
//                String artist = songJson.getString("artist").replace("&", "、");
//                String artistId = songJson.getString("artistid");
//                String albumName = songJson.getString("album");
//                String albumId = songJson.getString("albumid");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KW);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//
//                res.add(musicInfo);
//            }

            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id, page - 1, limit))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(playlistInfoBody);
            JSONArray songArray = data.getJSONArray("musiclist");
            total.set(data.getIntValue("total"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("name");
                String artist = songJson.getString("artist").replace("&", "、");
                String artistId = songJson.getString("artistid");
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumid");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
                String formats = songJson.getString("formats");
                int qualityType = AudioQuality.UNKNOWN;
                if (formats.contains("HIRFLAC")) qualityType = AudioQuality.HR;
                else if (formats.contains("ALFLAC")) qualityType = AudioQuality.SQ;
                else if (formats.contains("MP3H")) qualityType = AudioQuality.HQ;
                else if (formats.contains("MP3128")) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KW);
                musicInfo.setId(songId);
                musicInfo.setName(name);
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

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_MG_API, id, page, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray songArray = playlistInfoJson.getJSONArray("list");
            total.set(playlistInfoJson.getIntValue("totalCount"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("copyrightId");
                String name = songJson.getString("songName");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumId");
                Double duration = DurationUtil.toSeconds(songJson.getString("length"));
                // 咪咕音乐没有 mv 时，该字段不存在！
                String mvId = songJson.getString("mvId");
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray newRateFormats = songJson.getJSONArray("newRateFormats");
                for (int k = newRateFormats.size() - 1; k >= 0; k--) {
                    String formatType = newRateFormats.getJSONObject(k).getString("formatType");
                    if ("ZQ".equals(formatType)) qualityType = AudioQuality.HR;
                    else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                    else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                    else if ("PQ".equals(formatType)) qualityType = AudioQuality.LQ;
                    if (qualityType != AudioQuality.UNKNOWN) break;
                }

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
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

        // 千千
        else if (source == NetMusicSource.QI) {
            String playlistInfoBody = SdkCommon.qiRequest(String.format(PLAYLIST_DETAIL_QI_API, id, page, limit, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getIntValue("trackCount"));
            JSONArray songArray = data.getJSONArray("trackList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
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
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setQualityType(qualityType);

                res.add(musicInfo);
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_FS_API, creatorId, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(playlistInfoBody);
            total.set(Integer.parseInt(RegexUtil.getGroup1("（(\\d+)）", doc.select("span.number").text())));
            Elements songArray = doc.select("li.p_rel");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, songArray.size()); i < len; i++) {
                Element elem = songArray.get(i);
                Elements na = elem.select(".s_title.lt a");
                Elements aa = elem.select(".s_soner.lt a");

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?).html", na.attr("href")).replaceFirst("/", "_");
                String name = na.text();
                String artist = aa.text();
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", aa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                res.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("info");
            JSONArray songArray = data.getJSONArray("sounds");
            total.set(songArray.size());
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("soundstr");
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_BI_API, id, page, limit))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getIntValue("totalSize"));
            JSONArray songArray = data.getJSONArray("data");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("title");
                String artist = songJson.getString("uname");
                String artistId = songJson.getString("uid");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.BI);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, total.get());
    }
}
