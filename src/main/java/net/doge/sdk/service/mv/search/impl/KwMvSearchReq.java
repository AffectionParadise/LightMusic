package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwMvSearchReq {
    private static KwMvSearchReq instance;

    private KwMvSearchReq() {
    }

    public static KwMvSearchReq getInstance() {
        if (instance == null) instance = new KwMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV API (酷我)
    private final String SEARCH_MV_KW_API = "https://kuwo.cn/api/www/search/searchMvBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_MV_KW_API, encodedKeyword, page, limit)).execute();
        if (resp.isSuccessful()) {
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("mvlist");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = mvJson.getString("artist").replace("&", "、");
                String creatorId = mvJson.getString("artistid");
                Long playCount = mvJson.getLong("mvPlayCnt");
                Double duration = mvJson.getDouble("duration");
                String coverImgUrl = mvJson.getString("pic");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KW);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
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
