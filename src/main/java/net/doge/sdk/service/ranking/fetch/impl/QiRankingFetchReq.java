package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiRankingFetchReq {
    private static QiRankingFetchReq instance;

    private QiRankingFetchReq() {
    }

    public static QiRankingFetchReq getInstance() {
        if (instance == null) instance = new QiRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (千千)
    private final String GET_RANKING_QI_API = "https://music.91q.com/v1/bd/category?appid=16073360&timestamp=%s";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = SdkCommon.qiRequest(String.format(GET_RANKING_QI_API, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray rankingArray = rankingInfoJson.getJSONArray("data");
        for (int i = 0, len = rankingArray.size(); i < len; i++) {
            JSONObject rankingJson = rankingArray.getJSONObject(i);

            String rankingId = rankingJson.getString("bdid");
            String rankingName = rankingJson.getString("title");
            String coverImgUrl = rankingJson.getString("pic");

            NetRankingInfo rankingInfo = new NetRankingInfo();
            rankingInfo.setSource(NetMusicSource.QI);
            rankingInfo.setId(rankingId);
            rankingInfo.setName(rankingName);
            rankingInfo.setCoverImgUrl(coverImgUrl);
            rankingInfo.setDescription("");
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankingInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankingInfo);
        }
        return new CommonResult<>(r, t);
    }
}
