package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetRankInfo;
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

public class NcRankFetchReq {
    private static NcRankFetchReq instance;

    private NcRankFetchReq() {
    }

    public static NcRankFetchReq getInstance() {
        if (instance == null) instance = new NcRankFetchReq();
        return instance;
    }

    // 获取榜单 API (网易云)
    private final String GET_RANK_NC_API = "https://music.163.com/api/toplist";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String rankInfoBody = SdkCommon.ncRequest(Method.POST, GET_RANK_NC_API, "{}", options)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray rankArray = rankInfoJson.getJSONArray("list");
        for (int i = 0, len = rankArray.size(); i < len; i++) {
            JSONObject rankJson = rankArray.getJSONObject(i);

            String rankId = rankJson.getString("id");
            String rankName = rankJson.getString("name");
            String coverImgUrl = rankJson.getString("coverImgUrl");
            String desc = rankJson.getString("description");
            String description = StringUtil.isEmpty(desc) ? "" : desc;
            Long playCount = rankJson.getLong("playCount");
            String updateFre = rankJson.getString("updateFrequency");
            String updateTime = TimeUtil.msToDate(rankJson.getLong("trackUpdateTime"));

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
            rankInfo.setDescription(description);
            rankInfo.setPlayCount(playCount);
            rankInfo.setUpdateFre(updateFre);
            rankInfo.setUpdateTime(updateTime);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankInfo);
        }
        return new CommonResult<>(r, t);
    }
}
