package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

public class QqMvUrlReq {
    private static QqMvUrlReq instance;

    private QqMvUrlReq() {
    }

    public static QqMvUrlReq getInstance() {
        if (instance == null) instance = new QqMvUrlReq();
        return instance;
    }

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"getMvUrl\":{\"module\":\"gosrf.Stream.MvUrlProxy\",\"method\":\"GetMvUrls\"," +
                        "\"param\":{\"vids\":[\"%s\"],\"request_typet\":10001}}}", id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("getMvUrl").getJSONObject("data").getJSONObject(id);
        JSONArray mp4Array = data.getJSONArray("mp4");
        String quality;
        String[] qs = {"50", "40", "30", "20", "10"};
        switch (VideoQuality.quality) {
            case VideoQuality.UHD:
                quality = "50";
                break;
            case VideoQuality.FHD:
                quality = "40";
                break;
            case VideoQuality.HD:
                quality = "30";
                break;
            case VideoQuality.SD:
                quality = "20";
                break;
            default:
                quality = "10";
                break;
        }
        for (int i = mp4Array.size() - 1; i >= 0; i--) {
            JSONObject urlJson = mp4Array.getJSONObject(i);
            if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("filetype"))) continue;
            JSONArray freeFlowUrl = urlJson.getJSONArray("freeflow_url");
            if (JsonUtil.isEmpty(freeFlowUrl)) continue;
            return freeFlowUrl.getString(freeFlowUrl.size() - 1);
        }
        return "";
    }
}
