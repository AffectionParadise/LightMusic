package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.MvInfoType;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcMvSearchReq {
    private static NcMvSearchReq instance;

    private NcMvSearchReq() {
    }

    public static NcMvSearchReq getInstance() {
        if (instance == null) instance = new NcMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV / 视频 API (网易云)
    private final String CLOUD_SEARCH_NC_API = "https://interface.music.163.com/eapi/cloudsearch/pc";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":1004,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
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
    }

    /**
     * 根据关键词获取视频
     */
    public CommonResult<NetMvInfo> searchVideos(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":1014,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
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
    }
}
