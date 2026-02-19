package net.doge.sdk.service.radio.rcmd.impl.hotradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcHotRadioReq {
    private static NcHotRadioReq instance;

    private NcHotRadioReq() {
    }

    public static NcHotRadioReq getInstance() {
        if (instance == null) instance = new NcHotRadioReq();
        return instance;
    }

    //    // 个性电台推荐 API (网易云)
//    private final String PERSONAL_RADIO_NC_API = prefix + "/dj/personalize/recommend";
    // 今日优选电台 API (网易云)
    private final String DAILY_RADIO_NC_API = "https://music.163.com/weapi/djradio/home/today/perfered";
    // 热门电台 API (网易云)
    private final String HOT_RADIO_NC_API = "https://music.163.com/weapi/djradio/hot/v1";
    // 热门电台榜 API (网易云)
    private final String RADIO_TOPLIST_NC_API = "https://music.163.com/api/djradio/toplist";
    // 推荐电台 API (网易云)
    private final String RECOMMEND_RADIO_NC_API = "https://music.163.com/weapi/djradio/recommend/v1";
    // 分类热门电台 API (网易云)
    private final String CAT_HOT_RADIO_NC_API = "https://music.163.com/api/djradio/hot";
    // 分类推荐电台 API (网易云)
    private final String CAT_REC_RADIO_NC_API = "https://music.163.com/weapi/djradio/recommend";

//    /**
//     * 个性电台推荐
//     */
//    public CommonResult<NetRadioInfo> getPersonalRadios() {
    //        String radioInfoBody = HttpRequest.get(PERSONAL_RADIO_NC_API)
//                .executeAsync()
//                .body();
//        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
//        JSONArray radioArray = radioInfoJson.getJSONArray("data");
//        for (int i = 0, len = radioArray.size(); i < len; i++) {
//            JSONObject radioJson = radioArray.getJSONObject(i);
//
//            long radioId = radioJson.getLong("id");
//            String radioName = radioJson.getString("name");
//
//            NetRadioInfo radioInfo = new NetRadioInfo();
//            radioInfo.setId(radioId);
//            radioInfo.setName(radioName);
//            res.add(radioInfo);
//        }
//    }

    /**
     * 今日优选电台
     */
    public CommonResult<NetRadioInfo> getDailyRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, DAILY_RADIO_NC_API, "{\"page\":0}", options)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("data");
        t = radioArray.size();
        for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = null;
//                if (i >= rs) dj = radioJson.getJSONObject("dj").getString("nickname");
            Long playCount = radioJson.getLong("playCount");
            Integer trackCount = radioJson.getIntValue("programCount");
            String category = radioJson.getString("category");
            String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.getLongValue("createTime");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 热门电台
     */
    public CommonResult<NetRadioInfo> getHotRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, HOT_RADIO_NC_API, "{\"offset\":0,\"limit\":1000}", options)
                .executeAsStr();
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

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setDjId(djId);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 热门电台榜
     */
    public CommonResult<NetRadioInfo> getRadiosRank(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_TOPLIST_NC_API, "{\"type\":1,\"offset\":0,\"limit\":200}", options)
                .executeAsStr();
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

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setDjId(djId);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 推荐电台
     */
    public CommonResult<NetRadioInfo> getRecRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_RADIO_NC_API, "{}", options)
                .executeAsStr();
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

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setDjId(djId);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类热门电台
     */
    public CommonResult<NetRadioInfo> getCatHotRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, CAT_HOT_RADIO_NC_API,
                            String.format("{\"cateId\":\"%s\",\"offset\":%s,\"limit\":%s}", s[0], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            t = radioInfoJson.getIntValue("count");
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);
                JSONObject djJson = radioJson.getJSONObject("dj");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = djJson.getString("nickname");
                String djId = djJson.getString("userId");
//                    Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类推荐电台
     */
    public CommonResult<NetRadioInfo> getCatRecRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, CAT_REC_RADIO_NC_API, String.format("{\"cateId\":\"%s\"}", s[1]), options)
                    .executeAsStr();
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
//                    Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
