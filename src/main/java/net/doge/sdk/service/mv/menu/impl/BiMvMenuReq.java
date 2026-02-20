package net.doge.sdk.service.mv.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiMvMenuReq {
    private static BiMvMenuReq instance;

    private BiMvMenuReq() {
    }

    public static BiMvMenuReq getInstance() {
        if (instance == null) instance = new BiMvMenuReq();
        return instance;
    }

    // 相似视频 API (哔哩哔哩)
    private final String SIMILAR_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/archive/related?bvid=%s";
    // 视频分集 API (哔哩哔哩)
    private final String VIDEO_EPISODES_BI_API = "https://api.bilibili.com/x/player/pagelist?bvid=%s";

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        List<NetMvInfo> res = new LinkedList<>();
        int t;

        String bvid = netMvInfo.getBvId();
        String mvInfoBody = HttpRequest.get(String.format(SIMILAR_VIDEO_BI_API, bvid))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONArray mvArray = JSONObject.parseObject(mvInfoBody).getJSONArray("data");
        t = mvArray.size();
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject owner = mvJson.getJSONObject("owner");

            String mvId = mvJson.getString("cid");
            String bvId = mvJson.getString("bvid");
            String mvName = mvJson.getString("title");
            String artistName = owner.getString("name");
            String creatorId = owner.getString("mid");
            String coverImgUrl = mvJson.getString("pic");
            Long playCount = mvJson.getJSONObject("stat").getLong("view");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.BI);
            mvInfo.setId(mvId);
            mvInfo.setBvId(bvId);
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

            res.add(mvInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取视频分集
     *
     * @return
     */
    public CommonResult<NetMvInfo> getVideoEpisodes(NetMvInfo netMvInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int t = 0;

        String bvid = netMvInfo.getBvId();
        String mvInfoBody = HttpRequest.get(String.format(VIDEO_EPISODES_BI_API, bvid))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONArray mvArray = JSONObject.parseObject(mvInfoBody).getJSONArray("data");
        if (JsonUtil.notEmpty(mvArray)) {
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(page * limit, mvArray.size()); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("cid");
                String mvName = mvJson.getString("part");
                String artistName = netMvInfo.getArtist();
                String creatorId = netMvInfo.getCreatorId();
                String coverImgUrl = netMvInfo.getCoverImgUrl();
                Long playCount = netMvInfo.getPlayCount();
                Double duration = mvJson.getDouble("duration");
                String pubTime = netMvInfo.getPubTime();

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.BI);
                mvInfo.setId(mvId);
                mvInfo.setBvId(bvid);
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

                res.add(mvInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
