package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgRankingFetchReq {
    private static MgRankingFetchReq instance;

    private MgRankingFetchReq() {
    }

    public static MgRankingFetchReq getInstance() {
        if (instance == null) instance = new MgRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (咪咕)
    private final String GET_RANKING_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/rank-list";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = HttpRequest.get(GET_RANKING_MG_API)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONObject data = rankingInfoJson.getJSONObject("data");
        JSONArray contentItemList = data.getJSONArray("contentItemList");
        for (int i = 0, len = contentItemList.size(); i < len; i++) {
            JSONArray itemList = contentItemList.getJSONObject(i).getJSONArray("itemList");
            if (JsonUtil.notEmpty(itemList)) {
                for (int j = 0, s = itemList.size(); j < s; j++) {
                    JSONObject item = itemList.getJSONObject(j);

                    String template = item.getString("template");
                    if (template.equals("row1") || template.equals("grid1")) {
                        JSONObject param = item.getJSONObject("displayLogId").getJSONObject("param");

                        String rankingId = param.getString("rankId");
                        String rankingName = param.getString("rankName");
                        String coverImgUrl = item.getString("imageUrl");
                        String updateFre = item.getJSONArray("barList").getJSONObject(0).getString("title");

                        NetRankingInfo rankingInfo = new NetRankingInfo();
                        rankingInfo.setSource(NetMusicSource.MG);
                        rankingInfo.setId(rankingId);
                        rankingInfo.setName(rankingName);
                        rankingInfo.setUpdateFre(updateFre);
                        rankingInfo.setCoverImgUrl(coverImgUrl);
//                        rankingInfo.setPlayCount(playCount);
//                        rankingInfo.setUpdateTime(updateTime);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                            rankingInfo.setCoverImgThumb(coverImgThumb);
                        });

                        r.add(rankingInfo);
                    }
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
