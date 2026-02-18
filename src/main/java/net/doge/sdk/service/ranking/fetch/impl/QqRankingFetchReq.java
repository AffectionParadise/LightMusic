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

public class QqRankingFetchReq {
    private static QqRankingFetchReq instance;

    private QqRankingFetchReq() {
    }

    public static QqRankingFetchReq getInstance() {
        if (instance == null) instance = new QqRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (QQ)
    private final String GET_RANKING_QQ_API = "https://u.y.qq.com/cgi-bin/musicu.fcg?_=1577086820633&data={%22comm%22:{%22g_tk%22:5381,%22uin%22:123456," +
            "%22format%22:%22json%22,%22inCharset%22:%22utf-8%22,%22outCharset%22:%22utf-8%22,%22notice%22:0,%22platform%22:%22h5%22,%22needNewCode%22:1," +
            "%22ct%22:23,%22cv%22:0},%22topList%22:{%22module%22:%22musicToplist.ToplistInfoServer%22,%22method%22:%22GetAll%22,%22param%22:{}}}";
    // 获取榜单 API (QQ)
    private final String GET_RANKING_QQ_API_2
            = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=1928093487&inCharset=utf-8&outCharset=utf-8&notice=0&format=json&uin=0&needNewCode=1&platform=h5";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = HttpRequest.get(GET_RANKING_QQ_API)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray group = rankingInfoJson.getJSONObject("topList").getJSONObject("data").getJSONArray("group");
        for (int i = 0, len = group.size(); i < len; i++) {
            JSONArray rankingArray = group.getJSONObject(i).getJSONArray("toplist");
            for (int j = 0, s = rankingArray.size(); j < s; j++) {
                JSONObject rankingJson = rankingArray.getJSONObject(j);

                String rankingId = rankingJson.getString("topId");
                String rankingName = rankingJson.getString("title");
                String coverImgUrl = rankingJson.getString("headPicUrl").replaceFirst("http:", "https:");
                Long playCount = rankingJson.getLong("listenNum");
                String updateTime = rankingJson.getString("updateTime");
                String updateFre = rankingJson.getString("updateTips");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.QQ);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setPlayCount(playCount);
                rankingInfo.setUpdateTime(updateTime);
                rankingInfo.setUpdateFre(updateFre);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings2() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        String rankingInfoBody = HttpRequest.get(GET_RANKING_QQ_API_2)
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONArray data = rankingInfoJson.getJSONObject("data").getJSONArray("topList");
        for (int i = 0, len = data.size(); i < len; i++) {
            JSONObject rankingJson = data.getJSONObject(i);

            String rankingId = rankingJson.getString("id");
            String rankingName = rankingJson.getString("topTitle");
            String coverImgUrl = rankingJson.getString("picUrl").replaceFirst("http:", "https:");
            Long playCount = rankingJson.getLong("listenCount");

            NetRankingInfo rankingInfo = new NetRankingInfo();
            rankingInfo.setSource(NetMusicSource.QQ);
            rankingInfo.setId(rankingId);
            rankingInfo.setName(rankingName);
            rankingInfo.setCoverImgUrl(coverImgUrl);
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
