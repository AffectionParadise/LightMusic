package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwRankFetchReq {
    private static KwRankFetchReq instance;

    private KwRankFetchReq() {
    }

    public static KwRankFetchReq getInstance() {
        if (instance == null) instance = new KwRankFetchReq();
        return instance;
    }

    // 获取榜单 API (酷我)
    private final String GET_RANK_KW_API = "https://kuwo.cn/api/www/bang/bang/bangMenu?&httpsStatus=1";
    private final String GET_RANK_KW_API_V2 = "http://qukudata.kuwo.cn/q.k?op=query&cont=tree&node=2&pn=0&rn=1000&fmt=json&level=2";
    // 获取推荐榜单 API (酷我)
//    private final String GET_REC_RANK_KW_API = "https://kuwo.cn/api/www/bang/index/bangList?&httpsStatus=1";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = SdkCommon.kwRequest(GET_RANK_KW_API).execute();
        if (resp.isSuccessful()) {
            String rankInfoBody = resp.body();
            JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
            JSONArray data = rankInfoJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONArray rankArray = data.getJSONObject(i).getJSONArray("list");
                for (int j = 0, s = rankArray.size(); j < s; j++) {
                    JSONObject rankJson = rankArray.getJSONObject(j);

                    String rankId = rankJson.getString("sourceid");
                    String rankName = rankJson.getString("name");
                    String coverImgUrl = rankJson.getString("pic");
                    String description = rankJson.getString("intro");
                    String updateFre = rankJson.getString("pub");

                    NetRankInfo rankInfo = new NetRankInfo();
                    rankInfo.setSource(NetMusicSource.KW);
                    rankInfo.setId(rankId);
                    rankInfo.setName(rankName);
                    rankInfo.setCoverImgUrl(coverImgUrl);
                    rankInfo.setUpdateFre(updateFre);
                    rankInfo.setDescription(description);
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

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanksV2() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = HttpRequest.get(GET_RANK_KW_API_V2).execute();
        if (resp.isSuccessful()) {
            String rankInfoBody = resp.body();
            JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
            JSONArray data = rankInfoJson.getJSONArray("child");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject rankJson = data.getJSONObject(i);

                String rankId = rankJson.getString("sourceid");
                String rankName = rankJson.getString("name");
                String coverImgUrl = rankJson.getString("pic");
                String updateTime = rankJson.getString("info").replaceFirst("更新于", "");

                NetRankInfo rankInfo = new NetRankInfo();
                rankInfo.setSource(NetMusicSource.KW);
                rankInfo.setId(rankId);
                rankInfo.setName(rankName);
                rankInfo.setCoverImgUrl(coverImgUrl);
                rankInfo.setUpdateTime(updateTime);
                rankInfo.setDescription("");
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

//    /**
//     * 获取所有榜单
//     */
//    public CommonResult<NetRankInfo> getRecRanks() {
//        List<NetRankInfo> r = new LinkedList<>();
//        int t = 0;
//
    //            HttpResponse resp = kwRequest(GET_REC_RANK_KW_API)
//                    .header(Header.REFERER, "https://kuwo.cn/rankList")
//                    .executeAsync();
//            if (resp.isSuccessful()) {
//                String rankInfoBody = resp.body();
//                JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
//                JSONArray data = rankInfoJson.getJSONArray("data");
//                for (int i = 0, len = data.size(); i < len; i++) {
//                    JSONObject rankJson = data.getJSONObject(i);
//
//                    String rankId = rankJson.getString("id");
//                    String rankName = rankJson.getString("name");
//                    String coverImgUrl = rankJson.getString("pic");
//                    String updateTime = rankJson.getString("pub");
//                    String desc = "";
//
//                    NetRankInfo rankInfo = new NetRankInfo();
//                    rankInfo.setSource(NetMusicSource.KW);
//                    rankInfo.setId(rankId);
//                    rankInfo.setName(rankName);
//                    rankInfo.setCoverImgUrl(coverImgUrl);
//                    rankInfo.setUpdateTime(updateTime);
//                    rankInfo.setDescription(desc);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
//                        rankInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    r.add(rankInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//    }
}
