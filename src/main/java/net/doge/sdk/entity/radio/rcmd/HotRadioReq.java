package net.doge.sdk.entity.radio.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.model.RadioType;
import net.doge.model.entity.NetRadioInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
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

public class HotRadioReq {
    //    // 个性电台推荐 API
//    private final String PERSONAL_RADIO_API = prefix + "/dj/personalize/recommend";
    // 今日优选电台 API
    private final String DAILY_RADIO_API = "https://music.163.com/weapi/djradio/home/today/perfered";
    // 热门电台 API
    private final String HOT_RADIO_API = "https://music.163.com/weapi/djradio/hot/v1";
    // 热门电台榜 API
    private final String RADIO_TOPLIST_API = "https://music.163.com/api/djradio/toplist";
    // 推荐电台 API
    private final String RECOMMEND_RADIO_API = "https://music.163.com/weapi/djradio/recommend/v1";
    // 分类热门电台 API
    private final String CAT_HOT_RADIO_API = "https://music.163.com/api/djradio/hot";
    // 分类推荐电台 API
    private final String CAT_REC_RADIO_API = "https://music.163.com/weapi/djradio/recommend";
    // 分类电台榜 API (喜马拉雅)
    private final String CAT_RADIO_RANKING_XM_API = "https://www.ximalaya.com/revision/rank/v3/element?typeId=%s&clusterId=%s";
    // 分类电台 API (喜马拉雅)
    private final String CAT_RADIO_XM_API
            = "https://www.ximalaya.com/revision/category/queryCategoryPageAlbums?category=%s&subcategory=%s&meta=&sort=0&page=%s&perPage=%s&useCache=false";
    // 频道电台 API (喜马拉雅)
    private final String CHANNEL_RADIO_XM_API = "https://www.ximalaya.com/revision/metadata/v2/channel/albums?groupId=%s&pageNum=%s&pageSize=%s&sort=1&metadata=";
    // 周榜电台 API (猫耳)
    private final String WEEK_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=1&page=%s&page_size=%s";
    // 月榜电台 API (猫耳)
    private final String MONTH_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=2&page=%s&page_size=%s";
    // 总榜电台 API (猫耳)
    private final String ALL_TIME_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=3&page=%s&page_size=%s";
    // 广播剧分类电台 API (猫耳)
    private final String CAT_RADIO_ME_API = "https://www.missevan.com/dramaapi/filter?filters=%s_0_%s_%s_0&page=%s&order=1&page_size=%s";
    // Top 250 电台 API (豆瓣)
    private final String TOP_RADIO_DB_API = "https://movie.douban.com/top250?start=%s&filter=";
    // 分类电台 API (豆瓣)
    private final String CAT_RADIO_DB_API = "https://movie.douban.com/j/chart/top_list?type=%s&interval_id=100:90&action=&start=%s&limit=%s";
    // 分类电台总数 API (豆瓣)
    private final String CAT_RADIO_TOTAL_DB_API = "https://movie.douban.com/j/chart/top_list_count?type=%s&interval_id=100:90";
    // 分类游戏电台 API (豆瓣)
    private final String CAT_GAME_RADIO_DB_API = "https://www.douban.com/j/ilmen/game/search?genres=%s&platforms=%s&more=%s&sort=rating";

