package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRankingFetchReq {
    private static NcRankingFetchReq instance;

    private NcRankingFetchReq() {
    }

    public static NcRankingFetchReq getInstance() {
        if (instance == null) instance = new NcRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (网易云)
    private final String GET_RANKING_NC_API = "https://music.163.com/api/toplist";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String rankingInfoBody = SdkCommon.ncRequest(Method.POST, GET_RANKING_NC_API, "{}", options)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray rankingArray = rankingInfoJson.getJSONArray("list");
        for (int i = 0, len = rankingArray.size(); i < len; i++) {
            JSONObject rankingJson = rankingArray.getJSONObject(i);

            String rankingId = rankingJson.getString("id");
            String rankingName = rankingJson.getString("name");
            String coverImgUrl = rankingJson.getString("coverImgUrl");
            String desc = rankingJson.getString("description");
            String description = StringUtil.isEmpty(desc) ? "" : desc;
            Long playCount = rankingJson.getLong("playCount");
            String updateFre = rankingJson.getString("updateFrequency");
            String updateTime = TimeUtil.msToDate(rankingJson.getLong("trackUpdateTime"));

            NetRankingInfo rankingInfo = new NetRankingInfo();
            rankingInfo.setId(rankingId);
            rankingInfo.setName(rankingName);
            rankingInfo.setCoverImgUrl(coverImgUrl);
            rankingInfo.setDescription(description);
            rankingInfo.setPlayCount(playCount);
            rankingInfo.setUpdateFre(updateFre);
            rankingInfo.setUpdateTime(updateTime);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankingInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankingInfo);
        }
        return new CommonResult<>(r, t);
    }
}
