package net.doge.sdk.service.ranking.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwRankingFetchReq {
    private static KwRankingFetchReq instance;

    private KwRankingFetchReq() {
    }

    public static KwRankingFetchReq getInstance() {
        if (instance == null) instance = new KwRankingFetchReq();
        return instance;
    }

    // 获取榜单 API (酷我)
    private final String GET_RANKING_KW_API = "https://kuwo.cn/api/www/bang/bang/bangMenu?&httpsStatus=1";
    private final String GET_RANKING_KW_API_2 = "http://qukudata.kuwo.cn/q.k?op=query&cont=tree&node=2&pn=0&rn=1000&fmt=json&level=2";
    // 获取推荐榜单 API (酷我)
//    private final String GET_REC_RANKING_KW_API = "https://kuwo.cn/api/www/bang/index/bangList?&httpsStatus=1";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings() {
        List<NetRankingInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = SdkCommon.kwRequest(GET_RANKING_KW_API).execute();
        if (resp.isSuccessful()) {
            String rankingInfoBody = resp.body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONArray rankingArray = data.getJSONObject(i).getJSONArray("list");
                for (int j = 0, s = rankingArray.size(); j < s; j++) {
                    JSONObject rankingJson = rankingArray.getJSONObject(j);

                    String rankingId = rankingJson.getString("sourceid");
                    String rankingName = rankingJson.getString("name");
                    String coverImgUrl = rankingJson.getString("pic");
                    String description = rankingJson.getString("intro");
                    String updateFre = rankingJson.getString("pub");

                    NetRankingInfo rankingInfo = new NetRankingInfo();
                    rankingInfo.setSource(NetMusicSource.KW);
                    rankingInfo.setId(rankingId);
                    rankingInfo.setName(rankingName);
                    rankingInfo.setCoverImgUrl(coverImgUrl);
                    rankingInfo.setUpdateFre(updateFre);
                    rankingInfo.setDescription(description);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                        rankingInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(rankingInfo);
                }
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

        HttpResponse resp = HttpRequest.get(GET_RANKING_KW_API_2).execute();
        if (resp.isSuccessful()) {
            String rankingInfoBody = resp.body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONArray("child");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject rankingJson = data.getJSONObject(i);

                String rankingId = rankingJson.getString("sourceid");
                String rankingName = rankingJson.getString("name");
                String coverImgUrl = rankingJson.getString("pic");
                String updateTime = rankingJson.getString("info").replaceFirst("更新于", "");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.KW);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setUpdateTime(updateTime);
                rankingInfo.setDescription("");
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

//    /**
//     * 获取所有榜单
//     */
//    public CommonResult<NetRankingInfo> getRecRankings() {
//        List<NetRankingInfo> r = new LinkedList<>();
//        int t = 0;
//
    //            HttpResponse resp = kwRequest(GET_REC_RANKING_KW_API)
//                    .header(Header.REFERER, "https://kuwo.cn/rankList")
//                    .executeAsync();
//            if (resp.isSuccessful()) {
//                String rankingInfoBody = resp.body();
//                JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
//                JSONArray data = rankingInfoJson.getJSONArray("data");
//                for (int i = 0, len = data.size(); i < len; i++) {
//                    JSONObject rankingJson = data.getJSONObject(i);
//
//                    String rankingId = rankingJson.getString("id");
//                    String rankingName = rankingJson.getString("name");
//                    String coverImgUrl = rankingJson.getString("pic");
//                    String updateTime = rankingJson.getString("pub");
//                    String desc = "";
//
//                    NetRankingInfo rankingInfo = new NetRankingInfo();
//                    rankingInfo.setSource(NetMusicSource.KW);
//                    rankingInfo.setId(rankingId);
//                    rankingInfo.setName(rankingName);
//                    rankingInfo.setCoverImgUrl(coverImgUrl);
//                    rankingInfo.setUpdateTime(updateTime);
//                    rankingInfo.setDescription(desc);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
//                        rankingInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    r.add(rankingInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//    }
}
