package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiRankFetchReq {
    private static QiRankFetchReq instance;

    private QiRankFetchReq() {
    }

    public static QiRankFetchReq getInstance() {
        if (instance == null) instance = new QiRankFetchReq();
        return instance;
    }

    // 获取榜单 API (千千)
    private final String GET_RANK_QI_API = "https://music.91q.com/v1/bd/category?appid=16073360&timestamp=%s";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = SdkCommon.qiRequest(String.format(GET_RANK_QI_API, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray rankArray = rankInfoJson.getJSONArray("data");
        for (int i = 0, len = rankArray.size(); i < len; i++) {
            JSONObject rankJson = rankArray.getJSONObject(i);

            String rankId = rankJson.getString("bdid");
            String rankName = rankJson.getString("title");
            String coverImgUrl = rankJson.getString("pic");

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setSource(NetMusicSource.QI);
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
            rankInfo.setDescription("");
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankInfo);
        }
        return new CommonResult<>(r, t);
    }
}
