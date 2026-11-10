package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.http.HttpRequest;

import java.util.Map;
import java.util.TreeMap;

public class BiMvUrlReq {
    private static BiMvUrlReq instance;

    private BiMvUrlReq() {
    }

    public static BiMvUrlReq getInstance() {
        if (instance == null) instance = new BiMvUrlReq();
        return instance;
    }

    // 视频 bvid 获取 cid (哔哩哔哩)
    private final String VIDEO_CID_BI_API = "https://api.bilibili.com/x/player/pagelist?bvid=%s";
    // MV 视频链接获取 API (哔哩哔哩)
    private final String VIDEO_URL_BI_API = "https://api.bilibili.com/x/player/playurl";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo, boolean forDownload) {
        String bvId = mvInfo.getBvId();
        // 先通过 bvid 获取 cid
        String cidBody = HttpRequest.get(String.format(VIDEO_CID_BI_API, bvId))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        String cid = JSONObject.parseObject(cidBody).getJSONArray("data").getJSONObject(0).getString("cid");

        String quality;
        switch (forDownload ? VideoQuality.downQuality : VideoQuality.playQuality) {
            case VideoQuality.UHD:
            case VideoQuality.FHD:
                quality = "116";
                break;
            case VideoQuality.HD:
                quality = "74";
                break;
            case VideoQuality.SD:
                quality = "32";
                break;
            default:
                quality = "16";
                break;
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("bvid", bvId);
        params.put("cid", cid);
        params.put("qn", quality);
        String mvBody = SdkCommon.biRequest(VIDEO_URL_BI_API, params)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
        JSONArray urlArray = data.getJSONArray("durl");
        return urlArray.getJSONObject(0).getString("url");
    }
}
