package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.array.ArrayUtil;

import java.util.Map;
import java.util.TreeMap;

public class QiMvUrlReq {
    private static QiMvUrlReq instance;

    private QiMvUrlReq() {
    }

    public static QiMvUrlReq getInstance() {
        if (instance == null) instance = new QiMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (千千)
    private final String MV_URL_QI_API = "https://music.91q.com/v1/video/info";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo, boolean forDownload) {
        String id = mvInfo.getId();
        Map<String, Object> params = new TreeMap<>();
        params.put("assetCode", id);
        String mvBody = SdkCommon.qiRequest(MV_URL_QI_API, params)
                .executeAsStr();
        JSONArray data = JSONObject.parseObject(mvBody).getJSONArray("data");
        JSONArray urlArray = data.getJSONObject(0).getJSONArray("allRate");
        String quality;
        String[] qs = {"r2k", "r1080", "r720", "r480", "r360"};
        switch (forDownload ? VideoQuality.downQuality : VideoQuality.playQuality) {
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
