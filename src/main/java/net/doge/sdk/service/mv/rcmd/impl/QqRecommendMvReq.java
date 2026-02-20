package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqRecommendMvReq {
    private static QqRecommendMvReq instance;

    private QqRecommendMvReq() {
    }

    public static QqRecommendMvReq getInstance() {
        if (instance == null) instance = new QqRecommendMvReq();
        return instance;
    }

    // 最新 MV API (QQ)
    private final String NEW_MV_QQ_API = "https://c.y.qq.com/mv/fcgi-bin/getmv_by_tag?cmd=shoubo&format=json&lan=%s";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.RECOMMEND_MV_QQ];
        if (StringUtil.notEmpty(param)) {
            String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody(String.format("{\"comm\":{\"ct\":24},\"mv_list\":{\"module\":\"MvService.MvInfoProServer\"," +
                            "\"method\":\"GetAllocMvInfo\",\"param\":{\"area_id\":%s,\"version_id\":%s,\"start\":%s,\"size\":%s," +
                            "\"order\":1}}}", param, s[TagType.RECOMMEND_MV_QQ_2], (page - 1) * limit, limit))
                    .executeAsStr();
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
    }

    /**
     * 最新 MV (程序分页)
     */
    public CommonResult<NetMvInfo> getNewMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.NEW_MV_QQ];
        if (StringUtil.notEmpty(param)) {
            String mvInfoBody = HttpRequest.get(String.format(NEW_MV_QQ_API, param))
                    .executeAsStr();
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
    }
}
