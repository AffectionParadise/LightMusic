package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqMvSearchReq {
    private static QqMvSearchReq instance;

    private QqMvSearchReq() {
    }

    public static QqMvSearchReq getInstance() {
        if (instance == null) instance = new QqMvSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 4))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray mvArray = data.getJSONObject("body").getJSONObject("mv").getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("v_id");
            String mvName = mvJson.getString("mv_name").trim();
            String artistName = SdkUtil.parseArtist(mvJson);
            String creatorId = SdkUtil.parseArtistId(mvJson);
            Long playCount = mvJson.getLong("play_count");
            Double duration = mvJson.getDouble("duration");
            String pubTime = mvJson.getString("publish_date");
            String coverImgUrl = mvJson.getString("mv_pic_url").replaceFirst("http:", "https:");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.QQ);
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
