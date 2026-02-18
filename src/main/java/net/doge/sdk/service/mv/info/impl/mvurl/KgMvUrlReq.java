package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.VideoQuality;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

public class KgMvUrlReq {
    private static KgMvUrlReq instance;

    private KgMvUrlReq() {
    }

    public static KgMvUrlReq getInstance() {
        if (instance == null) instance = new KgMvUrlReq();
        return instance;
    }

    // MV 视频链接获取 API (酷狗)
    private final String MV_URL_KG_API = "http://m.kugou.com/app/i/mv.php?cmd=100&hash=%s&ismp3=1&ext=mp4";
    //    private final String MV_URL_KG_API = "/v2/interface/index";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(MV_URL_KG_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("mvdata");
        // 高画质优先
        JSONObject mvJson = VideoQuality.quality >= VideoQuality.FHD ? data.getJSONObject("rq") : null;
        if (JsonUtil.isEmpty(mvJson))
            mvJson = VideoQuality.quality >= VideoQuality.HD ? data.getJSONObject("sq") : null;
        if (JsonUtil.isEmpty(mvJson))
            mvJson = VideoQuality.quality >= VideoQuality.SD ? data.getJSONObject("hd") : null;
        if (JsonUtil.isEmpty(mvJson))
            mvJson = VideoQuality.quality >= VideoQuality.SD ? data.getJSONObject("sd") : null;
        if (JsonUtil.isEmpty(mvJson)) mvJson = data.getJSONObject("le");
        return mvJson.getString("downurl");

        // 部分参数缺失，继续使用旧接口
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGetWithKey(MV_URL_KG_API);
//            Map<String, Object> params = new TreeMap<>();
//            params.put("backupdomain", 1);
//            params.put("cmd", 123);
//            params.put("ext", "mp4");
//            params.put("ismp3", 0);
//            params.put("hash", id);
//            params.put("pid", 1);
//            params.put("type", 1);
//            String mvBody = SdkCommon.kgRequest(params, null, options)
//                    .header("x-router", "trackermv.kugou.com")
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data").getJSONObject(id.toLowerCase());
//            return data.getString("downurl");
    }
}
