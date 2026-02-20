package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class FsRecommendMvReq {
    private static FsRecommendMvReq instance;

    private FsRecommendMvReq() {
    }

    public static FsRecommendMvReq getInstance() {
        if (instance == null) instance = new FsRecommendMvReq();
        return instance;
    }

    // 推荐 MV API (5sing)
    private final String RECOMMEND_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=3&sortType=2&pageIndex=%s&pageSize=%s";
    // 最热 MV API (5sing)
    private final String HOT_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=2&sortType=2&pageIndex=%s&pageSize=%s";
    // 最新 MV API (5sing)
    private final String NEW_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew?type=2&sortType=1&pageIndex=%s&pageSize=%s";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_FS_API, page, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject user = mvJson.getJSONObject("user");

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("title");
            String artistName = user.getString("NN");
            String creatorId = user.getString("ID");
            Long playCount = mvJson.getLong("play");
            String coverImgUrl = mvJson.getString("cover_url");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.FS);
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
     * 最热 MV
     */
    public CommonResult<NetMvInfo> getHotMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(HOT_MV_FS_API, page, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject user = mvJson.getJSONObject("user");

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("title");
            String artistName = user.getString("NN");
            String creatorId = user.getString("ID");
            Long playCount = mvJson.getLong("play");
            String coverImgUrl = mvJson.getString("cover_url");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.FS);
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
     * 最新 MV
     */
    public CommonResult<NetMvInfo> getNewMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.get(String.format(NEW_MV_FS_API, page, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject user = mvJson.getJSONObject("user");

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("title");
            String artistName = user.getString("NN");
            String creatorId = user.getString("ID");
            Long playCount = mvJson.getLong("play");
            String coverImgUrl = mvJson.getString("cover_url");
            Double duration = mvJson.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("create_time") * 1000);

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.FS);
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
}
