package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BiRecommendMvReq {
    private static BiRecommendMvReq instance;

    private BiRecommendMvReq() {
    }

    public static BiRecommendMvReq getInstance() {
        if (instance == null) instance = new BiRecommendMvReq();
        return instance;
    }

    // 首页推荐视频 API (哔哩哔哩)
    private final String INDEX_REC_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/wbi/index/top/feed/rcmd";
    // 热门视频 API (哔哩哔哩)
    private final String HOT_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/popular";
    // 入站必刷视频 API (哔哩哔哩)
    private final String POP_PRECIOUS_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/popular/precious";
    // 分区排行榜视频 API (哔哩哔哩)
    private final String CAT_RANK_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/ranking/v2";
    // 分区最新视频 API (哔哩哔哩)
    private final String CAT_NEW_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/dynamic/region";
    // 每周必看视频 API (哔哩哔哩)
    private final String POP_SERIES_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/popular/series/one";

    /**
     * 首页推荐视频
     */
    public CommonResult<NetMvInfo> getIndexRecVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = SdkCommon.biRequest(INDEX_REC_VIDEO_BI_API, null)
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        JSONArray mvArray = data.getJSONArray("item");
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject owner = mvJson.getJSONObject("owner");

            String id = mvJson.getString("id");
            String bvId = mvJson.getString("bvid");
            String mvName = mvJson.getString("title");
            String artistName = owner.getString("name");
            String creatorId = owner.getString("mid");
            String coverImgUrl = mvJson.getString("pic");
            Long playCount = mvJson.getJSONObject("stat").getLong("view");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.BI);
            mvInfo.setId(id);
            mvInfo.setBvId(bvId);
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
    }

    /**
     * 热门视频
     */
    public CommonResult<NetMvInfo> getHotVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("pn", page);
        params.put("ps", limit);
        String mvInfoBody = HttpRequest.get(HOT_VIDEO_BI_API)
                .queryParams(params)
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
        t = data.getBoolean("no_more") ? page * limit : page * limit + 1;
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject owner = mvJson.getJSONObject("owner");

            String id = mvJson.getString("aid");
            String bvId = mvJson.getString("bvid");
            String mvName = mvJson.getString("title");
            String artistName = owner.getString("name");
            String creatorId = owner.getString("mid");
            String coverImgUrl = mvJson.getString("pic");
            Long playCount = mvJson.getJSONObject("stat").getLong("view");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.BI);
            mvInfo.setId(id);
            mvInfo.setBvId(bvId);
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
    }

    /**
     * 入站必刷视频
     */
    public CommonResult<NetMvInfo> getPopPreciousVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(POP_PRECIOUS_VIDEO_BI_API)
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        JSONArray mvArray = data.getJSONArray("list");
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject owner = mvJson.getJSONObject("owner");

            String id = mvJson.getString("aid");
            String bvId = mvJson.getString("bvid");
            String mvName = mvJson.getString("title");
            String artistName = owner.getString("name");
            String creatorId = owner.getString("mid");
            String coverImgUrl = mvJson.getString("pic");
            Long playCount = mvJson.getJSONObject("stat").getLong("view");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.BI);
            mvInfo.setId(id);
            mvInfo.setBvId(bvId);
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
    }

    /**
     * 分区排行榜视频
     */
    public CommonResult<NetMvInfo> getCatRankVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagModule.CAT_RANK_VIDEO_BI];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("rid", param);
            params.put("type", "all");
            String mvInfoBody = HttpRequest.get(CAT_RANK_VIDEO_BI_API)
                    .queryParams(params)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("list");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(page * limit, mvArray.size()); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject owner = mvJson.getJSONObject("owner");

                String id = mvJson.getString("aid");
                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = owner.getString("name");
                String creatorId = owner.getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getJSONObject("stat").getLong("view");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.BI);
                mvInfo.setId(id);
                mvInfo.setBvId(bvId);
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
    }

    /**
     * 分区最新视频
     */
    public CommonResult<NetMvInfo> getCatNewVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagModule.CAT_RANK_VIDEO_BI];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("rid", param);
            params.put("pn", page);
            params.put("ps", limit);
            String mvInfoBody = HttpRequest.get(CAT_NEW_VIDEO_BI_API)
                    .queryParams(params)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                t = data.getJSONObject("page").getIntValue("count");
                JSONArray mvArray = data.getJSONArray("archives");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    JSONObject owner = mvJson.getJSONObject("owner");

                    String id = mvJson.getString("aid");
                    String bvId = mvJson.getString("bvid");
                    String mvName = mvJson.getString("title");
                    String artistName = owner.getString("name");
                    String creatorId = owner.getString("mid");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getJSONObject("stat").getLong("view");
                    Double duration = mvJson.getDouble("duration");
                    String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetResourceSource.BI);
                    mvInfo.setId(id);
                    mvInfo.setBvId(bvId);
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
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 每周必看视频
     */
    public CommonResult<NetMvInfo> getPopSeriesVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagModule.POP_SERIES_VIDEO_BI];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("number", param);
            String mvInfoBody = HttpRequest.get(POP_SERIES_VIDEO_BI_API)
                    .queryParams(params)
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("list");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(page * limit, mvArray.size()); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject owner = mvJson.getJSONObject("owner");

                String id = mvJson.getString("aid");
                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = owner.getString("name");
                String creatorId = owner.getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getJSONObject("stat").getLong("view");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.BI);
                mvInfo.setId(id);
                mvInfo.setBvId(bvId);
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
    }
}