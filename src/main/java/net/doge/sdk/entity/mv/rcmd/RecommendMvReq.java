package net.doge.sdk.entity.mv.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RecommendMvReq {
    private static RecommendMvReq instance;

    private RecommendMvReq() {
    }

    public static RecommendMvReq getInstance() {
        if (instance == null) instance = new RecommendMvReq();
        return instance;
    }

    // MV 排行 API
    private final String TOP_MV_API = "https://music.163.com/weapi/mv/toplist";
    // 最新 MV API
    private final String NEW_MV_API = "https://interface.music.163.com/weapi/mv/first";
    // 全部 MV API
    private final String ALL_MV_API = "https://interface.music.163.com/api/mv/all";
    // 推荐 MV API
    private final String RECOMMEND_MV_API = "https://music.163.com/weapi/personalized/mv";
    // 网易出品 MV API
    private final String EXCLUSIVE_MV_API = "https://interface.music.163.com/api/mv/exclusive/rcmd";
    // 推荐 MV API (酷狗)
    private final String RECOMMEND_MV_KG_API = "http://mobilecdnbj.kugou.com/api/v5/video/list?sort=4&id=%s&page=%s&pagesize=%s";
    // 编辑精选 MV API (酷狗)
    private final String IP_MV_KG_API = "/openapi/v1/ip/videos";
    // 最新 MV API (QQ)
    private final String NEW_MV_QQ_API = "https://c.y.qq.com/mv/fcgi-bin/getmv_by_tag?cmd=shoubo&format=json&lan=%s";
    // 推荐 MV API (酷我)
    private final String RECOMMEND_MV_KW_API = "http://www.kuwo.cn/api/www/music/mvList?pid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 推荐 MV API (千千)
    private final String RECOMMEND_MV_QI_API = "https://music.91q.com/v1/video/list?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s";
    // 推荐 MV API (5sing)
    private final String RECOMMEND_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=3&sortType=2&pageIndex=%s&pageSize=%s";
    // 最热 MV API (5sing)
    private final String HOT_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=2&sortType=2&pageIndex=%s&pageSize=%s";
    // 最新 MV API (5sing)
    private final String NEW_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=2&sortType=1&pageIndex=%s&pageSize=%s";
    // 猜你喜欢视频 API (好看)
    private final String GUESS_VIDEO_HK_API = "https://haokan.baidu.com/videoui/api/Getvideolandfeed?time=%s";
    // 榜单视频 API (好看)
    private final String TOP_VIDEO_HK_API
            = "https://haokan.baidu.com/videoui/page/pc/toplist?type=hotvideo&sfrom=haokan_web_banner&page=%s&pageSize=%s&_format=json";
    // 推荐视频 API (好看)
    private final String RECOMMEND_VIDEO_HK_API = "https://haokan.baidu.com/web/video/feed?tab=%s&act=pcFeed&pd=pc&num=%s&shuaxin_id=1661766211525";
    // 热门视频 API (哔哩哔哩)
    private final String HOT_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/popular?pn=%s&ps=%s";
    // 分区排行榜视频 API (哔哩哔哩)
    private final String CAT_RANK_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/ranking/region?rid=%s";
    // 分区最新视频 API (哔哩哔哩)
    private final String CAT_NEW_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/dynamic/region?rid=%s&pn=%s&ps=%s";
    // 视频 API (发姐)
    private final String VIDEO_FA_API = "https://www.chatcyf.com/video/page/%s/?c2=%s&c3&c4&t";
    // 直播 API (发姐)
    private final String LIVE_FA_API = "https://www.chatcyf.com/teaparty/page/%s/?c2=%s&c3&c4&t";
    // 视频 API (李志)
    private final String VIDEO_LZ_API = "https://www.lizhinb.com/live-category/%s/";

    /**
     * 获取 MV 排行 + 最新 MV + 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMvs(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> res = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.mvTag.get(tag);

        // 网易云(程序分页)
        // MV 排行
        Callable<CommonResult<NetMvInfo>> getMvRanking = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String mvInfoBody = SdkCommon.ncRequest(Method.POST, TOP_MV_API, String.format("{\"area\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}",
                                s[0].replace("全部", ""), (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    JSONObject mv = mvJson.getJSONObject("mv");

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mv.getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                    String pubTime = mv.getString("publishTime");
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
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
            }
            return new CommonResult<>(r, t);
        };
        // 最新 MV
        Callable<CommonResult<NetMvInfo>> getNewMv = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String mvInfoBody = SdkCommon.ncRequest(Method.POST, NEW_MV_API, String.format("{\"area\":\"%s\",\"limit\":100,\"total\":true}", s[0]), options)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playCount");
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
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
        // 全部 MV
        Callable<CommonResult<NetMvInfo>> getAllMv = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0]) || StringUtil.notEmpty(s[1])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String mvInfoBody = SdkCommon.ncRequest(Method.POST, ALL_MV_API,
                                String.format("{\"tags\":\"{'area':'%s','type':'%s','order':'上升最快'}\",\"offset\":%s,\"limit\":%s,\"total\":true}",
                                        s[0], s[1], (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
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
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMv = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_MV_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("result");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("playCount");
                Double duration = mvJson.getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("picUrl");

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
            return new CommonResult<>(r, t);
        };
        // 网易出品 MV
        Callable<CommonResult<NetMvInfo>> getExclusiveMv = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, EXCLUSIVE_MV_API,
                            String.format("{\"offset\":%s,\"limit\":%s}", (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("playCount");
//                Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("cover");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗(接口分页)
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvKg = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_KG_API, s[2], page, limit))
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("info");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("mvhash");
                    String mvName = mvJson.getString("videoname");
                    String artistName = mvJson.getString("singername");
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playcount");
                    Double duration = mvJson.getDouble("duration") / 1000;
                    String pubTime = mvJson.getString("publish").split(" ")[0];
                    String coverImgUrl = mvJson.getString("img").replace("/{size}", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KG);
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
            }
            return new CommonResult<>(r, t);
        };
        // 编辑精选 MV
        Callable<CommonResult<NetMvInfo>> getIpMvKg = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_MV_KG_API);
                String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", s[3], page, limit);
                String mvInfoBody = SdkCommon.kgRequest(null, dat, options)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                t = mvInfoJson.getIntValue("total");
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    JSONObject base = mvJson.getJSONObject("base");
                    JSONObject h264 = mvJson.getJSONObject("h264");
                    JSONObject extra = mvJson.getJSONObject("extra");

                    String mvId = h264.getString("sd_hash");
                    String mvName = base.getString("mv_name");
                    String artistName = base.getString("singer");
                    Long playCount = extra.getLong("hit");
                    Double duration = base.getDouble("duration") / 1000;
                    String pubTime = base.getString("publish_time").split(" ")[0];
                    String coverImgUrl = base.getString("hdpic").replace("/{size}", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KG);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
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
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        // 推荐 MV (接口分页)
        Callable<CommonResult<NetMvInfo>> getRecommendMvQq = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                        .body(String.format("{\"comm\":{\"ct\":24},\"mv_list\":{\"module\":\"MvService.MvInfoProServer\"," +
                                "\"method\":\"GetAllocMvInfo\",\"param\":{\"area_id\":%s,\"version_id\":%s,\"start\":%s,\"size\":%s," +
                                "\"order\":1}}}", s[4], s[5], (page - 1) * limit, limit))
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("mv_list").getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("list");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title").trim();
                    String artistName = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = mvJson.getDouble("duration");
                    String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);
                    String coverImgUrl = mvJson.getString("picurl");

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
            }
            return new CommonResult<>(r, t);
        };
        // 最新 MV (程序分页)
        Callable<CommonResult<NetMvInfo>> getNewMvQq = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String mvInfoBody = HttpRequest.get(String.format(NEW_MV_QQ_API, s[6]))
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("mvlist");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("mvtitle").trim();
                    String artistName = SdkUtil.parseArtist(mvJson);
                    String creatorId = SdkUtil.parseArtistId(mvJson);
                    Long playCount = mvJson.getLong("listennum");
                    String pubTime = mvJson.getString("pub_date");
                    String coverImgUrl = mvJson.getString("picurl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.QQ);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setPubTime(pubTime);
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

        // 酷我
        Callable<CommonResult<NetMvInfo>> getRecommendMvKw = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                HttpResponse resp = SdkCommon.kwRequest(String.format(RECOMMEND_MV_KW_API, s[7], page, limit)).executeAsync();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String mvInfoBody = resp.body();
                    JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                    JSONObject data = mvInfoJson.getJSONObject("data");
                    t = data.getIntValue("total");
                    JSONArray mvArray = data.getJSONArray("mvlist");
                    for (int i = 0, len = mvArray.size(); i < len; i++) {
                        JSONObject mvJson = mvArray.getJSONObject(i);

                        String mvId = mvJson.getString("id");
                        String mvName = mvJson.getString("name");
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
            }
            return new CommonResult<>(r, t);
        };

        // 千千
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvQi = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = SdkCommon.qiRequest(String.format(RECOMMEND_MV_QI_API, page, limit, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("result");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("assetCode");
                String mvName = mvJson.getString("title").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("playnum");
                String coverImgUrl = mvJson.getString("pic");
                Double duration = mvJson.getDouble("duration") / 1000;
                String pubTime = mvJson.getString("originalReleaseDate").split("T")[0];

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QI);
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

        // 5sing
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvFs = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_FS_API, page, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject user = mvJson.getJSONObject("user");

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = user.getString("NN");
                String creatorId = user.getString("ID");
                Long playCount = mvJson.getLong("play");
                String coverImgUrl = mvJson.getString("cover_url");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.FS);
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
        // 最热 MV
        Callable<CommonResult<NetMvInfo>> getHotMvFs = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(HOT_MV_FS_API, page, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject user = mvJson.getJSONObject("user");

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = user.getString("NN");
                String creatorId = user.getString("ID");
                Long playCount = mvJson.getLong("play");
                String coverImgUrl = mvJson.getString("cover_url");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.FS);
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
        // 最新 MV
        Callable<CommonResult<NetMvInfo>> getNewMvFs = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(NEW_MV_FS_API, page, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject user = mvJson.getJSONObject("user");

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = user.getString("NN");
                String creatorId = user.getString("ID");
                Long playCount = mvJson.getLong("play");
                String coverImgUrl = mvJson.getString("cover_url");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.FS);
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

        // 好看
        // 猜你喜欢视频
        Callable<CommonResult<NetMvInfo>> getGuessVideoHk = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(GUESS_VIDEO_HK_API, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("apiData");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
//                String creatorId = mvJson.getString("author_id");
                Long playCount = StringUtil.parseNumber(mvJson.getString("fmplaycnt"));
                String coverImgUrl = "https:" + mvJson.getString("poster");
                Double duration = TimeUtil.toSeconds(mvJson.getString("time_length"));
                String pubTime = TimeUtil.msToDate(mvJson.getLong("publish_time") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
//                mvInfo.setCreatorId(creatorId);
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
        // 榜单视频
        Callable<CommonResult<NetMvInfo>> getTopVideoHk = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(TOP_VIDEO_HK_API, page, limit))
                    .executeAsync()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("apiData").getJSONObject("response");
            t = data.getIntValue("total_page") * limit;
            JSONArray mvArray = data.getJSONArray("video");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("third_id");
                Long playCount = mvJson.getLong("hot");
                String coverImgUrl = mvJson.getString("poster");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publish_time").replaceAll("[发布时间：日]", "").replaceAll("年|月", "-");

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
        // 分类推荐视频
        Callable<CommonResult<NetMvInfo>> getRecommendVideoHk = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[8])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_VIDEO_HK_API, s[8], limit))
                        .cookie(SdkCommon.HK_COOKIE)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data").getJSONObject("response");
                JSONArray mvArray = data.getJSONArray("videos");
                t = limit;
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("title");
                    String artistName = mvJson.getString("source_name");
                    String creatorId = mvJson.getString("third_id");
                    Long playCount = mvJson.getLong("playcnt");
                    String coverImgUrl = mvJson.getString("poster_pc");
                    Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                    String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

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
            }
            return new CommonResult<>(r, t);
        };

        // 哔哩哔哩
        // 热门视频
        Callable<CommonResult<NetMvInfo>> getHotVideoBi = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(HOT_VIDEO_BI_API, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
            t = data.getBoolean("no_more") ? page * limit : page * limit + 1;
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject owner = mvJson.getJSONObject("owner");

                String id = mvJson.getString("cid");
                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = owner.getString("name");
                String creatorId = owner.getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getJSONObject("stat").getLong("view");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
                mvInfo.setId(id);
                mvInfo.setBvid(bvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 分区排行榜视频
        Callable<CommonResult<NetMvInfo>> getCatRankVideoBi = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[9])) {
                String mvInfoBody = HttpRequest.get(String.format(CAT_RANK_VIDEO_BI_API, s[9]))
                        .cookie(SdkCommon.BI_COOKIE)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(page * limit, mvArray.size()); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

//                    String id = mvJson.getString("cid");
                    String bvId = mvJson.getString("bvid");
                    String mvName = mvJson.getString("title");
                    String artistName = mvJson.getString("author");
                    String creatorId = mvJson.getString("mid");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getLong("play");
                    Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                    String pubTime = mvJson.getString("create").split(" ")[0];

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.BI);
//                    mvInfo.setId(id);
                    mvInfo.setBvid(bvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 分区最新视频
        Callable<CommonResult<NetMvInfo>> getCatNewVideoBi = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[9])) {
                String mvInfoBody = HttpRequest.get(String.format(CAT_NEW_VIDEO_BI_API, s[9], page, limit))
                        .cookie(SdkCommon.BI_COOKIE)
                        .executeAsync()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getJSONObject("page").getIntValue("count");
                JSONArray mvArray = data.getJSONArray("archives");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    JSONObject owner = mvJson.getJSONObject("owner");

                    String id = mvJson.getString("cid");
                    String bvId = mvJson.getString("bvid");
                    String mvName = mvJson.getString("title");
                    String artistName = owner.getString("name");
                    String creatorId = owner.getString("mid");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getJSONObject("stat").getLong("view");
                    Double duration = mvJson.getDouble("duration");
                    String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.BI);
                    mvInfo.setId(id);
                    mvInfo.setBvid(bvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 发姐
        // 视频
        Callable<CommonResult<NetMvInfo>> getVideoFa = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String[] sp = s[10].split(" ", -1);
            if (StringUtil.notEmpty(sp[0]) || StringUtil.isEmpty(sp[1])) {
                String mvInfoBody = HttpRequest.get(String.format(VIDEO_FA_API, page, sp[0]))
                        .setFollowRedirects(true)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(mvInfoBody);
                Elements as = doc.select(".pagination ul li");
                if (as.isEmpty()) t = limit;
                else {
                    for (int i = as.size() - 1; i >= 0; i--) {
                        String ts = as.get(i).text();
                        if (!StringUtil.isNumber(ts)) continue;
                        t = Integer.parseInt(ts) * limit;
                        break;
                    }
                }
                Elements mvArray = doc.select(".post.list");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    Element mv = mvArray.get(i);
                    Elements a = mv.select(".con h3 a");
                    Elements author = mv.select(".author a");
                    Elements img = mv.select(".img img");
                    Elements views = mv.select(".views");
                    Elements time = mv.select(".time");

                    String id = RegexUtil.getGroup1("topics/(\\d+)/", a.attr("href"));
                    String mvName = a.text();
                    String artistName = author.text();
                    String coverImgUrl = img.attr("src");
                    Long playCount = StringUtil.parseNumber(views.text());
                    String pubTime = time.text().trim();

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.FA);
                    mvInfo.setId(id);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setPubTime(pubTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 直播
        Callable<CommonResult<NetMvInfo>> getLiveFa = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            String[] sp = s[10].split(" ", -1);
            if (StringUtil.notEmpty(sp[1]) || StringUtil.isEmpty(sp[0])) {
                String mvInfoBody = HttpRequest.get(String.format(LIVE_FA_API, page, sp[1]))
                        .setFollowRedirects(true)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(mvInfoBody);
                Elements as = doc.select(".pagination ul li");
                if (as.isEmpty()) t = limit;
                else {
                    for (int i = as.size() - 1; i >= 0; i--) {
                        String ts = as.get(i).text();
                        if (!StringUtil.isNumber(ts)) continue;
                        t = Integer.parseInt(ts) * limit;
                        break;
                    }
                }
                Elements mvArray = doc.select(".post.list");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    Element mv = mvArray.get(i);
                    Elements a = mv.select(".con h3 a");
                    Elements author = mv.select(".author a");
                    Elements img = mv.select(".img img");
                    Elements views = mv.select(".views");
                    Elements time = mv.select(".time");

                    String id = RegexUtil.getGroup1("topics/(\\d+)/", a.attr("href"));
                    String mvName = a.text();
                    String artistName = author.text();
                    String coverImgUrl = img.attr("src");
                    Long playCount = StringUtil.parseNumber(views.text());
                    String pubTime = time.text().trim();

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.FA);
                    mvInfo.setId(id);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setPubTime(pubTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 李志
        Callable<CommonResult<NetMvInfo>> getVideoLz = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[11])) {
                String mvInfoBody = HttpRequest.get(String.format(VIDEO_LZ_API, s[11]))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(mvInfoBody);
                Elements mvArray = doc.select(".tile-content");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    Element mv = mvArray.get(i);
                    Elements a = mv.select(".tile-link.ajax-link");
                    Elements hl = mv.select(".tile-headline");
                    Elements img = mv.select("img");
                    Elements time = mv.select(".tile-date");

                    String id = RegexUtil.getGroup1("live/(.*?)/", a.attr("href"));
                    String mvName = hl.text();
                    String artistName = "李志";
                    String coverImgUrl = img.attr("srcset").split(" ")[0];
                    if (StringUtil.isEmpty(coverImgUrl)) coverImgUrl = img.attr("data-src");
                    String pubTime = time.text();

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.LZ);
                    mvInfo.setId(id);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPubTime(pubTime);
                    String finalCoverImgUrl = coverImgUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(finalCoverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(mvInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMv));
            taskList.add(GlobalExecutors.requestExecutor.submit(getMvRanking));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewMv));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllMv));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getExclusiveMv));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getIpMvKg));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvQq));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewMvQq));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvKw));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvQi));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvFs));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotMvFs));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getNewMvFs));
        }
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getGuessVideoHk));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getTopVideoHk));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendVideoHk));
        }
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotVideoBi));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRankVideoBi));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatNewVideoBi));
        }
        if (src == NetMusicSource.FA || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getVideoFa));
            taskList.add(GlobalExecutors.requestExecutor.submit(getLiveFa));
        }
        if (src == NetMusicSource.LZ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getVideoLz));
        }

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

        return new CommonResult<>(res, total.get());
    }
}
