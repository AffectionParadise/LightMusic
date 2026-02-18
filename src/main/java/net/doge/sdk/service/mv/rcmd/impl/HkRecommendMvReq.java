package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.LangUtil;
import net.doge.util.core.time.TimeUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkRecommendMvReq {
    private static HkRecommendMvReq instance;

    private HkRecommendMvReq() {
    }

    public static HkRecommendMvReq getInstance() {
        if (instance == null) instance = new HkRecommendMvReq();
        return instance;
    }

    // 猜你喜欢视频 API (好看)
    private final String GUESS_VIDEO_HK_API = "https://haokan.baidu.com/videoui/api/Getvideolandfeed?time=%s";
    // 榜单视频 API (好看)
    private final String TOP_VIDEO_HK_API
            = "https://haokan.baidu.com/videoui/page/pc/toplist?type=hotvideo&sfrom=haokan_web_banner&page=%s&pageSize=%s&_format=json";
    // 推荐视频 API (好看)
    private final String RECOMMEND_VIDEO_HK_API = "https://haokan.baidu.com/web/video/feed?tab=%s&act=pcFeed&pd=pc&num=%s&shuaxin_id=1661766211525";

    /**
     * 猜你喜欢视频
     */
    public CommonResult<NetMvInfo> getGuessVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(GUESS_VIDEO_HK_API, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        JSONArray mvArray = data.getJSONArray("apiData");
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("title");
            String artistName = mvJson.getString("author");
//                String creatorId = mvJson.getString("author_id");
            Long playCount = LangUtil.parseNumber(mvJson.getString("fmplaycnt"));
            String coverImgUrl = "https:" + mvJson.getString("poster");
            Double duration = DurationUtil.toSeconds(mvJson.getString("time_length"));
            String pubTime = TimeUtil.msToDate(mvJson.getLong("publish_time") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetMusicSource.HK);
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
//                mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(mvInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 榜单视频
     */
    public CommonResult<NetMvInfo> getTopVideo(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(TOP_VIDEO_HK_API, page, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("apiData").getJSONObject("response");
        t = data.getIntValue("total_page") * limit;
        JSONArray mvArray = data.getJSONArray("video");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("vid");
            String mvName = mvJson.getString("title");
            String artistName = mvJson.getString("author");
            String creatorId = mvJson.getString("third_id");
            Long playCount = mvJson.getLong("hot");
            String coverImgUrl = mvJson.getString("poster");
            Double duration = mvJson.getDouble("duration");
            String pubTime = mvJson.getString("publish_time").replaceAll("[发布时间：日]", "").replaceAll("年|月", "-");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetMusicSource.HK);
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(mvInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类推荐视频
     */
    public CommonResult<NetMvInfo> getRecommendVideo(String tag, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTag.get(tag);

        if (StringUtil.notEmpty(s[8])) {
            String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_VIDEO_HK_API, s[8], limit))
                    .cookie(SdkCommon.HK_COOKIE)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data").getJSONObject("response");
            JSONArray mvArray = data.getJSONArray("videos");
            t = limit;
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("source_name");
                String creatorId = mvJson.getString("third_id");
                Long playCount = mvJson.getLong("playcnt");
                String coverImgUrl = mvJson.getString("poster_pc");
                Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}