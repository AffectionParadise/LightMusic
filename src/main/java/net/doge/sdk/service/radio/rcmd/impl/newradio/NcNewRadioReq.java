package net.doge.sdk.service.radio.rcmd.impl.newradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcNewRadioReq {
    private static NcNewRadioReq instance;

    private NcNewRadioReq() {
    }

    public static NcNewRadioReq getInstance() {
        if (instance == null) instance = new NcNewRadioReq();
        return instance;
    }

    // 新晋电台 API (网易云)
    private final String NEW_RADIO_NC_API = "https://music.163.com/api/djradio/toplist";
    // 推荐个性电台 API (网易云)
    private final String PERSONALIZED_RADIO_NC_API = "https://music.163.com/weapi/personalized/djprogram";
    // 推荐电台 API (网易云)
    private final String RECOMMEND_RADIO_NC_API = "https://music.163.com/weapi/djradio/recommend/v1";
    // 付费精品电台 API (网易云)
    private final String PAY_RADIO_NC_API = "https://music.163.com/api/djradio/toplist/pay";
    // 付费精选电台 API (网易云)
    private final String PAY_GIFT_RADIO_NC_API = "https://music.163.com/weapi/djradio/home/paygift/list?_nmclfl=1";

    /**
     * 新晋电台榜
     */
    public CommonResult<NetRadioInfo> getNewRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, NEW_RADIO_NC_API, "{\"type\":0,\"offset\":0,\"limit\":200}", options)
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
    }

    /**
     * 推荐个性电台
     */
    public CommonResult<NetRadioInfo> getPersonalizedRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, PERSONALIZED_RADIO_NC_API, "{}", options)
                .executeAsStr();
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
    }

    /**
     * 推荐电台
     */
    public CommonResult<NetRadioInfo> getRecommendRadios(int page, int limit) {
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
    }

    /**
     * 付费精品电台
     */
    public CommonResult<NetRadioInfo> getPayRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, PAY_RADIO_NC_API, "{\"limit\":100}", options)
                .executeAsStr();
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
    }

    /**
     * 付费精选电台
     */
    public CommonResult<NetRadioInfo> getPayGiftRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, PAY_GIFT_RADIO_NC_API,
                        String.format("{\"offset\":%s,\"limit\":%s}", (page - 1) * limit, limit), options)
                .executeAsStr();
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
    }
}
