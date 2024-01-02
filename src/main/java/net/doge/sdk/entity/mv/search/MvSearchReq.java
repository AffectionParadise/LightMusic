package net.doge.sdk.entity.mv.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MvSearchReq {
    private static MvSearchReq instance;

    private MvSearchReq() {
    }

    public static MvSearchReq getInstance() {
        if (instance == null) instance = new MvSearchReq();
        return instance;
    }

    // 关键词搜索 MV / 视频 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    // 关键词搜索 MV API (酷狗)
    private final String SEARCH_MV_KG_API = "http://msearch.kugou.com/api/v3/search/mv?version=9108&keyword=%s&page=%s&pagesize=%s&sver=2";
    //    private final String SEARCH_MV_KG_API = "/v1/search/mv";
    // 关键词搜索 MV API (酷我)
    private final String SEARCH_MV_KW_API = "http://www.kuwo.cn/api/www/search/searchMvBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索 MV API (好看)
    private final String SEARCH_MV_HK_API = "https://haokan.baidu.com/haokan/ui-search/pc/search/video?query=%s&pn=%s&rn=%s&type=video";
    // 关键词搜索 MV API (哔哩哔哩)
    private final String SEARCH_MV_BI_API = "https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=%s&page=%s";
    // 关键词搜索 MV API (音悦台)
    private final String SEARCH_MV_YY_API = "https://search-api.yinyuetai.com/search/get_search_result.json";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(int src, String keyword, int page, int limit, String cursor) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> res = new LinkedList<>();
        AtomicReference<String> atomicCursor = new AtomicReference<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncodeAll(keyword);

        // 网易云
        // MV
        Callable<CommonResult<NetMvInfo>> searchMvs = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1004,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("mvCount");
                JSONArray mvArray = result.getJSONArray("mvs");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getDouble("duration") / 1000;
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 视频
        Callable<CommonResult<NetMvInfo>> searchVideos = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1014,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("videoCount");
                JSONArray mvArray = result.getJSONArray("videos");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    Integer type = mvJson.getIntValue("type");
                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String creator = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playTime");
                    Double duration = mvJson.getDouble("durationms") / 1000;
                    String coverImgUrl = mvJson.getString("coverUrl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    // 网易云视频和 MV 分开了
                    mvInfo.setType(type == 1 ? MvInfoType.VIDEO : MvInfoType.MV);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(creator);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        Callable<CommonResult<NetMvInfo>> searchMvsKg = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_KG_API, encodedKeyword, page, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("hash");
                // 酷狗返回的名称含有 HTML 标签，需要去除
                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("filename"));
                String artistName = StringUtil.removeHTMLLabel(mvJson.getString("singername"));
                String creatorId = mvJson.getString("userid");
                Long playCount = mvJson.getLong("historyheat");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publishdate").split(" ")[0];
                String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                // 酷狗搜索 MV 只给了用户 id，默认全是视频
                mvInfo.setType(MvInfoType.VIDEO);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }

            // 新接口部分参数获取不到，暂时使用旧接口
//            Map<String, Object> params = new TreeMap<>();
//            params.put("platform", "AndroidFilter");
//            params.put("keyword", keyword);
//            params.put("page", page);
//            params.put("pagesize", limit);
//            params.put("category", 1);
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_MV_KG_API);
//            String mvInfoBody = SdkCommon.kgRequest(params, null, options)
//                    .header("x-router", "complexsearch.kugou.com")
//                    .executeAsync()
//                    .body();
//            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//            JSONObject data = mvInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray mvArray = data.getJSONArray("lists");
//            for (int i = 0, len = mvArray.size(); i < len; i++) {
//                JSONObject mvJson = mvArray.getJSONObject(i);
//
//                String mvId = mvJson.getString("MvHash");
//                String mvName = mvJson.getString("MvName");
//                String artistName = SdkUtil.parseArtist(mvJson);
//                String creatorId = SdkUtil.parseArtistId(mvJson);
//                Long playCount = mvJson.getLong("HistoryHeat");
//                Double duration = mvJson.getDouble("Duration");
//                String pubTime = mvJson.getString("PublishDate").split(" ")[0];
//                String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");
//
//                NetMvInfo mvInfo = new NetMvInfo();
//                mvInfo.setSource(NetMusicSource.KG);
//                mvInfo.setId(mvId);
//                mvInfo.setName(mvName);
//                mvInfo.setArtist(artistName);
//                mvInfo.setCreatorId(creatorId);
//                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
//                mvInfo.setPubTime(pubTime);
//                mvInfo.setCoverImgUrl(coverImgUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                    mvInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(mvInfo);
//            }
            return new CommonResult<>(r, t);
        };

        // QQ
        Callable<CommonResult<NetMvInfo>> searchMvsQq = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 4))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray mvArray = data.getJSONObject("body").getJSONObject("mv").getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("v_id");
                String mvName = mvJson.getString("mv_name").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("play_count");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publish_date");
                String coverImgUrl = mvJson.getString("mv_pic_url").replaceFirst("http:", "https:");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        Callable<CommonResult<NetMvInfo>> searchMvsKw = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_MV_KW_API, encodedKeyword, page, limit)).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = mvJson.getString("artist").replace("&", "、");
                    String creatorId = mvJson.getString("artistid");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");
                    String coverImgUrl = mvJson.getString("pic");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 好看
        Callable<CommonResult<NetMvInfo>> searchMvsHk = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_HK_API, encodedKeyword, page, limit))
                    .cookie(SdkCommon.HK_COOKIE)
                    .executeAsync();
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = page * limit;
            if (data.getIntValue("has_more") == 1) t++;
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("author_id");
                Long playCount = StringUtil.parseNumber(mvJson.getString("read_num").replaceFirst("次播放", ""));
                Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publishTimeText");
                String coverImgUrl = mvJson.getString("cover_src");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 哔哩哔哩
        Callable<CommonResult<NetMvInfo>> searchMvsBi = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_BI_API, encodedKeyword, page))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync();
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("numResults");
            JSONArray mvArray = data.getJSONArray("result");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String bvId = mvJson.getString("bvid");
                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("title"));
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("mid");
                Long playCount = mvJson.getLong("play");
                Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);
                String coverImgUrl = "https:" + mvJson.getString("pic");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
                mvInfo.setBvid(bvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 音悦台
        Callable<CommonResult<NetMvInfo>> searchMvsYy = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.post(SEARCH_MV_YY_API)
                    .body(String.format("{\"searchType\":\"MV\",\"key\":\"%s\",\"sinceId\":\"%s\",\"size\":%s," +
                                    "\"requestTagRows\":[{\"key\":\"sortType\",\"chosenTags\":[\"COMPREHENSIVE\"]}," +
                                    "{\"key\":\"source\",\"chosenTags\":[\"-1\"]},{\"key\":\"duration\",\"chosenTags\":[\"-1\"]}]}",
                            keyword, cursor, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = page * limit + 1;
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject fullClip = mvJson.getJSONObject("fullClip");

                String mvId = mvJson.getString("id");
                if (i == len - 1) atomicCursor.set(mvId);
                String mvName = mvJson.getString("title");
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("playNum");
                Double duration = fullClip.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("publishDate") * 1000);
                String coverImgUrl = mvJson.getString("headImg");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.YY);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvs));
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchVideos));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKw));
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsHk));
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsBi));
        if (src == NetMusicSource.YY || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsYy));

        List<List<NetMvInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMvInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get(), atomicCursor.get());
    }
}