    /**
     * 获取个性电台 + 今日优选 + 热门电台 + 热门电台榜
     */
    public CommonResult<NetRadioInfo> getHotRadios(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> radioInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.radioTag.get(tag);
        // 网易云(程序分页)
//        // 个性电台推荐
//        String radioInfoBody = HttpRequest.get(PERSONAL_RADIO_API)
//                .execute()
//                .body();
//        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
//        JSONArray radioArray = radioInfoJson.getJSONArray("data");
//        for (int i = 0, len = radioArray.size(); i < len; i++) {
//            JSONObject radioJson = radioArray.getJSONObject(i);
//
//            long radioId = radioJson.getLong("id");
//            String radioName = radioJson.getString("name");
//
//            NetRadioInfo radioInfo = new NetRadioInfo();
//            radioInfo.setId(radioId);
//            radioInfo.setName(radioName);
//            radioInfos.add(radioInfo);
//        }
        // 今日优选电台
        Callable<CommonResult<NetRadioInfo>> getDailyRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, DAILY_RADIO_API, "{\"page\":0}", options)
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("data");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = null;
//                if (i >= rs) dj = radioJson.getJSONObject("dj").getString("nickname");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.getLongValue("createTime");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 热门电台
        Callable<CommonResult<NetRadioInfo>> getHotRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, HOT_RADIO_API, "{\"offset\":0,\"limit\":1000}", options)
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 热门电台榜
        Callable<CommonResult<NetRadioInfo>> getRadiosRanking = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_TOPLIST_API, "{\"type\":1,\"offset\":0,\"limit\":200}", options)
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("toplist");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐电台
        Callable<CommonResult<NetRadioInfo>> getRecRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_RADIO_API, "{}", options)
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类热门电台
        Callable<CommonResult<NetRadioInfo>> getCatHotRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
                String radioInfoBody = SdkCommon.ncRequest(Method.POST, CAT_HOT_RADIO_API,
                                String.format("{\"cateId\":\"%s\",\"offset\":%s,\"limit\":%s}", s[0], (page - 1) * limit, limit), options)
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                t = radioInfoJson.getIntValue("count");
                JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getJSONObject("dj").getString("nickname");
                    String djId = radioJson.getJSONObject("dj").getString("userId");
//                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getIntValue("programCount");
                    String category = radioJson.getString("category");
                    String coverImgThumbUrl = radioJson.getString("picUrl");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类推荐电台
        Callable<CommonResult<NetRadioInfo>> getCatRecRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
                String radioInfoBody = SdkCommon.ncRequest(Method.POST, CAT_REC_RADIO_API, String.format("{\"cateId\":\"%s\"}", s[1]), options)
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
                t = radioArray.size();
                for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getJSONObject("dj").getString("nickname");
                    String djId = radioJson.getJSONObject("dj").getString("userId");
