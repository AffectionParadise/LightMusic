package net.doge.sdk.service.mv.info.impl.mvinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;

public class KwMvInfoReq {
    private static KwMvInfoReq instance;

    private KwMvInfoReq() {
    }

    public static KwMvInfoReq getInstance() {
        if (instance == null) instance = new KwMvInfoReq();
        return instance;
    }

    // MV 信息 API (酷我)
    private final String MV_DETAIL_KW_API = "https://kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        String mvId = mvInfo.getId();
        String mvBody = SdkCommon.kwRequest(String.format(MV_DETAIL_KW_API, mvId))
                .executeAsStr();
        JSONObject mvJson = JSONObject.parseObject(mvBody);
        JSONObject data = mvJson.getJSONObject("data");

        String name = data.getString("name");
        String artist = data.getString("artist").replace("&", "、");
        String creatorId = data.getString("artistid");
        Long playCount = data.getLong("mvPlayCnt");
        Double duration = data.getDouble("duration");
        String pubTime = data.getString("releaseDate");
        String coverImgUrl = data.getString("pic");

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
