package net.doge.sdk.service.playlist.search;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Header;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.collection.ListUtil;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaylistSearchReq {
    private static PlaylistSearchReq instance;

    private PlaylistSearchReq() {
    }

    public static PlaylistSearchReq getInstance() {
        if (instance == null) instance = new PlaylistSearchReq();
        return instance;
    }

    // 关键词搜索歌单 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    // 关键词搜索歌单 API (酷狗)
//    private final String SEARCH_PLAYLIST_KG_API = "http://mobilecdnbj.kugou.com/api/v3/search/special?filter=0&keyword=%s&page=%s&pagesize=%s";
    private final String SEARCH_PLAYLIST_KG_API = "/v1/search/special";
    // 关键词搜索歌单 API (酷我)
//    private final String SEARCH_PLAYLIST_KW_API = "https://kuwo.cn/api/www/search/searchPlayListBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String SEARCH_PLAYLIST_KW_API = "http://search.kuwo.cn/r.s?all=%s&pn=%s&rn=%s&rformat=json&encoding=utf8&ver=mbox&vipver=MUSIC_8.7.7.0_BCS37&plat=pc&devid=28156413&ft=playlist&pay=0&needliveshow=0";
    // 关键词搜索歌单 API (咪咕)
    private final String SEARCH_PLAYLIST_MG_API = "https://m.music.migu.cn/migu/remoting/scr_search_tag?type=6&keyword=%s&pgc=%s&rows=%s";
    // 关键词搜索歌单 API (5sing)
    private final String SEARCH_PLAYLIST_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=0&type=1";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(int src, String keyword, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> res = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);

        // 网易云
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1000,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject result = playlistInfoJson.getJSONObject("result");
            if (result.containsKey("playlists")) {
                t = result.getIntValue("playlistCount");
                JSONArray playlistArray = result.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.getJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                    String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getIntValue("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

//            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KG_API, encodedKeyword, page, limit))
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray playlistArray = data.getJSONArray("info");
//            for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                String playlistId = playlistJson.getString("specialid");
//                String playlistName = playlistJson.getString("specialname");
//                String creator = playlistJson.getString("nickname");
//                Long playCount = playlistJson.getLong("playcount");
//                Integer trackCount = playlistJson.getIntValue("songcount");
//                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.KG);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(playlistName);
//                playlistInfo.setCreator(creator);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//                r.add(playlistInfo);
//            }

            Map<String, Object> params = new TreeMap<>();
            params.put("platform", "AndroidFilter");
            params.put("keyword", keyword);
            params.put("page", page);
            params.put("pagesize", limit);
            params.put("category", 1);
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_PLAYLIST_KG_API);
            String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
                    .header("x-router", "complexsearch.kugou.com")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray playlistArray = data.getJSONArray("lists");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("total_play_count");
                Integer trackCount = playlistJson.getIntValue("song_count");
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
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
                r.add(playlistInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsQq = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 3))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray playlistArray = data.getJSONObject("body").getJSONObject("songlist").getJSONArray("list");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("dissid");
                String playlistName = playlistJson.getString("dissname");
                String creator = playlistJson.getJSONObject("creator").getString("name");
                Long playCount = playlistJson.getLong("listennum");
                Integer trackCount = playlistJson.getIntValue("song_count");
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
                r.add(playlistInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKw = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

//            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page, limit)).executeAsync();
//            if (resp.isSuccessful()) {
//                String playlistInfoBody = resp.body();
//                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//                JSONObject data = playlistInfoJson.getJSONObject("data");
//                t = data.getIntValue("total");
//                JSONArray playlistArray = data.getJSONArray("list");
//                for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("id");
//                    String playlistName = StringUtil.removeHTMLLabel(playlistJson.getString("name"));
//                    String creator = playlistJson.getString("uname");
//                    Long playCount = playlistJson.getLong("listencnt");
//                    Integer trackCount = playlistJson.getIntValue("total");
//                    String coverImgThumbUrl = playlistJson.getString("img");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.KW);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//                    r.add(playlistInfo);
//                }
//            }
            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page - 1, limit))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(playlistInfoBody);
            t = data.getIntValue("TOTAL");
            JSONArray playlistArray = data.getJSONArray("abslist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playlistid");
                String playlistName = HtmlUtil.removeHtmlLabel(playlistJson.getString("name"));
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcnt");
                Integer trackCount = playlistJson.getIntValue("songnum");
                String coverImgThumbUrl = playlistJson.getString("pic");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KW);
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
                r.add(playlistInfo);
            }

            return new CommonResult<>(r, t);
        };

        // 咪咕
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsMg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_MG_API, encodedKeyword, page, limit))
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = playlistInfoJson.getIntValue("pgt");
            JSONArray playlistArray = playlistInfoJson.getJSONArray("songLists");
            if (JsonUtil.notEmpty(playlistArray)) {
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("userName");
                    String creatorId = playlistJson.getString("userId");
                    Long playCount = playlistJson.getLong("playNum");
                    Integer trackCount = playlistJson.getIntValue("musicNum");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
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
                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
//        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsMg = () -> {
//            List<NetPlaylistInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String playlistInfoBody = SdkCommon.mgSearchRequest("playlist", keyword, page, limit)
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("songListResultData");
//            t = data.getIntValue("totalCount");
//            JSONArray playlistArray = data.getJSONArray("result");
//            if (JsonUtil.notEmpty(playlistArray)) {
//                for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("id");
//                    String playlistName = playlistJson.getString("name");
//                    String creator = playlistJson.getString("userName");
//                    String creatorId = playlistJson.getString("userId");
//                    Long playCount = playlistJson.getLong("playNum");
//                    Integer trackCount = playlistJson.getIntValue("musicNum");
//                    String coverImgThumbUrl = playlistJson.getString("musicListPicUrl");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.MG);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCreatorId(creatorId);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//                    r.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//        };

        // 5sing
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsFs = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_FS_API, encodedKeyword, page))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(playlistInfoBody);
            t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
            JSONArray playlistArray = data.getJSONArray("list");
            if (JsonUtil.notEmpty(playlistArray)) {
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("songListId");
                    String playlistName = HtmlUtil.removeHtmlLabel(playlistJson.getString("title"));
                    String creator = playlistJson.getString("userName");
                    String creatorId = playlistJson.getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getIntValue("songCnt");
                    String coverImgThumbUrl = playlistJson.getString("pictureUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.FS);
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
                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylists));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKw));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsMg));
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsFs));

        List<List<NetPlaylistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetPlaylistInfo> result = task.get();
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
