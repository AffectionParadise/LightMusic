package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.constant.service.MvInfoType;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;

import java.util.Map;

public class NcMvUrlReq {
    private static NcMvUrlReq instance;

    private NcMvUrlReq() {
    }

    public static NcMvUrlReq getInstance() {
        if (instance == null) instance = new NcMvUrlReq();
        return instance;
    }

    // mlog id 转视频 id API (网易云)
    private final String MLOG_TO_VIDEO_NC_API = "https://music.163.com/weapi/mlog/video/convert/id";
    // MV 视频链接 API (网易云)
    private final String MV_URL_NC_API = "https://music.163.com/weapi/song/enhance/play/mv/url";
    // 视频链接 API (网易云)
    private final String VIDEO_URL_NC_API = "https://music.163.com/weapi/cloudvideo/playurl";
    // Mlog 链接 API (网易云)
//    private final String MLOG_URL_NC_API = SdkCommon.PREFIX + "/mlog/url?id=%s";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        boolean isVideo = mvInfo.isVideo();
        boolean isMlog = mvInfo.isMlog();
        String quality;
        switch (VideoQuality.quality) {
            case VideoQuality.UHD:
            case VideoQuality.FHD:
                quality = "1080";
                break;
            case VideoQuality.HD:
                quality = "720";
                break;
            case VideoQuality.SD:
                quality = "480";
                break;
            default:
                quality = "240";
                break;
        }
        // Mlog 需要先获取视频 id，并转为视频类型
        if (isMlog) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String body = SdkCommon.ncRequest(Method.POST, MLOG_TO_VIDEO_NC_API, String.format("{\"mlogId\":\"%s\"}", id), options)
                    .executeAsStr();
            id = JSONObject.parseObject(body).getString("data");
            mvInfo.setId(id);
            mvInfo.setType(MvInfoType.VIDEO);
        }
        if (isVideo) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvBody = SdkCommon.ncRequest(Method.POST, VIDEO_URL_NC_API, String.format("{\"ids\":\"['%s']\",\"resolution\":\"%s\"}", id, quality), options)
                    .executeAsStr();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONArray urls = mvJson.getJSONArray("urls");
            String url = urls.getJSONObject(0).getString("url");
            if (StringUtil.notEmpty(url)) return url;
        }
//            else if (isMlog) {
//                String mvBody = HttpRequest.get(String.format(MLOG_URL_NC_API, id))
//                        .executeAsync()
//                        .body();
//                JSONObject mvJson = JSONObject.parseObject(mvBody);
//                JSONArray urls = mvJson.getJSONObject("data")
//                        .getJSONObject("resource")
//                        .getJSONObject("content")
//                        .getJSONObject("video")
//                        .getJSONArray("urlInfos");
//                String url = null;
//                int r = 0;
//                for (int i = 0, s = urls.size(); i < s; i++) {
//                    JSONObject urlJson = urls.getJSONObject(i);
//                    int r1 = urlJson.getIntValue("r");
//                    if (r < r1) {
//                        r = r1;
//                        url = urlJson.getString("url");
//                    }
//                }
//                if (StringUtil.notEmpty(url)) return url;
//            }
        else {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvBody = SdkCommon.ncRequest(Method.POST, MV_URL_NC_API, String.format("{\"id\":\"%s\",\"r\":\"%s\"}", id, quality), options)
                    .executeAsStr();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data");
            String url = data.getString("url");
            if (StringUtil.notEmpty(url)) return url;
        }
        return "";
    }
}
