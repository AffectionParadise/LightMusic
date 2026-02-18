package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class YyMvSearchReq {
    private static YyMvSearchReq instance;

    private YyMvSearchReq() {
    }

    public static YyMvSearchReq getInstance() {
        if (instance == null) instance = new YyMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV API (音悦台)
    private final String SEARCH_MV_YY_API = "https://search-api.yinyuetai.com/search/get_search_result.json";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit, String cursor) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.post(SEARCH_MV_YY_API)
                .jsonBody(String.format("{\"searchType\":\"MV\",\"key\":\"%s\",\"sinceId\":\"%s\",\"size\":%s," +
                                "\"requestTagRows\":[{\"key\":\"sortType\",\"chosenTags\":[\"COMPREHENSIVE\"]}," +
                                "{\"key\":\"source\",\"chosenTags\":[\"-1\"]},{\"key\":\"duration\",\"chosenTags\":[\"-1\"]}]}",
                        keyword, cursor, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONArray mvArray = mvInfoJson.getJSONArray("data");
        t = page * limit + 1;
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);
            JSONObject fullClip = mvJson.getJSONObject("fullClip");

            String mvId = mvJson.getString("id");
            if (i == len - 1) cursor = mvId;
            String mvName = mvJson.getString("title");
            String artistName = SdkUtil.parseArtist(mvJson);
            String creatorId = SdkUtil.parseArtistId(mvJson);
            Long playCount = mvJson.getLong("playNum");
            Double duration = fullClip.getDouble("duration");
            String pubTime = TimeUtil.msToDate(mvJson.getLong("publishDate") * 1000);
            String coverImgUrl = mvJson.getString("headImg");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetMusicSource.YY);
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
        return new CommonResult<>(r, t, cursor);
    }
}
