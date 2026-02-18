package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KgRankingFetchReq {
    private static KgRankingFetchReq instance;

    private KgRankingFetchReq() {
    }

    public static KgRankingFetchReq getInstance() {
        if (instance == null) instance = new KgRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (酷狗)
    private final String GET_RANKING_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/list?apiver=6&area_code=1";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = HttpRequest.get(GET_RANKING_KG_API)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray rankingArray = rankingInfoJson.getJSONObject("data").getJSONArray("info");
        for (int i = 0, len = rankingArray.size(); i < len; i++) {
            JSONObject rankingJson = rankingArray.getJSONObject(i);

            String rankingId = rankingJson.getString("rankid");
            String rankingName = rankingJson.getString("rankname");
            String coverImgUrl = rankingJson.getString("banner_9").replace("/{size}", "");
            String description = rankingJson.getString("intro");
            String updateFre = rankingJson.getString("update_frequency");
            Long playCount = rankingJson.getLong("play_times");

            NetRankingInfo rankingInfo = new NetRankingInfo();
            rankingInfo.setSource(NetMusicSource.KG);
            rankingInfo.setId(rankingId);
            rankingInfo.setName(rankingName);
            rankingInfo.setCoverImgUrl(coverImgUrl);
            rankingInfo.setDescription(description);
            rankingInfo.setUpdateFre(updateFre);
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
