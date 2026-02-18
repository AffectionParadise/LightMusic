package net.doge.sdk.service.mv.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HkMvMenuReq {
    private static HkMvMenuReq instance;

    private HkMvMenuReq() {
    }

    public static HkMvMenuReq getInstance() {
        if (instance == null) instance = new HkMvMenuReq();
        return instance;
    }

    // 相似视频 API (好看)
    private final String SIMILAR_VIDEO_HK_API = "https://haokan.baidu.com/videoui/api/videorec?title=%s&vid=%s&act=pcRec&pd=pc";

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        List<NetMvInfo> res = new LinkedList<>();
        int t;

        String id = netMvInfo.getId();
        String name = UrlUtil.encodeAll(netMvInfo.getName());
        String mvInfoBody = HttpRequest.get(String.format(SIMILAR_VIDEO_HK_API, name, id))
                .header(Header.REFERER, String.format("https://haokan.baidu.com/v?vid=%s", id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data").getJSONObject("response");
        JSONArray mvArray = data.getJSONArray("videos");
        t = mvArray.size();
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("id");
            String mvName = mvJson.getString("title");
            String artistName = mvJson.getString("source_name");
            String creatorId = mvJson.getString("mthid");
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

            res.add(mvInfo);
        }

        return new CommonResult<>(res, t);
    }
}
