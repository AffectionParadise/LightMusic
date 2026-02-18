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
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwRecommendMvReq {
    private static KwRecommendMvReq instance;

    private KwRecommendMvReq() {
    }

    public static KwRecommendMvReq getInstance() {
        if (instance == null) instance = new KwRecommendMvReq();
        return instance;
    }

    // 推荐 MV API (酷我)
    private final String RECOMMEND_MV_KW_API = "https://kuwo.cn/api/www/music/mvList?pid=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTag.get(tag);

        if (StringUtil.notEmpty(s[7])) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(RECOMMEND_MV_KW_API, s[7], page, limit)).execute();
            if (resp.isSuccessful()) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
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
        }
        return new CommonResult<>(r, t);
    }
}