//                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getIntValue("programCount");
                    String category = radioJson.getString("category");
                    String coverImgThumbUrl = radioJson.getString("picUrl");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 喜马拉雅
        // 分类电台
        Callable<CommonResult<NetRadioInfo>> getCatRadiosXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                String[] sp = s[3].split(" ", -1);
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_XM_API, sp[0], sp[1], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray radioArray = data.getJSONArray("albums");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("albumId");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getString("anchorName");
                    String djId = radioJson.getString("uid");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getIntValue("trackCount");
                    String category = tag;
                    String coverImgThumbUrl = "https:" + radioJson.getString("coverPath");
                    coverImgThumbUrl = coverImgThumbUrl.substring(0, coverImgThumbUrl.lastIndexOf('!'));

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);

                    final String finalCoverImgThumbUrl = coverImgThumbUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(finalCoverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 频道电台
        Callable<CommonResult<NetRadioInfo>> getChannelRadiosXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                String radioInfoBody = HttpRequest.get(String.format(CHANNEL_RADIO_XM_API, s[4], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray radioArray = data.getJSONArray("albums");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("albumId");
                    String radioName = radioJson.getString("albumTitle");
                    String dj = radioJson.getString("albumUserNickName");
                    Long playCount = radioJson.getLong("albumPlayCount");
                    Integer trackCount = radioJson.getIntValue("albumTrackCount");
                    String category = tag;
                    String coverImgThumbUrl = "https://imagev2.xmcdn.com/" + radioJson.getString("albumCoverPath");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 排行榜
        Callable<CommonResult<NetRadioInfo>> getCatRadioRankingXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String[] sp = s[2].split(" ");
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_RANKING_XM_API, sp[0], sp[1]))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("rankList").getJSONObject(0).getJSONArray("albums");
                t = radioArray.size();
                for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("albumTitle");
                    String dj = radioJson.getString("anchorName");
                    String djId = radioJson.getString("anchorUrl").replace("/zhubo/", "");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getIntValue("trackCount");
                    String category = radioJson.getString("categoryTitle");
                    String coverImgThumbUrl = "https://imagev2.xmcdn.com/" + radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 周榜
        Callable<CommonResult<NetRadioInfo>> getWeekRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(WEEK_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 月榜
        Callable<CommonResult<NetRadioInfo>> getMonthRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(MONTH_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 总榜
        Callable<CommonResult<NetRadioInfo>> getAllTimeRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(ALL_TIME_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 广播剧分类
        Callable<CommonResult<NetRadioInfo>> getCatRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                String[] sp = s[5].split(" ");
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_ME_API, sp[2], sp[0], sp[1], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getIntValue("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String category = radioJson.getString("type_name");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
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
        };

        // 豆瓣
        // Top 250
        Callable<CommonResult<NetRadioInfo>> getTopRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;
            final int rn = 25;

            String radioInfoBody = HttpRequest.get(String.format(TOP_RADIO_DB_API, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioInfoBody);
            Elements rs = doc.select("div.item");
            String ts = RegexUtil.getGroup1("共(\\d+)条", doc.select("span.count").text());
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t -= t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Elements a = radio.select("div.hd a");
                Elements p = radio.select("div.bd p");
                Elements img = radio.select("div.pic img");

                String radioId = RegexUtil.getGroup1("/subject/(\\d+)/", a.attr("href"));
                String radioName = a.text().trim();
                String dj = RegexUtil.getGroup1("导演: (.*?) ", p.text());
                String coverImgThumbUrl = img.attr("src");
                String category = "电影";

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
            return new CommonResult<>(res, t);
        };
        // 分类电台
        Callable<CommonResult<NetRadioInfo>> getCatRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_DB_API, s[6], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONArray radioArray = JSONArray.parseArray(radioInfoBody);
                t = JSONObject.parseObject(HttpRequest.get(String.format(CAT_RADIO_TOTAL_DB_API, s[6])).execute().body()).getIntValue("total");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = SdkUtil.joinString(radioJson.getJSONArray("actors"));
                    String coverImgThumbUrl = radioJson.getString("cover_url");
                    String category = SdkUtil.joinString(radioJson.getJSONArray("types"));

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
        };
        // 分类游戏电台
        Callable<CommonResult<NetRadioInfo>> getCatGameRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String[] sp = s[7].split(" ", -1);
                String radioInfoBody = HttpRequest.get(String.format(CAT_GAME_RADIO_DB_API, sp[0], sp[1], page))
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(radioInfoBody);
                JSONArray radioArray = data.getJSONArray("games");
                t = data.getIntValue("total");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getString("platforms");
                    String coverImgThumbUrl = radioJson.getString("cover");
                    String category = radioJson.getString("genres");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setType(RadioType.GAME);
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
        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getDailyRadios));
                taskList.add(GlobalExecutors.requestExecutor.submit(getHotRadios));
                taskList.add(GlobalExecutors.requestExecutor.submit(getRadiosRanking));
                taskList.add(GlobalExecutors.requestExecutor.submit(getRecRadios));
            }

            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getWeekRadiosMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getMonthRadiosMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getAllTimeRadiosMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosMe));
            }

            if (src == NetMusicSource.DB || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getTopRadiosDb));
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatGameRadiosDb));
            }
        } else {
            if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatHotRadios));
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRecRadios));
            }

            if (src == NetMusicSource.XM || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosXm));
                taskList.add(GlobalExecutors.requestExecutor.submit(getChannelRadiosXm));
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadioRankingXm));
            }

            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosMe));
            }

            if (src == NetMusicSource.DB || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosDb));
                taskList.add(GlobalExecutors.requestExecutor.submit(getCatGameRadiosDb));
            }
        }

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        radioInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(radioInfos, total.get());
    }
}
