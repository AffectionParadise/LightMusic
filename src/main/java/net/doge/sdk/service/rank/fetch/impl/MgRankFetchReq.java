package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgRankFetchReq {
    private static MgRankFetchReq instance;

    private MgRankFetchReq() {
    }

    public static MgRankFetchReq getInstance() {
        if (instance == null) instance = new MgRankFetchReq();
        return instance;
    }

    // 获取榜单 API (咪咕)
    private final String GET_RANK_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/rank-list";
    private final String GET_RANK_MG_API_V2 = "https://app.c.nf.migu.cn/pc/bmw/rank/rank-index/v1.0";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = HttpRequest.get(GET_RANK_MG_API)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("data");
        JSONArray contentItemList = data.getJSONArray("contentItemList");
        for (int i = 0, len = contentItemList.size(); i < len; i++) {
            JSONArray itemList = contentItemList.getJSONObject(i).getJSONArray("itemList");
            if (JsonUtil.notEmpty(itemList)) {
                for (int j = 0, s = itemList.size(); j < s; j++) {
                    JSONObject item = itemList.getJSONObject(j);

                    String template = item.getString("template");
                    if (template.equals("row1") || template.equals("grid1")) {
                        JSONObject param = item.getJSONObject("displayLogId").getJSONObject("param");

                        String rankId = param.getString("rankId");
                        String rankName = param.getString("rankName");
                        String coverImgUrl = item.getString("imageUrl");
                        String updateFre = item.getJSONArray("barList").getJSONObject(0).getString("title");

                        NetRankInfo rankInfo = new NetRankInfo();
                        rankInfo.setSource(NetResourceSource.MG);
                        rankInfo.setId(rankId);
                        rankInfo.setName(rankName);
                        rankInfo.setUpdateFre(updateFre);
                        rankInfo.setCoverImgUrl(coverImgUrl);
//                        rankInfo.setPlayCount(playCount);
//                        rankInfo.setUpdateTime(updateTime);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                            rankInfo.setCoverImgThumb(coverImgThumb);
                        });

                        r.add(rankInfo);
                    }
                }
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanksV2() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = HttpRequest.get(GET_RANK_MG_API_V2)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("data");
        JSONArray contents = data.getJSONArray("contents");
        for (int i = 0, len = contents.size(); i < len; i++) {
            JSONObject content = contents.getJSONObject(i);
            JSONArray subContents = content.getJSONArray("contents");
            if (JsonUtil.notEmpty(subContents)) {
                for (int j = 0, s = subContents.size(); j < s; j++) {
                    JSONObject rank = subContents.getJSONObject(j);

                    String rankId = rank.getString("rankId");
                    String rankName = rank.getString("rankName");
                    String coverImgUrl = rank.getString("imageUrl");
                    String updateFre = rank.getString("desc");
                    if (StringUtil.isEmpty(updateFre)) updateFre = content.getString("desc");

                    NetRankInfo rankInfo = new NetRankInfo();
                    rankInfo.setSource(NetResourceSource.MG);
                    rankInfo.setId(rankId);
                    rankInfo.setName(rankName);
                    rankInfo.setUpdateFre(updateFre);
                    rankInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                        rankInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(rankInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
