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
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.core.time.TimeUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiMvSearchReq {
    private static BiMvSearchReq instance;

    private BiMvSearchReq() {
    }

    public static BiMvSearchReq getInstance() {
        if (instance == null) instance = new BiMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV API (哔哩哔哩)
    private final String SEARCH_MV_BI_API = "https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=%s&page=%s";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_BI_API, encodedKeyword, page))
                .cookie(SdkCommon.BI_COOKIE)
                .execute();
        String mvInfoBody = resp.body();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = data.getIntValue("numResults");
        JSONArray mvArray = data.getJSONArray("result");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String bvId = mvJson.getString("bvid");
            String mvName = HtmlUtil.removeHtmlLabel(mvJson.getString("title"));
            String artistName = mvJson.getString("author");
            String creatorId = mvJson.getString("mid");
            Long playCount = mvJson.getLong("play");
            Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
            String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);
            String coverImgUrl = "https:" + mvJson.getString("pic");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.BI);
            mvInfo.setBvId(bvId);
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
