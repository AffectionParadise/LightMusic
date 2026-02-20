package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KgRankFetchReq {
    private static KgRankFetchReq instance;

    private KgRankFetchReq() {
    }

    public static KgRankFetchReq getInstance() {
        if (instance == null) instance = new KgRankFetchReq();
        return instance;
    }

    // 获取榜单 API (酷狗)
    private final String GET_RANK_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/list?apiver=6&area_code=1";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = HttpRequest.get(GET_RANK_KG_API)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray rankArray = rankInfoJson.getJSONObject("data").getJSONArray("info");
        for (int i = 0, len = rankArray.size(); i < len; i++) {
            JSONObject rankJson = rankArray.getJSONObject(i);

            String rankId = rankJson.getString("rankid");
            String rankName = rankJson.getString("rankname");
            String coverImgUrl = rankJson.getString("banner_9").replace("/{size}", "");
            String description = rankJson.getString("intro");
            String updateFre = rankJson.getString("update_frequency");
            Long playCount = rankJson.getLong("play_times");

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setSource(NetResourceSource.KG);
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
            rankInfo.setDescription(description);
            rankInfo.setUpdateFre(updateFre);
            rankInfo.setPlayCount(playCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankInfo);
        }
        return new CommonResult<>(r, t);
    }
}
