package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class HkUserMenuReq {
    private static HkUserMenuReq instance;

    private HkUserMenuReq() {
    }

    public static HkUserMenuReq getInstance() {
        if (instance == null) instance = new HkUserMenuReq();
        return instance;
    }

    // 用户视频 API (好看)
    private final String USER_VIDEO_HK_API = "https://haokan.baidu.com/web/author/listall?app_id=%s&rn=20&ctime=%s";
    // 用户小视频 API (好看)
    private final String USER_SMALL_VIDEO_HK_API = "https://haokan.baidu.com/web/author/listall?app_id=%s&rn=20&video_type=haokan|tabhubVideo";

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int page, int limit, String cursor) {
        String id = userInfo.getId();
        // 普通视频
        Callable<CommonResult<NetMvInfo>> getNormalVideos = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            int t;

            String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_HK_API, id, cursor))
                    .cookie(SdkCommon.HK_COOKIE)
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
            String cs = data.getString("ctime");
            t = data.getIntValue("has_more") == 0 ? page * limit : (page + 1) * limit;
            JSONArray mvArray = data.getJSONArray("results");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = userInfo.getName();
                String creatorId = userInfo.getId();
                String coverImgUrl = mvJson.getString("poster");
                Long playCount = mvJson.getLong("playcnt");
                Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }

            return new CommonResult<>(r, t, cs);
        };
        // 小视频
        Callable<CommonResult<NetMvInfo>> getSmallVideos = () -> {
            List<NetMvInfo> r = new LinkedList<>();
            int t;

            String mvInfoBody = HttpRequest.get(String.format(USER_SMALL_VIDEO_HK_API, id))
                    .cookie(SdkCommon.HK_COOKIE)
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
            String cs = data.getString("ctime");
            t = data.getBoolean("has_more") ? (page + 1) * limit : page * limit;
            JSONArray mvArray = data.getJSONArray("results");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = userInfo.getName();
                String creatorId = userInfo.getId();
                String coverImgUrl = mvJson.getString("poster");
                Long playCount = mvJson.getLong("playcnt");
                Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }

            return new CommonResult<>(r, t, cs);
        };

        MultiCommonResultCallableExecutor<NetMvInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getNormalVideos);
        executor.submit(getSmallVideos);
        return executor.getResult();
    }
}
