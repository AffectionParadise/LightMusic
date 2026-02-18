package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.http.HttpRequest;

public class FsMvUrlReq {
    private static FsMvUrlReq instance;

    private FsMvUrlReq() {
    }

    public static FsMvUrlReq getInstance() {
        if (instance == null) instance = new FsMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (5sing)
    private final String MV_URL_FS_API = "http://service.5sing.kugou.com/mv/play?mvId=%s";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(MV_URL_FS_API, id))
                .executeAsStr();
        JSONArray urlArray = JSONObject.parseObject(mvBody).getJSONArray("data");
        String quality;
        String[] qs = {"超清", "高清", "标清"};
        switch (VideoQuality.quality) {
            case VideoQuality.UHD:
            case VideoQuality.FHD:
            case VideoQuality.HD:
                quality = "超清";
                break;
            case VideoQuality.SD:
                quality = "高清";
                break;
            default:
                quality = "标清";
                break;
        }
        for (int i = 0, s = urlArray.size(); i < s; i++) {
            JSONObject urlJson = urlArray.getJSONObject(i);
            if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("title"))) continue;
            return urlJson.getString("url");
        }
        return "";
    }
}
