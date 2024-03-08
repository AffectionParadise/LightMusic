package net.doge.sdk.entity.artist.menu;

import cn.hutool.http.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.*;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class ArtistMenuReq {
    private static ArtistMenuReq instance;

    private ArtistMenuReq() {
    }

    public static ArtistMenuReq getInstance() {
        if (instance == null) instance = new ArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API
    private final String ARTIST_ALBUMS_API = "https://music.163.com/weapi/artist/albums/%s";
    // 歌手专辑 API (酷狗)
    private final String ARTIST_ALBUMS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/album?singerid=%s&page=%s&pagesize=%s";
    //    private final String ARTIST_ALBUMS_KG_API = "/kmr/v1/author/albums";
    // 歌手专辑 API (酷我)
    private final String ARTIST_ALBUMS_KW_API = "https://kuwo.cn/api/www/artist/artistAlbum?artistid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 歌手专辑 API (咪咕)
    private final String ARTIST_ALBUMS_MG_API = "http://music.migu.cn/v3/music/artist/%s/album?page=%s";
    // 歌手专辑 API (千千)
    private final String ARTIST_ALBUMS_QI_API = "https://music.91q.com/v1/artist/album?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    // 歌手 MV API
    private final String ARTIST_MVS_API = "https://music.163.com/weapi/artist/mvs";
    // 歌手视频 API
//    private final String ARTIST_VIDEOS_API = SdkCommon.PREFIX + "/artist/video?id=%s&cursor=%s&size=%s";
    // 歌手 MV API (酷狗)
//    private final String ARTIST_MVS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/mv?&singerid=%s&page=%s&pagesize=%s";
    private final String ARTIST_MVS_KG_API = "https://openapicdn.kugou.com/kmr/v1/author/videos";
    // 歌手 MV API (QQ)
    private final String ARTIST_MVS_QQ_API = "http://c.y.qq.com/mv/fcgi-bin/fcg_singer_mv.fcg?singermid=%s&order=time&begin=%s&num=%s&cid=205360581";
    // 歌手 MV API (酷我)
    private final String ARTIST_MVS_KW_API = "https://kuwo.cn/api/www/artist/artistMv?artistid=%s&pn=%s&rn=%s&httpsStatus=1";

    // 相似歌手 API
    private final String SIMILAR_ARTIST_API = "https://music.163.com/weapi/discovery/simiArtist";
    // 相似歌手 API (酷狗)(POST)
    private final String SIMILAR_ARTIST_KG_API = "http://kmr.service.kugou.com/v1/author/similar";
    // 相似歌手 API (QQ)
    private final String SIMILAR_ARTIST_QQ_API = "http://c.y.qq.com/v8/fcg-bin/fcg_v8_simsinger.fcg?singer_mid=%s&num=10&utf8=1";

    // 歌手粉丝 API
    private final String ARTIST_FANS_API = "https://music.163.com/weapi/artist/fans/get";
    // 歌手粉丝总数 API
    private final String ARTIST_FANS_TOTAL_API = "https://music.163.com/weapi/artist/follow/count/get";
    // 社团职员 API (猫耳)
    private final String ORGANIZATION_STAFFS_ME_API = "https://www.missevan.com/organization/staff?organization_id=%s&page=%s";
    // 歌手粉丝 API (豆瓣)
    private final String ARTIST_FANS_DB_API = "https://movie.douban.com/celebrity/%s/fans?start=%s";
    // 社团声优 API (猫耳)
    private final String ORGANIZATION_CVS_ME_API = "https://www.missevan.com/organization/cast?organization_id=%s&page=%s";
    // 歌手合作人 API (豆瓣)
    private final String ARTIST_BUDDY_DB_API = "https://movie.douban.com/celebrity/%s/partners?start=%s";

    // 歌手电台 API (豆瓣)
    private final String ARTIST_RADIO_DB_API = "https://movie.douban.com/celebrity/%s/movies?start=%s&format=pic&sortby=time";
    // 社团电台 API (猫耳)
    private final String ORGANIZATION_RADIOS_ME_API = "https://www.missevan.com/organization/drama?organization_id=%s&page=%s";

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    // 获取歌手照片 API (豆瓣)
    private final String GET_ARTISTS_IMG_DB_API = "https://movie.douban.com/celebrity/%s/photos/?type=C&start=%s&sortby=like&size=a&subtype=a";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        int total = 0;
        List<NetAlbumInfo> res = new LinkedList<>();

        String id = artistInfo.getId();
        int source = artistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, String.format(ARTIST_ALBUMS_API, id),
                            String.format("{\"offset\":%s,\"limit\":%s,\"total\":true}", (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            total = albumInfoJson.getJSONObject("artist").getIntValue("albumSize");
            JSONArray albumArray = albumInfoJson.getJSONArray("hotAlbums");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String name = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getIntValue("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_KG_API, id, page, limit))
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("info");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
                String artistId = albumJson.getString("singerid");
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
                Integer songNum = albumJson.getIntValue("songcount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }

            // 部分信息缺失，继续使用旧接口
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(ARTIST_ALBUMS_KG_API);
//            String dat = String.format("{\"author_id\":\"%s\",\"page\":%s,\"pagesize\":%s,\"sort\":3,\"category\":1,\"area_code\":\"all\"}",
//                     id, page, limit);
//            String albumInfoBody = SdkCommon.kgRequest(null, dat, options)
//                    .header("x-router", "openapi.kugou.com")
//                    .header("kg-tid", "36")
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            total = albumInfoJson.getIntValue("total");
//            JSONArray albumArray = albumInfoJson.getJSONArray("data");
//            for (int i = 0, len = albumArray.size(); i < len; i++) {
//                JSONObject albumJson = albumArray.getJSONObject(i);
//
//                String albumId = albumJson.getString("album_id");
//                String albumName = albumJson.getString("album_name");
//                String artist = SdkUtil.parseArtist(albumJson);
//                String artistId = SdkUtil.parseArtistId(albumJson);
//                String coverImgThumbUrl = albumJson.getString("sizable_cover").replace("/{size}", "");
//                String publishTime = albumJson.getString("publish_date");
////                Integer songNum = albumJson.getIntValue("songcount");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.KG);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setArtistId(artistId);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
////                albumInfo.setSongNum(songNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//                res.add(albumInfo);
//            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"ct\":24,\"cv\":0},\"singerAlbum\":{\"method\":\"get_singer_album\",\"param\":" +
                            "{\"singermid\":\"%s\",\"order\":\"time\",\"begin\":%s,\"num\":%s,\"exstatus\":1}," +
                            "\"module\":\"music.web_singer_info_svr\"}}", id, (page - 1) * limit, limit))
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("singerAlbum").getJSONObject("data");
            total = Math.max(total, data.getIntValue("total"));
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("album_mid");
                String albumName = albumJson.getString("album_name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = albumJson.getString("pub_time");
                Integer songNum = albumJson.getJSONObject("latest_song").getIntValue("song_count");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_ALBUMS_KW_API, id, page, limit))
                    .header(Header.REFERER, "https://kuwo.cn/singer_detail/" + StringUtil.urlEncodeAll(id) + "/album")
                    .executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String albumInfoBody = resp.body();
                JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
                JSONObject data = albumInfoJson.getJSONObject("data");
                total = data.getIntValue("total");
                JSONArray albumArray = data.getJSONArray("albumList");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumid");
                    String albumName = StringUtil.removeHTMLLabel(albumJson.getString("album"));
                    String artist = StringUtil.removeHTMLLabel(albumJson.getString("artist")).replace("&", "、");
                    String artistId = albumJson.getString("artistid");
                    String publishTime = albumJson.getString("releaseDate");
                    String coverImgThumbUrl = albumJson.getString("pic");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.KW);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setArtistId(artistId);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(albumInfo);
                }
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_MG_API, id, page))
                    .setFollowRedirects(true)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements pageElem = doc.select(".views-pagination .pagination-item");
            total = !pageElem.isEmpty() ? Integer.parseInt(pageElem.get(pageElem.size() - 1).text()) * limit : limit;
            Elements albumArray = doc.select(".artist-album-list li");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                Element album = albumArray.get(i);
                Elements a = album.select("a.album-name");
                Elements sa = album.select(".album-singers a");
                Elements img = album.select(".thumb-link img");

                String albumId = RegexUtil.getGroup1("album/(\\d+)", a.attr("href"));
                String albumName = a.text();
                StringJoiner sj = new StringJoiner("、");
                sa.forEach(aElem -> sj.add(aElem.text()));
                String artist = sj.toString();
                String artistId = sa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/music/artist/(\\d+)", sa.get(0).attr("href"));
                String coverImgThumbUrl = "https:" + img.attr("data-original");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.MG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = SdkCommon.qiRequest(String.format(ARTIST_ALBUMS_QI_API, id, page, limit, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("result");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String coverImgThumbUrl = albumJson.getString("pic");
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                JSONArray trackList = albumJson.getJSONArray("trackList");
                Integer songNum = JsonUtil.notEmpty(trackList) ? trackList.size() : null;

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        int total = 0;
        List<NetMvInfo> res = new LinkedList<>();

        String id = artistInfo.getId();
        int source = artistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NC) {
            // 歌手 MV
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_MVS_API,
                            String.format("{\"artistId\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}", id, (page - 1) * limit, limit),
                            options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
            total = artistInfo.getMvNum();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = mvJson.getString("artistName");
                String creatorId = mvJson.getJSONObject("artist").getString("id");
                Long playCount = mvJson.getLong("playCount");
                Double duration = mvJson.getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("imgurl");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            // 歌手视频
//            Callable<CommonResult<NetMvInfo>> getArtistVideo = ()->{
//                List<NetMvInfo> res = new LinkedList<>();
//                int t = 0;
//
//                String mvInfoBody = HttpRequest.get(String.format(ARTIST_VIDEOS_API, id, (page - 1) * limit, limit))
//                        .executeAsync()
//                        .body();
//                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("records");
//                t = artistInfo.getMvNum();
//                for (int i = 0, len = mvArray.size(); i < len; i++) {
//                    JSONObject mvJson = mvArray.getJSONObject(i);
//
//                    String mvId = mvJson.getString("id");
//                    String mvName = mvJson.getString("name");
//                    String artistName = mvJson.getString("artistName");
//                    Long playCount = mvJson.getLong("playCount");
//                    Double duration = mvJson.getDouble("duration") / 1000;
//                    String coverImgUrl = mvJson.getString("imgurl");
//
//                    NetMvInfo mvInfo = new NetMvInfo();
//                    mvInfo.setId(mvId);
//                    mvInfo.setName(mvName.trim());
//                    mvInfo.setArtist(artistName);
//                    mvInfo.setCoverImgUrl(coverImgUrl);
//                    mvInfo.setPlayCount(playCount);
//                    mvInfo.setDuration(duration);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                        mvInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(mvInfo);
//                }
//
//                return new CommonResult<>(res, t);
//            };
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_KG_API, id, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//            JSONObject data = mvInfoJson.getJSONObject("data");
//            total = data.getIntValue("total");
//            JSONArray mvArray = data.getJSONArray("info");
//            for (int i = 0, len = mvArray.size(); i < len; i++) {
//                JSONObject mvJson = mvArray.getJSONObject(i);
//
//                String mvId = mvJson.getString("hash");
//                // 酷狗返回的名称含有 HTML 标签，需要去除
//                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("filename"));
//                String artistName = StringUtil.removeHTMLLabel(mvJson.getString("singername"));
//                String coverImgUrl = mvJson.getString("imgurl");
//
//                NetMvInfo mvInfo = new NetMvInfo();
//                mvInfo.setSource(NetMusicSource.KG);
//                mvInfo.setId(mvId);
//                mvInfo.setName(mvName);
//                mvInfo.setArtist(artistName);
//                mvInfo.setCoverImgUrl(coverImgUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                    mvInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(mvInfo);
//            }

            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(ARTIST_MVS_KG_API);
            Map<String, Object> params = new TreeMap<>();
            params.put("author_id", id);
            params.put("is_fanmade", "");
            // 18：官方 20：现场 23：饭制 42419：歌手发布
            params.put("tag_idx", "");
            params.put("page", page);
            params.put("pagesize", limit);
            String mvInfoBody = SdkCommon.kgRequest(params, null, options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            total = mvInfoJson.getIntValue("total");
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("mkv_qhd_hash");
                String mvName = mvJson.getString("video_name");
                String artistName = mvJson.getString("author_name");
                String coverImgUrl = mvJson.getString("hdpic").replace("/{size}", "");
                Double Duration = mvJson.getDouble("timelength") / 1000;
                Long playCount = mvJson.getLong("history_heat");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setDuration(Duration);
                mvInfo.setPlayCount(playCount);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_QQ_API, id, (page - 1) * limit, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title").trim();
                String artistName = mvJson.getString("singer_name");
                String creatorId = mvJson.getString("singer_id");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("listenCount");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_MVS_KW_API, id, page, limit))
                    .header(Header.REFERER, "https://kuwo.cn/singer_detail/" + StringUtil.urlEncodeAll(id) + "/mv")
                    .executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                total = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = mvJson.getString("artist").replace("&", "、");
                    String creatorId = mvJson.getString("artistid");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取歌手照片链接
     */
    public CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        int source = artistInfo.getSource();
        String id = artistInfo.getId();
        List<String> res = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB) {
            String imgInfoBody = HttpRequest.get(String.format(GET_ARTISTS_IMG_DB_API, id, (page - 1) * limit))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix .cover img");
            String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
            total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                res.add(url);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_ARTIST_API, String.format("{\"artistid\":\"%s\"}", id), options)
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("artists");
            if (JsonUtil.notEmpty(artistArray)) {
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("musicSize");
                    Integer albumNum = artistJson.getIntValue("albumSize");
//                Integer mvNum = artistJson.getIntValue("mvSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(SIMILAR_ARTIST_KG_API);
            String ct = String.valueOf(System.currentTimeMillis() / 1000);
            String dat = String.format("{\"clientver\":\"%s\",\"mid\":\"%s\",\"clienttime\":\"%s\",\"key\":\"%s\"," +
                            "\"appid\":\"%s\",\"data\":[{\"author_id\":\"%s\"}]}",
                    KugouReqBuilder.clientver, KugouReqBuilder.mid, ct, KugouReqBuilder.signParamsKey(ct), KugouReqBuilder.appid, id);
            String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("data").getJSONArray(0);
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("author_id");
                String artistName = artistJson.getString("author_name");
                String coverImgThumbUrl = artistJson.getString("sizable_avatar").replace("{size}", "240");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_QQ_API, id))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONObject("singers").getJSONArray("items");
            if (JsonUtil.notEmpty(artistArray)) {
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("mid");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QQ);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        String id = artistInfo.getId();

        List<NetUserInfo> res = new LinkedList<>();
        AtomicReference<Integer> t = new AtomicReference<>(0);

        // 网易云
        if (source == NetMusicSource.NC) {
            Runnable getFans = () -> {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String userInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_FANS_API,
                                String.format("{\"id\":\"%s\",\"offset\":%s,\"limit\":%s}", id, (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
                JSONArray userArray = userInfoJson.getJSONArray("data");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i).getJSONObject("userProfile");

                    String userId = userJson.getString("userId");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getIntValue("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                    String avatarThumbUrl = userJson.getString("avatarUrl");
//                    Integer follow = userJson.getIntValue("follows");
//                    Integer fan = userJson.getIntValue("followeds");
//                    Integer playlistCount = userJson.getIntValue("playlistCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                    userInfo.setFollow(follow);
//                    userInfo.setFan(fan);
//                    userInfo.setPlaylistCount(playlistCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            };

            Runnable getFansCnt = () -> {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String tBody = SdkCommon.ncRequest(Method.POST, ARTIST_FANS_TOTAL_API, String.format("{\"id\":\"%s\"}", id), options)
                        .executeAsync()
                        .body();
                t.set(JSONObject.parseObject(tBody).getJSONObject("data").getIntValue("fansCnt"));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getFans));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFansCnt));

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

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(ORGANIZATION_STAFFS_ME_API, id, page))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("staff");
            t.set(data.getJSONObject("pagination").getIntValue("count"));
            for (int i = (page - 1) * limit, len = Math.min(page * limit, userArray.size()); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("name");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 35;
            String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_DB_API, id, (page - 1) * rn))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            String ts = RegexUtil.getGroup1("（(\\d+)）", doc.select("#content > h1").text());
            int tn = Integer.parseInt(ts);
            t.set(tn -= tn / rn * 15);
            Elements us = doc.select("dl.obu");
            for (int i = 0, len = us.size(); i < len; i++) {
                Element user = us.get(i);
                Elements a = user.select("dd a");
                Elements img = user.select("img");

                String userId = RegexUtil.getGroup1("/people/(.*?)/", a.attr("href"));
                String userName = a.text();
                String gender = "保密";
                String src = img.attr("src");
                String avatarThumbUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst("/u", "/ul");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DB);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t.get());
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page, int limit) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;
        final int dbLimit = 10;

        // 猫耳
        if (source == NetMusicSource.ME) {
            String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_CVS_ME_API, id, page))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject info = artistInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getIntValue("count");
            JSONArray artistArray = info.getJSONArray("cast");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, artistArray.size()); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("avatar");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_BUDDY_DB_API, id, (page - 1) * dbLimit))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select(".partners.item");
            String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
            t = StringUtil.isEmpty(ts) ? cs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select(".info a").first();
                Element img = artist.select(".pic img").first();

                String artistId = RegexUtil.getGroup1("celebrity/(\\d+)/", a.attr("href"));
                String artistName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        int source = artistInfo.getSource();
        String id = artistInfo.getId();

        List<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;
        final int dbLimit = 10;

        // 猫耳
        if (source == NetMusicSource.ME) {
            if (artistInfo.isOrganization()) {
                String radioInfoBody = HttpRequest.get(String.format(ORGANIZATION_RADIOS_ME_API, id, page))
                        .executeAsync()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                t = data.getJSONObject("pagination").getIntValue("count");
                JSONArray radioArray = data.getJSONArray("drama");
                for (int i = (page - 1) * limit, len = Math.min(page * limit, radioArray.size()); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = artistInfo.getName();
                    String coverImgThumbUrl = "https:" + radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            } else {
                String radioInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, page, limit))
                        .executeAsync()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("dramas");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getIntValue("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i).getJSONObject("drama");

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String category = radioJson.getString("catalog_name");
                    String dj = artistInfo.getName();
                    Long playCount = radioJson.getLong("view_count");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_RADIO_DB_API, id, (page - 1) * dbLimit))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements rs = doc.select(".grid_view > ul > li > dl");
            String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("h6 a").first();
                Element span = radio.select("h6 span").first();
                Element img = radio.select("img").first();
                Elements dl = radio.select("dl > dd > dl");

                String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                String radioName = a.text();
                String dj = dl.text().trim();
                String coverImgThumbUrl = img.attr("src");
                String category = RegexUtil.getGroup1("(\\d+)", span.text());

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
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
