package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiRecommendMvReq {
    private static QiRecommendMvReq instance;

    private QiRecommendMvReq() {
    }

    public static QiRecommendMvReq getInstance() {
        if (instance == null) instance = new QiRecommendMvReq();
        return instance;
    }

    // 推荐 MV API (千千)
    private final String RECOMMEND_MV_QI_API = "https://music.91q.com/v1/video/list?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = SdkCommon.qiRequest(String.format(RECOMMEND_MV_QI_API, page, limit, System.currentTimeMillis()))
                .executeAsStr();
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
    }
}
