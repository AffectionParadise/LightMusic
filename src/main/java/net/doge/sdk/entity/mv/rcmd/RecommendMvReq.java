package net.doge.sdk.entity.mv.rcmd;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.sdk.common.Tags;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RecommendMvReq {
    // MV 排行 API
    private final String TOP_MV_API = SdkCommon.prefix + "/top/mv?area=%s&offset=%s&limit=%s";
    // 最新 MV API
    private final String NEW_MV_API = SdkCommon.prefix + "/mv/first?area=%s&limit=100";
    // 全部 MV API
    private final String ALL_MV_API = SdkCommon.prefix + "/mv/all?area=%s&type=%s&offset=%s&limit=%s";
    // 推荐 MV API
    private final String RECOMMEND_MV_API = SdkCommon.prefix + "/personalized/mv?limit=100";
    // 网易出品 MV API
    private final String EXCLUSIVE_MV_API = SdkCommon.prefix + "/mv/exclusive/rcmd?offset=%s&limit=%s";
    // 推荐 MV API (酷狗)
    private final String RECOMMEND_MV_KG_API = "http://mobilecdnbj.kugou.com/api/v5/video/list?sort=4&id=%s&page=%s&pagesize=%s";
    // 推荐 MV API (QQ)
    private final String RECOMMEND_MV_QQ_API = SdkCommon.prefixQQ33 + "/mv/list?area=%s&version=%s&pageNo=%s&pageSize=%s";
    // 最新 MV API (QQ)
    private final String NEW_MV_QQ_API = SdkCommon.prefixQQ33 + "/new/mv?type=%s";
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

    /**
     * 获取 MV 排行 + 最新 MV + 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMvs(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> mvInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.mvTag.get(tag);

        // 网易云(程序分页)
        // MV 排行
        Callable<CommonResult<NetMvInfo>> getMvRanking = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[0])) {
                String mvInfoBody = HttpRequest.get(String.format(TOP_MV_API, s[0].replace("全部", ""), (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                    String pubTime = mvJson.getJSONObject("mv").getString("publishTime");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新 MV
        Callable<CommonResult<NetMvInfo>> getNewMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[0])) {
                String mvInfoBody = HttpRequest.get(String.format(NEW_MV_API, s[0]))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 全部 MV
        Callable<CommonResult<NetMvInfo>> getAllMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[0]) || StringUtil.isNotEmpty(s[1])) {
                String mvInfoBody = HttpRequest.get(String.format(ALL_MV_API, s[0], s[1], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(RECOMMEND_MV_API)
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("result");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 网易出品 MV
        Callable<CommonResult<NetMvInfo>> getExclusiveMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(EXCLUSIVE_MV_API, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        Callable<CommonResult<NetMvInfo>> getRecommendMvKg = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[2])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_KG_API, s[2], page, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("info");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("mvhash");
                    String mvName = mvJson.getString("videoname");
                    String artistName = mvJson.getString("singername");
                    JSONArray artistArray = mvJson.optJSONArray("authors");
                    String creatorId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("singerid") : "";
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 推荐 MV (接口分页)
        Callable<CommonResult<NetMvInfo>> getRecommendMvQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[3])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_QQ_API, s[3], s[4], page, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("list");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.QQ);
                    String creatorId = mvJson.getJSONArray("singers").getJSONObject(0).getString("mid");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新 MV (程序分页)
        Callable<CommonResult<NetMvInfo>> getNewMvQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[5])) {
                String mvInfoBody = HttpRequest.get(String.format(NEW_MV_QQ_API, s[5]))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("list");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("mvtitle").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.QQ);
                    String creatorId = mvJson.getJSONArray("singers").getJSONObject(0).getString("mid");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetMvInfo>> getRecommendMvKw = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[6])) {
                HttpResponse resp = SdkCommon.kwRequest(String.format(RECOMMEND_MV_KW_API, s[6], page, limit)).execute();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String mvInfoBody = resp.body();
                    JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                    JSONObject data = mvInfoJson.getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray mvArray = data.getJSONArray("mvlist");
                    for (int i = 0, len = mvArray.size(); i < len; i++) {
                        JSONObject mvJson = mvArray.getJSONObject(i);

                        String mvId = mvJson.getString("id");
                        String mvName = mvJson.getString("name");
                        String artistName = mvJson.getString("artist");
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

                        res.add(mvInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvQi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(RECOMMEND_MV_QI_API, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("result");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("assetCode");
                String mvName = mvJson.getString("title").trim();
                String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.QI);
                String creatorId = mvJson.getJSONArray("artist").getJSONObject(0).getString("artistCode");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 5sing
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvFs = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_FS_API, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getJSONObject("user").getString("NN");
                String creatorId = mvJson.getJSONObject("user").getString("ID");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 最热 MV
        Callable<CommonResult<NetMvInfo>> getHotMvFs = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(HOT_MV_FS_API, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getJSONObject("user").getString("NN");
                String creatorId = mvJson.getJSONObject("user").getString("ID");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 最新 MV
        Callable<CommonResult<NetMvInfo>> getNewMvFs = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(NEW_MV_FS_API, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getJSONObject("user").getString("NN");
                String creatorId = mvJson.getJSONObject("user").getString("ID");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 好看
        // 猜你喜欢视频
        Callable<CommonResult<NetMvInfo>> getGuessVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(GUESS_VIDEO_HK_API, System.currentTimeMillis()))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("apiData");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 榜单视频
        Callable<CommonResult<NetMvInfo>> getTopVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(TOP_VIDEO_HK_API, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("apiData").getJSONObject("response");
            t = data.getInt("total_page") * limit;
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类推荐视频
        Callable<CommonResult<NetMvInfo>> getRecommendVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[7])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_VIDEO_HK_API, s[7], limit))
                        .header(Header.COOKIE, SdkCommon.HK_COOKIE)
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 哔哩哔哩
        // 热门视频
        Callable<CommonResult<NetMvInfo>> getHotVideoBi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(HOT_VIDEO_BI_API, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvInfoBody).getJSONObject("data");
            t = data.getBoolean("no_more") ? page * limit : page * limit + 1;
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String id = mvJson.getString("cid");
                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getJSONObject("owner").getString("name");
                String creatorId = mvJson.getJSONObject("owner").getString("mid");
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

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分区排行榜视频
        Callable<CommonResult<NetMvInfo>> getCatRankVideoBi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[8])) {
                String mvInfoBody = HttpRequest.get(String.format(CAT_RANK_VIDEO_BI_API, s[8]))
                        .cookie(SdkCommon.BI_COOKIE)
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分区最新视频
        Callable<CommonResult<NetMvInfo>> getCatNewVideoBi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[8])) {
                String mvInfoBody = HttpRequest.get(String.format(CAT_NEW_VIDEO_BI_API, s[8], page, limit))
                        .cookie(SdkCommon.BI_COOKIE)
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getJSONObject("page").getInt("count");
                JSONArray mvArray = data.getJSONArray("archives");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String id = mvJson.getString("cid");
                    String bvId = mvJson.getString("bvid");
                    String mvName = mvJson.getString("title");
                    String artistName = mvJson.getJSONObject("owner").getString("name");
                    String creatorId = mvJson.getJSONObject("owner").getString("mid");
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

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMv));
            taskList.add(GlobalExecutors.requestExecutor.submit(getMvRanking));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewMv));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllMv));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getExclusiveMv));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvKg));
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
        mvInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(mvInfos, total.get());
    }
}
