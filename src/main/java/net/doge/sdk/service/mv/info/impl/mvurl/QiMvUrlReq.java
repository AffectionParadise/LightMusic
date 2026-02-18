package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.array.ArrayUtil;

public class QiMvUrlReq {
    private static QiMvUrlReq instance;

    private QiMvUrlReq() {
    }

    public static QiMvUrlReq getInstance() {
        if (instance == null) instance = new QiMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (千千)
    private final String MV_URL_QI_API = "https://music.91q.com/v1/video/info?appid=16073360&assetCode=%s&timestamp=%s";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = SdkCommon.qiRequest(String.format(MV_URL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONArray data = JSONObject.parseObject(mvBody).getJSONArray("data");
        JSONArray urlArray = data.getJSONObject(0).getJSONArray("allRate");
        String quality;
        String[] qs = {"r2k", "r1080", "r720", "r480", "r360"};
        switch (VideoQuality.quality) {
            case VideoQuality.UHD:
                quality = "r2k";
                break;
            case VideoQuality.FHD:
                quality = "r1080";
                break;
            case VideoQuality.HD:
                quality = "r720";
                break;
            case VideoQuality.SD:
                quality = "r480";
                break;
            default:
                quality = "r360";
                break;
        }
        for (int i = 0, s = urlArray.size(); i < s; i++) {
            JSONObject urlJson = urlArray.getJSONObject(i);
            if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("resolution"))) continue;
            return urlJson.getString("path");
        }
        return "";
    }
}
