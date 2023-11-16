package net.doge.sdk.entity.ranking.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetRankingInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RankingSearchReq {
    private static RankingSearchReq instance;

    private RankingSearchReq() {
    }

    public static RankingSearchReq getInstance() {
        if (instance == null) instance = new RankingSearchReq();
        return instance;
    }
    
    // 获取榜单 API
    private final String GET_RANKING_API = "https://music.163.com/api/toplist";
    // 获取榜单 API (酷狗)
    private final String GET_RANKING_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/list?apiver=6&area_code=1";
    // 获取榜单 API (QQ)
    private final String GET_RANKING_QQ_API = "https://u.y.qq.com/cgi-bin/musicu.fcg?_=1577086820633&data={%22comm%22:{%22g_tk%22:5381,%22uin%22:123456," +
            "%22format%22:%22json%22,%22inCharset%22:%22utf-8%22,%22outCharset%22:%22utf-8%22,%22notice%22:0,%22platform%22:%22h5%22,%22needNewCode%22:1," +
            "%22ct%22:23,%22cv%22:0},%22topList%22:{%22module%22:%22musicToplist.ToplistInfoServer%22,%22method%22:%22GetAll%22,%22param%22:{}}}";
    // 获取榜单 API 2 (QQ)
    private final String GET_RANKING_QQ_API_2
            = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=1928093487&inCharset=utf-8&outCharset=utf-8&notice=0&format=json&uin=0&needNewCode=1&platform=h5";
    private final String GET_RANKING_KW_API = "http://www.kuwo.cn/api/www/bang/bang/bangMenu?&httpsStatus=1";
    // 获取榜单 API 2 (酷我)
    private final String GET_RANKING_KW_API_2 = "http://qukudata.kuwo.cn/q.k?op=query&cont=tree&node=2&pn=0&rn=1000&fmt=json&level=2";
    // 获取推荐榜单 API (酷我)
//    private final String GET_REC_RANKING_KW_API = "http://www.kuwo.cn/api/www/bang/index/bangList?&httpsStatus=1";
    // 获取榜单 API (咪咕)
    private final String GET_RANKING_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/rank-list";
    // 获取榜单 API (千千)
    private final String GET_RANKING_QI_API = "https://music.91q.com/v1/bd/category?appid=16073360&timestamp=%s";
    // 获取榜单 API (猫耳)
    private final String GET_RANKING_ME_API = "https://www.missevan.com/mobileWeb/albumList";

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings(int src) {
        AtomicInteger total = new AtomicInteger();
        List<NetRankingInfo> res = new LinkedList<>();

        // 网易云
        Callable<CommonResult<NetRankingInfo>> getRankings = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String rankingInfoBody = SdkCommon.ncRequest(Method.POST, GET_RANKING_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("list");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("name");
                String coverImgUrl = rankingJson.getString("coverImgUrl");
                String desc = rankingJson.getString("description");
                String description = StringUtil.isEmpty(desc) ? "" : desc;
                Long playCount = rankingJson.getLong("playCount");
                String updateFre = rankingJson.getString("updateFrequency");
                String updateTime = TimeUtil.msToDate(rankingJson.getLong("trackUpdateTime"));

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description);
                rankingInfo.setPlayCount(playCount);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setUpdateTime(updateTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        Callable<CommonResult<NetRankingInfo>> getRankingsKg = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(GET_RANKING_KG_API)
                    .executeAsync()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONObject("data").getJSONArray("info");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("rankid");
                String rankingName = rankingJson.getString("rankname");
                String coverImgUrl = rankingJson.getString("banner_9").replace("/{size}", "");
                String description = rankingJson.getString("intro");
                String updateFre = rankingJson.getString("update_frequency");
                Long playCount = rankingJson.getLong("play_times");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.KG);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        Callable<CommonResult<NetRankingInfo>> getRankingsQq = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(GET_RANKING_QQ_API)
                    .executeAsync()
                    .body();
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
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsQq2 = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(GET_RANKING_QQ_API_2)
                    .executeAsync()
                    .body();
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
        };

        // 酷我
        // 所有榜单
        Callable<CommonResult<NetRankingInfo>> getRankingsKw = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(GET_RANKING_KW_API).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
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
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsKw2 = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(GET_RANKING_KW_API_2).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
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
        };
        // 推荐榜单
//        Callable<CommonResult<NetRankingInfo>> getRecRankingsKw = () -> {
//            List<NetRankingInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = kwRequest(GET_REC_RANKING_KW_API)
//                    .header(Header.REFERER, "http://www.kuwo.cn/rankList")
//                    .executeAsync();
//            if (resp.getStatus() == HttpStatus.HTTP_OK) {
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
//        };

        // 咪咕
        Callable<CommonResult<NetRankingInfo>> getRankingsMg = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(GET_RANKING_MG_API)
                    .executeAsync()
                    .body();
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
        };

        // 千千
        Callable<CommonResult<NetRankingInfo>> getRankingsQi = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = SdkCommon.qiRequest(String.format(GET_RANKING_QI_API, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("data");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("bdid");
                String rankingName = rankingJson.getString("title");
                String coverImgUrl = rankingJson.getString("pic");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.QI);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription("");
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        Callable<CommonResult<NetRankingInfo>> getRankingsMe = () -> {
            List<NetRankingInfo> r = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(GET_RANKING_ME_API)
                    .executeAsync()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("info");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i).getJSONObject("album");

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("title");
                String coverImgUrl = rankingJson.getString("front_cover");
                String updateTime = TimeUtil.msToDate(rankingJson.getLong("last_update_time") * 1000);
                Long playCount = rankingJson.getLong("view_count");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.ME);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setUpdateTime(updateTime);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(rankingInfo);
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetRankingInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankings));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq2));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw2));
//        taskList.add(GlobalExecutors.requestExecutor.submit(getRecRankingsKw));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsMg));
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQi));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsMe));

        List<List<NetRankingInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRankingInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get());
    }
}
