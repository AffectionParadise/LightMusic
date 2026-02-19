package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeRankFetchReq {
    private static MeRankFetchReq instance;

    private MeRankFetchReq() {
    }

    public static MeRankFetchReq getInstance() {
        if (instance == null) instance = new MeRankFetchReq();
        return instance;
    }

    // 获取榜单 API (猫耳)
    private final String GET_RANK_ME_API = "https://www.missevan.com/mobileWeb/albumList";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = HttpRequest.get(GET_RANK_ME_API)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray rankArray = rankInfoJson.getJSONArray("info");
        for (int i = 0, len = rankArray.size(); i < len; i++) {
            JSONObject rankJson = rankArray.getJSONObject(i).getJSONObject("album");

            String rankId = rankJson.getString("id");
            String rankName = rankJson.getString("title");
            String coverImgUrl = rankJson.getString("front_cover");
            String updateTime = TimeUtil.msToDate(rankJson.getLong("last_update_time") * 1000);
            Long playCount = rankJson.getLong("view_count");

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setSource(NetMusicSource.ME);
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
            rankInfo.setUpdateTime(updateTime);
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
