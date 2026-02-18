package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeRankingFetchReq {
    private static MeRankingFetchReq instance;

    private MeRankingFetchReq() {
    }

    public static MeRankingFetchReq getInstance() {
        if (instance == null) instance = new MeRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (猫耳)
    private final String GET_RANKING_ME_API = "https://www.missevan.com/mobileWeb/albumList";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = HttpRequest.get(GET_RANKING_ME_API)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray rankingArray = rankingInfoJson.getJSONArray("info");
        for (int i = 0, len = rankingArray.size(); i < len; i++) {
            JSONObject rankingJson = rankingArray.getJSONObject(i).getJSONObject("album");

            String rankingId = rankingJson.getString("id");
            String rankingName = rankingJson.getString("title");
            String coverImgUrl = rankingJson.getString("front_cover");
            String updateTime = TimeUtil.msToDate(rankingJson.getLong("last_update_time") * 1000);
            Long playCount = rankingJson.getLong("view_count");

            NetRankingInfo rankingInfo = new NetRankingInfo();
            rankingInfo.setSource(NetMusicSource.ME);
            rankingInfo.setId(rankingId);
            rankingInfo.setName(rankingName);
            rankingInfo.setCoverImgUrl(coverImgUrl);
            rankingInfo.setUpdateTime(updateTime);
            rankingInfo.setPlayCount(playCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankingInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankingInfo);
        }
        return new CommonResult<>(r, t);
    }
}
