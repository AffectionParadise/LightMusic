package net.doge.sdk.service.mv.info.impl.mvinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;

public class KgMvInfoReq {
    private static KgMvInfoReq instance;

    private KgMvInfoReq() {
    }

    public static KgMvInfoReq getInstance() {
        if (instance == null) instance = new KgMvInfoReq();
        return instance;
    }

    // MV 信息 API (酷狗)
    private final String MV_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/mv/detail?area_code=1&plat=0&mvhash=%s";

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        String mvId = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(MV_DETAIL_KG_API, mvId))
                .executeAsStr();
        JSONObject mvJson = JSONObject.parseObject(mvBody);
        JSONObject data = mvJson.getJSONObject("data").getJSONObject("info");
        String[] s = data.getString("filename").split(" - ");
        String name = s[1];
        String artist = s[0];
        String creatorId = SdkUtil.parseArtistId(mvJson);
        Long playCount = data.getLong("history_heat");
        Double duration = data.getDouble("mv_timelength") / 1000;
        String pubTime = data.getString("update");
        String coverImgUrl = data.getString("imgurl").replace("/{size}", "");

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
