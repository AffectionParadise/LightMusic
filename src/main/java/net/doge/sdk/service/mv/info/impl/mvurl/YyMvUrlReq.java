package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.http.HttpRequest;

public class YyMvUrlReq {
    private static YyMvUrlReq instance;

    private YyMvUrlReq() {
    }

    public static YyMvUrlReq getInstance() {
        if (instance == null) instance = new YyMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (音悦台)
    private final String MV_URL_YY_API = "https://video-api.yinyuetai.com/video/get?id=%s";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(MV_URL_YY_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
        JSONArray urlArray = data.getJSONObject("fullClip").getJSONArray("urls");
        for (int i = 0, s = urlArray.size(); i < s; i++) {
            JSONObject urlJson = urlArray.getJSONObject(i);
            int streamType = urlJson.getIntValue("streamType");
            if (VideoQuality.quality >= VideoQuality.FHD && streamType <= 1
                    || VideoQuality.quality < VideoQuality.FHD && streamType > 1)
                return urlJson.getString("url");
        }
        return "";
    }
}
