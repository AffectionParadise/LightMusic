package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.http.HttpRequest;

public class HkMvUrlReq {
    private static HkMvUrlReq instance;

    private HkMvUrlReq() {
    }

    public static HkMvUrlReq getInstance() {
        if (instance == null) instance = new HkMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (好看)
    private final String MV_URL_HK_API = "https://haokan.baidu.com/v?vid=%s&_format=json";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(MV_URL_HK_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
        JSONArray urlArray = data.getJSONObject("apiData").getJSONObject("curVideoMeta").getJSONArray("clarityUrl");
        String quality;
        String[] qs = {"1080p", "sc", "hd", "sd"};
        switch (VideoQuality.quality) {
            case VideoQuality.UHD:
            case VideoQuality.FHD:
                quality = "1080p";
                break;
            case VideoQuality.HD:
                quality = "sc";
                break;
            case VideoQuality.SD:
                quality = "hd";
                break;
            default:
                quality = "sd";
                break;
        }
        for (int i = urlArray.size() - 1; i >= 0; i--) {
            JSONObject urlJson = urlArray.getJSONObject(i);
            if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("key"))) continue;
            return urlJson.getString("url");
        }
        return "";
    }
}
