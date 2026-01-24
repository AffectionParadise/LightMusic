package net.doge.sdk.service.radio.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class NewRadioReq {
    private static NewRadioReq instance;

    private NewRadioReq() {
    }

    public static NewRadioReq getInstance() {
        if (instance == null) instance = new NewRadioReq();
        return instance;
    }
    
    // 新晋电台 API
    private final String NEW_RADIO_API = "https://music.163.com/api/djradio/toplist";
    // 推荐个性电台 API
    private final String PERSONALIZED_RADIO_API = "https://music.163.com/weapi/personalized/djprogram";
    // 推荐电台 API
    private final String RECOMMEND_RADIO_API = "https://music.163.com/weapi/djradio/recommend/v1";
    // 付费精品电台 API
    private final String PAY_RADIO_API = "https://music.163.com/api/djradio/toplist/pay";
    // 付费精选电台 API
    private final String PAY_GIFT_RADIO_API = "https://music.163.com/weapi/djradio/home/paygift/list?_nmclfl=1";
    // 推荐广播剧 API (猫耳)
    private final String REC_RADIO_ME_API = "https://www.missevan.com/drama/site/recommend";
    // 夏日推荐 API (猫耳)
    private final String SUMMER_RADIO_ME_API = "https://www.missevan.com/dramaapi/summerdrama";
    // 频道 API (猫耳)
//    private final String CHANNEL_ME_API = "https://www.missevan.com/explore/channels?type=0";

    /**
     * 获取新晋电台
     */
    public CommonResult<NetRadioInfo> getNewRadios(int src, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> res = new LinkedList<>();

        // 网易云(程序分页)
        // 新晋电台榜
        Callable<CommonResult<NetRadioInfo>> getNewRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, NEW_RADIO_API, "{\"type\":0,\"offset\":0,\"limit\":200}", options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("toplist");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);
                JSONObject djJson = radioJson.getJSONObject("dj");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = djJson.getString("nickname");
                String djId = djJson.getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 推荐个性电台
        Callable<CommonResult<NetRadioInfo>> getPersonalizedRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, PERSONALIZED_RADIO_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("result");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject infoJson = radioArray.getJSONObject(i);
                JSONObject programJson = infoJson.getJSONObject("program");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = djJson.getString("nickname");
                String djId = djJson.getString("userId");
//                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 推荐电台
        Callable<CommonResult<NetRadioInfo>> getRecommendRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_RADIO_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);
                JSONObject djJson = radioJson.getJSONObject("dj");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = djJson.getString("nickname");
                String djId = djJson.getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 付费精品电台
        Callable<CommonResult<NetRadioInfo>> getPayRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, PAY_RADIO_API, "{\"limit\":100}", options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("list");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("creatorName");
                Long playCount = radioJson.getLong("score");
//                Integer trackCount = radioJson.getIntValue("programCount");
//                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
//                radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 付费精选电台
        Callable<CommonResult<NetRadioInfo>> getPayGiftRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, PAY_GIFT_RADIO_API,
                            String.format("{\"offset\":%s,\"limit\":%s}", (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("list");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
//                String dj = radioJson.getString("creatorName");
//                Long playCount = radioJson.getLong("score");
                Integer trackCount = radioJson.getIntValue("programCount");
//                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
//                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };

        // QQ(程序分页)
        Callable<CommonResult<NetRadioInfo>> getRecommendRadiosQq = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body("{\"songlist\":{\"module\":\"mb_track_radio_svr\",\"method\":\"get_radio_track\"," +
                            "\"param\":{\"id\":99,\"firstplay\":1,\"num\":15}},\"radiolist\":{\"module\":\"pf.radiosvr\"," +
                            "\"method\":\"GetRadiolist\",\"param\":{\"ct\":\"24\"}},\"comm\":{\"ct\":24,\"cv\":0}}")
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("radiolist").getJSONObject("data");
            JSONArray radioList = data.getJSONArray("radio_list");
            for (int i = 0, len = radioList.size(); i < len; i++) {
                JSONArray radioArray = radioList.getJSONObject(i).getJSONArray("list");
                for (int j = 0, l = radioArray.size(); j < l; j++, t++) {
                    if (t >= (page - 1) * limit && t < page * limit) {
                        JSONObject radioJson = radioArray.getJSONObject(j);

                        String radioId = radioJson.getString("id");
                        String radioName = radioJson.getString("title");
                        String coverImgUrl = radioJson.getString("pic_url");
                        String coverImgThumbUrl = coverImgUrl;
                        Long playCount = radioJson.getLong("listenNum");

                        NetRadioInfo radioInfo = new NetRadioInfo();
                        radioInfo.setSource(NetMusicSource.QQ);
                        radioInfo.setId(radioId);
                        radioInfo.setName(radioName);
                        radioInfo.setPlayCount(playCount);
                        // QQ 需要提前写入电台图片 url，电台信息接口不提供！
                        radioInfo.setCoverImgUrl(coverImgUrl);
                        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            radioInfo.setCoverImgThumb(coverImgThumb);
                        });

                        r.add(radioInfo);
                    }
                }
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        // 推荐广播剧
        Callable<CommonResult<NetRadioInfo>> getRecRadiosMe = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(REC_RADIO_ME_API)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("info");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = "https:" + radioJson.getString("cover");
                String description = HtmlUtil.removeHtmlLabel(radioJson.getString("abstract"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 夏日推荐
        Callable<CommonResult<NetRadioInfo>> getSummerRadiosMe = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(SUMMER_RADIO_ME_API)
                    .executeAsync()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("info");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONArray array = radioArray.getJSONArray(i);
                for (int j = 0, s = array.size(); j < s; j++, t++) {
                    if (t >= (page - 1) * limit && t < page * limit) {
                        JSONObject radioJson = array.getJSONObject(j);

                        String radioId = radioJson.getString("id");
                        String radioName = radioJson.getString("name");
                        String dj = radioJson.getString("author");
                        String coverImgThumbUrl = radioJson.getString("cover");

                        NetRadioInfo radioInfo = new NetRadioInfo();
                        radioInfo.setSource(NetMusicSource.ME);
                        radioInfo.setId(radioId);
                        radioInfo.setName(radioName);
                        radioInfo.setDj(dj);
                        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            radioInfo.setCoverImgThumb(coverImgThumb);
                        });

                        r.add(radioInfo);
                    }
                }
            }

            return new CommonResult<>(r, t);
        };
        // 频道
//        Callable<CommonResult<NetRadioInfo>> getChannelsMe = () -> {
//            List<NetRadioInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String radioInfoBody = HttpRequest.get(CHANNEL_ME_API)
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(radioInfoBody);
//            Elements radios = doc.select(".item.blk > a");
//            t = radios.size();
//            for (int i = (page - 1) * limit, len = Math.min(radios.size(), page * limit); i < len; i++) {
//                Element radio = radios.get(i);
//
//                String radioId = radio.attr("href").replace("/explore/channel/","");
//                String radioName = radio.select("b").text();
//                String coverImgThumbUrl = "https:" + radioJson.getString("cover");
//                String description = StringUtil.removeHTMLLabel(radioJson.getString("abstract"));
//
//                NetRadioInfo radioInfo = new NetRadioInfo();
//                radioInfo.setSource(NetMusicSource.ME);
//                radioInfo.setId(radioId);
//                radioInfo.setName(radioName);
//                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setDescription(description);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    radioInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(radioInfo);
//            }
//            return new CommonResult<>(r, t);
//        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getPersonalizedRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getPayRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getPayGiftRadios));
        }

        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendRadiosQq));
        }

        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecRadiosMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSummerRadiosMe));
        }

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
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
