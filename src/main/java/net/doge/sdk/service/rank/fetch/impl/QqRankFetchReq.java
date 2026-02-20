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

public class QqRankFetchReq {
    private static QqRankFetchReq instance;

    private QqRankFetchReq() {
    }

    public static QqRankFetchReq getInstance() {
        if (instance == null) instance = new QqRankFetchReq();
        return instance;
    }

    // 获取榜单 API (QQ)
    private final String GET_RANK_QQ_API = "https://u.y.qq.com/cgi-bin/musicu.fcg?_=1577086820633&data={%22comm%22:{%22g_tk%22:5381,%22uin%22:123456," +
            "%22format%22:%22json%22,%22inCharset%22:%22utf-8%22,%22outCharset%22:%22utf-8%22,%22notice%22:0,%22platform%22:%22h5%22,%22needNewCode%22:1," +
            "%22ct%22:23,%22cv%22:0},%22topList%22:{%22module%22:%22musicToplist.ToplistInfoServer%22,%22method%22:%22GetAll%22,%22param%22:{}}}";
    // 获取榜单 API (QQ)
    private final String GET_RANK_QQ_API_V2
            = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=1928093487&inCharset=utf-8&outCharset=utf-8&notice=0&format=json&uin=0&needNewCode=1&platform=h5";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        String rankInfoBody = HttpRequest.get(GET_RANK_QQ_API)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray group = rankInfoJson.getJSONObject("topList").getJSONObject("data").getJSONArray("group");
        for (int i = 0, len = group.size(); i < len; i++) {
            JSONArray rankArray = group.getJSONObject(i).getJSONArray("toplist");
            for (int j = 0, s = rankArray.size(); j < s; j++) {
                JSONObject rankJson = rankArray.getJSONObject(j);

                String rankId = rankJson.getString("topId");
                String rankName = rankJson.getString("title");
                String coverImgUrl = rankJson.getString("headPicUrl").replaceFirst("http:", "https:");
                Long playCount = rankJson.getLong("listenNum");
                String updateTime = rankJson.getString("updateTime");
                String updateFre = rankJson.getString("updateTips");

                NetRankInfo rankInfo = new NetRankInfo();
                rankInfo.setSource(NetResourceSource.QQ);
                rankInfo.setId(rankId);
                rankInfo.setName(rankName);
                rankInfo.setCoverImgUrl(coverImgUrl);
                rankInfo.setPlayCount(playCount);
                rankInfo.setUpdateTime(updateTime);
                rankInfo.setUpdateFre(updateFre);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankInfo);
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

        String rankInfoBody = HttpRequest.get(GET_RANK_QQ_API_V2)
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONArray data = rankInfoJson.getJSONObject("data").getJSONArray("topList");
        for (int i = 0, len = data.size(); i < len; i++) {
            JSONObject rankJson = data.getJSONObject(i);

            String rankId = rankJson.getString("id");
            String rankName = rankJson.getString("topTitle");
            String coverImgUrl = rankJson.getString("picUrl").replaceFirst("http:", "https:");
            Long playCount = rankJson.getLong("listenCount");

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setSource(NetResourceSource.QQ);
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
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
