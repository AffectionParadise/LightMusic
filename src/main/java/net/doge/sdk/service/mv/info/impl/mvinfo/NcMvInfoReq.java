package net.doge.sdk.service.mv.info.impl.mvinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.Map;

public class NcMvInfoReq {
    private static NcMvInfoReq instance;

    private NcMvInfoReq() {
    }

    public static NcMvInfoReq getInstance() {
        if (instance == null) instance = new NcMvInfoReq();
        return instance;
    }

    // MV 信息 API (网易云)
    private final String MV_DETAIL_NC_API = "https://music.163.com/api/v1/mv/detail";

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        String mvId = mvInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String mvBody = SdkCommon.ncRequest(Method.POST, MV_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", mvId), options)
                .executeAsStr();
        JSONObject mvJson = JSONObject.parseObject(mvBody);
        JSONObject data = mvJson.getJSONObject("data");
        String name = data.getString("name").trim();
        String artist = SdkUtil.parseArtist(data);
        String creatorId = SdkUtil.parseArtistId(data);
        Long playCount = data.getLong("playCount");
        Double duration = data.getDouble("duration") / 1000;
        String pubTime = data.getString("publishTime");
        String coverImgUrl = data.getString("cover");

        mvInfo.setName(name);
        mvInfo.setArtist(artist);
        mvInfo.setCreatorId(creatorId);
        mvInfo.setPlayCount(playCount);
        mvInfo.setDuration(duration);
        mvInfo.setPubTime(pubTime);
        mvInfo.setCoverImgUrl(coverImgUrl);

        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
            mvInfo.setCoverImgThumb(coverImgThumb);
        });
    }
}
