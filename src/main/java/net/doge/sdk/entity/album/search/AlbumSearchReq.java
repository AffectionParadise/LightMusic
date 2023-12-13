package net.doge.sdk.entity.album.search;

import cn.hutool.http.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetAlbumInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AlbumSearchReq {
    private static AlbumSearchReq instance;

    private AlbumSearchReq() {
    }

    public static AlbumSearchReq getInstance() {
        if (instance == null) instance = new AlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    // 关键词搜索专辑 API (酷狗)
//    private final String SEARCH_ALBUM_KG_API = "http://msearch.kugou.com/api/v3/search/album?keyword=%s&page=%s&pagesize=%s";
    private final String SEARCH_ALBUM_KG_API = "/v1/search/album";
    // 关键词搜索专辑 API (酷我)
    private final String SEARCH_ALBUM_KW_API = "http://www.kuwo.cn/api/www/search/searchAlbumBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索专辑 API (咪咕)
    private final String SEARCH_ALBUM_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=4&keyword=%s&pgc=%s&rows=%s";
    // 关键词搜索专辑 API (千千)
    private final String SEARCH_ALBUM_QI_API = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=3&word=%s";
    // 关键词搜索专辑 API (豆瓣)
    private final String SEARCH_ALBUM_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1003";
    // 关键词搜索专辑 API (堆糖)
    private final String SEARCH_ALBUM_DT_API
            = "https://www.duitang.com/napi/album/list/by_search/?include_fields=is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover" +
            "&kw=%s&start=%s&limit=%s&type=album&_type=&_=%s";
    // 关键词搜索专辑 API 2 (堆糖)
    private final String SEARCH_ALBUM_DT_API_2
            = "https://www.duitang.com/napi/blogv2/list/by_search/?include_fields=is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover" +
            "&kw=%s&start=%s&limit=%s&type=feed&_type=&_=%s";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(int src, String keyword, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetAlbumInfo> res = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncodeAll(keyword);

        // 网易云
        Callable<CommonResult<NetAlbumInfo>> searchAlbums = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String albumInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":10,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject result = albumInfoJson.getJSONObject("result");
            JSONArray albumArray = result.getJSONArray("albums");
            if (JsonUtil.notEmpty(albumArray)) {
                t = result.getIntValue("albumCount");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = SdkUtil.parseArtist(albumJson);
                    String artistId = SdkUtil.parseArtistId(albumJson);
                    String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                    Integer songNum = albumJson.getIntValue("size");
                    String coverImgThumbUrl = albumJson.getString("picUrl");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
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
                    r.add(albumInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsKg = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

//            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray albumArray = data.getJSONArray("info");
//            for (int i = 0, len = albumArray.size(); i < len; i++) {
//                JSONObject albumJson = albumArray.getJSONObject(i);
//
//                String albumId = albumJson.getString("albumid");
//                String albumName = albumJson.getString("albumname");
//                String artist = albumJson.getString("singername");
//                String artistId = albumJson.getString("singerid");
//                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
//                Integer songNum = albumJson.getIntValue("songcount");
//                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.KG);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setArtistId(artistId);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
//                albumInfo.setSongNum(songNum);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//                r.add(albumInfo);
//            }

            Map<String, Object> params = new TreeMap<>();
            params.put("platform", "AndroidFilter");
            params.put("keyword", keyword);
            params.put("page", page);
            params.put("pagesize", limit);
            params.put("category", 1);
            Map<KugouReqOptEnum, String> options = KugouReqOptsBuilder.androidGet(SEARCH_ALBUM_KG_API);
            String albumInfoBody = SdkCommon.kgRequest(params, null, options)
                    .header("x-router", "complexsearch.kugou.com")
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("lists");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = albumJson.getString("publish_time");
                Integer songNum = albumJson.getIntValue("songcount");
                String coverImgThumbUrl = albumJson.getString("img");

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
                r.add(albumInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsQq = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 2))
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray albumArray = data.getJSONObject("body").getJSONObject("album").getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumMID");
                String albumName = albumJson.getString("albumName");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = albumJson.getString("publicTime");
                Integer songNum = albumJson.getIntValue("song_count");
                String coverImgThumbUrl = albumJson.getString("albumPic").replaceFirst("http:", "https:");

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
                r.add(albumInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsKw = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_ALBUM_KW_API, encodedKeyword, page, limit)).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String albumInfoBody = resp.body();
                JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
                JSONObject data = albumInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray albumArray = data.getJSONArray("albumList");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumid");
                    String albumName = StringUtil.removeHTMLLabel(albumJson.getString("album"));
                    String artist = albumJson.getString("artist").replace("&", "、");
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
                    r.add(albumInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咪咕
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsMg = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_MG_API, encodedKeyword, page, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            t = albumInfoJson.getIntValue("pgt");
            JSONArray albumArray = albumInfoJson.getJSONArray("albums");
            if (JsonUtil.notEmpty(albumArray)) {
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("title");
                    String artist = SdkUtil.parseArtist(albumJson);
                    String artistId = SdkUtil.parseArtistId(albumJson);
                    String publishTime = albumJson.getString("publishDate");
                    Integer songNum = albumJson.getIntValue("songNum");
                    String coverImgThumbUrl = albumJson.getString("albumPicM");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.MG);
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
                    r.add(albumInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
//        Callable<CommonResult<NetAlbumInfo>> searchAlbumsMg = () -> {
//            List<NetAlbumInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String albumInfoBody = SdkCommon.mgSearchRequest("album", keyword, page, limit)
//                    .executeAsync()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("albumResultData");
//            t = data.getIntValue("totalCount");
//            JSONArray albumArray = data.getJSONArray("result");
//            if (JsonUtil.notEmpty(albumArray)) {
//                for (int i = 0, len = albumArray.size(); i < len; i++) {
//                    JSONObject albumJson = albumArray.getJSONObject(i);
//
//                    String albumId = albumJson.getString("id");
//                    String albumName = albumJson.getString("name");
//                    String artist = albumJson.getString("singer");
//                    String publishTime = albumJson.getString("publishDate");
//                    String coverImgThumbUrl = albumJson.getJSONArray("imgItems").getJSONObject(0).getString("img");
//
//                    NetAlbumInfo albumInfo = new NetAlbumInfo();
//                    albumInfo.setSource(NetMusicSource.MG);
//                    albumInfo.setId(albumId);
//                    albumInfo.setName(albumName);
//                    albumInfo.setArtist(artist);
//                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    albumInfo.setPublishTime(publishTime);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        albumInfo.setCoverImgThumb(coverImgThumb);
//                    });
//                    r.add(albumInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//        };

        // 千千
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsQi = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.qiRequest(String.format(SEARCH_ALBUM_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword)).executeAsync();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("typeAlbum");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String rd = albumJson.getString("releaseDate");
                String publishTime = StringUtil.notEmpty(rd) ? rd.split("T")[0] : "";
                String coverImgThumbUrl = albumJson.getString("pic");
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
                r.add(albumInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 豆瓣
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsDb = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_DB_API, encodedKeyword, (page - 1) * lim))
                    .executeAsync()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONArray("items");
            if (JsonUtil.notEmpty(albumArray)) {
                int to = albumInfoJson.getIntValue("total");
                t = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    Document doc = Jsoup.parse(albumArray.getString(i));
                    Elements result = doc.select(".result");
                    Elements a = result.select("h3 a");

                    String albumId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                    String albumName = a.text().trim();
                    String artist = result.select("span.subject-cast").text();
                    String coverImgThumbUrl = result.select(".pic img").attr("src");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.DB);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(albumInfo);
                }
            }
//            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_DB_API, encodedKeyword, (page - 1) * lim))
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(albumInfoBody);
//            t = 4000 / lim * limit;
//            Elements result = doc.select(".sc-bZQynM.hrvolz.sc-bxivhb.hvEfwz");
//            for (int i = 0, len = result.size(); i < len; i++) {
//                Element album = result.get(i);
//                Element a = album.select(".title a").first();
//                Element img = album.select(".item-root img").first();
//
//                String albumId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
//                String albumName = a.text();
//                String artist = album.select(".meta.abstract").text();
//                String coverImgThumbUrl = img.attr("src");
//
//                NetAlbumInfo albumInfo = new NetAlbumInfo();
//                albumInfo.setSource(NetMusicSource.DB);
//                albumInfo.setId(albumId);
//                albumInfo.setName(albumName);
//                albumInfo.setArtist(artist);
//                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    albumInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(albumInfo);
//            }
            return new CommonResult<>(r, t);
        };

        // 堆糖
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsDt = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_DT_API, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis())).executeAsync();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("object_list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);
                JSONObject user = albumJson.getJSONObject("user");

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = user.getString("username");
                String artistId = user.getString("id");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
                Integer songNum = albumJson.getIntValue("count");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
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
                r.add(albumInfo);
            }
            return new CommonResult<>(r, t);
        };
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsDt2 = () -> {
            List<NetAlbumInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_DT_API_2, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis())).executeAsync();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("object_list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject mainJson = albumArray.getJSONObject(i);
                JSONObject albumJson = mainJson.getJSONObject("album");
                JSONObject sender = mainJson.getJSONObject("sender");

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = sender.getString("username");
                String artistId = sender.getString("id");
                String publishTime = TimeUtil.msToDate(mainJson.getLong("add_datetime_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
                Integer songNum = albumJson.getIntValue("count");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
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
                r.add(albumInfo);
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetAlbumInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbums));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsKw));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsMg));
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsQi));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsDb));
        if (src == NetMusicSource.DT || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsDt));
            taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsDt2));
        }

        List<List<NetAlbumInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetAlbumInfo> result = task.get();
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
