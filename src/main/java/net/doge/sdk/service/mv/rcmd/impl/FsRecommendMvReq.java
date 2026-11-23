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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FsRecommendMvReq {
    private static FsRecommendMvReq instance;

    private FsRecommendMvReq() {
    }

    public static FsRecommendMvReq getInstance() {
        if (instance == null) instance = new FsRecommendMvReq();
        return instance;
    }

    // 推荐 MV API (5sing)
    private final String RECOMMEND_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew";
    // 最热 MV API (5sing)
    private final String HOT_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew";
    // 最新 MV API (5sing)
    private final String NEW_MV_FS_API = "http://service.5sing.kugou.com/mv/listNew";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("type", 3);
        params.put("sortType", 2);
        params.put("pageIndex", page);
        params.put("pageSize", limit);
        String mvInfoBody = HttpRequest.get(RECOMMEND_MV_FS_API)
                .queryParams(params)
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

        Map<String, Object> params = new HashMap<>();
        params.put("type", 2);
        params.put("sortType", 2);
        params.put("pageIndex", page);
        params.put("pageSize", limit);
        String mvInfoBody = HttpRequest.get(HOT_MV_FS_API)
                .queryParams(params)
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

        Map<String, Object> params = new HashMap<>();
        params.put("type", 2);
        params.put("sortType", 1);
        params.put("pageIndex", page);
        params.put("pageSize", limit);
        String mvInfoBody = HttpRequest.get(NEW_MV_FS_API)
                .queryParams(params)
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
