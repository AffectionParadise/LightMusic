package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRecommendMvReq {
    private static NcRecommendMvReq instance;

    private NcRecommendMvReq() {
    }

    public static NcRecommendMvReq getInstance() {
        if (instance == null) instance = new NcRecommendMvReq();
        return instance;
    }

    // MV 排行 API (网易云)
    private final String TOP_MV_NC_API = "https://music.163.com/weapi/mv/toplist";
    // 最新 MV API (网易云)
    private final String NEW_MV_NC_API = "https://interface.music.163.com/weapi/mv/first";
    // 全部 MV API (网易云)
    private final String ALL_MV_NC_API = "https://interface.music.163.com/api/mv/all";
    // 推荐 MV API (网易云)
    private final String RECOMMEND_MV_NC_API = "https://music.163.com/weapi/personalized/mv";
    // 网易出品 MV API (网易云)
    private final String EXCLUSIVE_MV_NC_API = "https://interface.music.163.com/api/mv/exclusive/rcmd";

    /**
     * MV 排行
     */
    public CommonResult<NetMvInfo> getMvRank(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, TOP_MV_NC_API, String.format("{\"area\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}",
                            s[0].replace("全部", ""), (page - 1) * limit, limit), options)
                    .executeAsStr();
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
    }

    /**
     * 最新 MV
     */
    public CommonResult<NetMvInfo> getNewMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, NEW_MV_NC_API, String.format("{\"area\":\"%s\",\"limit\":100,\"total\":true}", s[0]), options)
                    .executeAsStr();
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
    }

    /**
     * 全部 MV
     */
    public CommonResult<NetMvInfo> getAllMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTag.get(tag);

        if (StringUtil.notEmpty(s[0]) || StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, ALL_MV_NC_API,
                            String.format("{\"tags\":\"{'area':'%s','type':'%s','order':'上升最快'}\",\"offset\":%s,\"limit\":%s,\"total\":true}",
                                    s[0], s[1], (page - 1) * limit, limit), options)
                    .executeAsStr();
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
    }

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_MV_NC_API, "{}", options)
                .executeAsStr();
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
    }

    /**
     * 网易出品 MV
     */
    public CommonResult<NetMvInfo> getExclusiveMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, EXCLUSIVE_MV_NC_API,
                        String.format("{\"offset\":%s,\"limit\":%s}", (page - 1) * limit, limit), options)
                .executeAsStr();
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
    }
}
