package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.json.JsonUtil;

public class KwMvUrlReq {
    private static KwMvUrlReq instance;

    private KwMvUrlReq() {
    }

    public static KwMvUrlReq getInstance() {
        if (instance == null) instance = new KwMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (酷我)
    private final String MV_URL_KW_API = "https://kuwo.cn/api/v1/www/music/playUrl?mid=%s&type=mv&httpsStatus=1";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = SdkCommon.kwRequest(String.format(MV_URL_KW_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
        if (JsonUtil.notEmpty(data)) return data.getString("url");
        return "";
    }
}
