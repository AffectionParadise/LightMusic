package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiRecommendMvReq {
    private static BiRecommendMvReq instance;

    private BiRecommendMvReq() {
    }

    public static BiRecommendMvReq getInstance() {
        if (instance == null) instance = new BiRecommendMvReq();
        return instance;
    }

    // 热门视频 API (哔哩哔哩)
    private final String HOT_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/popular?pn=%s&ps=%s";
    // 分区排行榜视频 API (哔哩哔哩)
    private final String CAT_RANK_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/ranking/region?rid=%s";
    // 分区最新视频 API (哔哩哔哩)
    private final String CAT_NEW_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/dynamic/region?rid=%s&pn=%s&ps=%s";

    /**
     * 热门视频
     */
    public CommonResult<NetMvInfo> getHotVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(HOT_VIDEO_BI_API, page, limit))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
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

        if (StringUtil.notEmpty(s[9])) {
            String mvInfoBody = HttpRequest.get(String.format(CAT_RANK_VIDEO_BI_API, s[9]))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
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
                Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("create").split(" ")[0];

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
//                    mvInfo.setId(id);
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

        if (StringUtil.notEmpty(s[9])) {
            String mvInfoBody = HttpRequest.get(String.format(CAT_NEW_VIDEO_BI_API, s[9], page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
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