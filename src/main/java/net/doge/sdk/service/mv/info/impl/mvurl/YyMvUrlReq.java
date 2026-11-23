package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class YyMvUrlReq {
    private static YyMvUrlReq instance;

    private YyMvUrlReq() {
    }

    public static YyMvUrlReq getInstance() {
        if (instance == null) instance = new YyMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (音悦台)
    private final String MV_URL_YY_API = "https://video-api.yinyuetai.com/video/get";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo, boolean forDownload) {
        String id = mvInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        String mvBody = HttpRequest.get(MV_URL_YY_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
        JSONArray urlArray = data.getJSONObject("fullClip").getJSONArray("urls");
        int quality = forDownload ? VideoQuality.downQuality : VideoQuality.playQuality;
        for (int i = 0, s = urlArray.size(); i < s; i++) {
            JSONObject urlJson = urlArray.getJSONObject(i);
            int streamType = urlJson.getIntValue("streamType");
            if (quality >= VideoQuality.FHD && streamType <= 1
                    || quality < VideoQuality.FHD && streamType > 1)
                return urlJson.getString("url");
        }
        return "";
    }
}
