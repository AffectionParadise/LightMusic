package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.LangUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkMvSearchReq {
    private static HkMvSearchReq instance;

    private HkMvSearchReq() {
    }

    public static HkMvSearchReq getInstance() {
        if (instance == null) instance = new HkMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV API (好看)
    private final String SEARCH_MV_HK_API = "https://haokan.baidu.com/haokan/ui-search/pc/search/video?query=%s&pn=%s&rn=%s&type=video";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_HK_API, encodedKeyword, page, limit))
                .cookie(SdkCommon.HK_COOKIE)
                .execute();
        String mvInfoBody = resp.body();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = page * limit;
        if (data.getIntValue("has_more") == 1) t++;
        JSONArray mvArray = data.getJSONArray("list");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("vid");
            String mvName = mvJson.getString("title");
            String artistName = mvJson.getString("author");
            String creatorId = mvJson.getString("author_id");
            Long playCount = LangUtil.parseNumber(mvJson.getString("read_num").replaceFirst("次播放", ""));
            Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
            String pubTime = mvJson.getString("publishTimeText");
            String coverImgUrl = mvJson.getString("cover_src");

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
}
