package net.doge.sdk.entity.mv.info;

import cn.hutool.http.HttpRequest;
import net.doge.constant.player.Format;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MvUrlReq {
    // mlog id 转视频 id API
    private final String MLOG_TO_VIDEO_API = SdkCommon.prefix + "/mlog/to/video?id=%s";
    // MV 视频链接 API
    private final String MV_URL_API = SdkCommon.prefix + "/mv/url?id=%s";
    // 视频链接 API
    private final String VIDEO_URL_API = SdkCommon.prefix + "/video/url?id=%s";
    // Mlog 链接 API
//    private final String MLOG_URL_API = SdkCommon.prefix + "/mlog/url?id=%s";
    // MV 视频链接获取 API (酷狗)
//    private final String MV_URL_KG_API = "https://gateway.kugou.com/v2/interface/index?appid=1014&clienttime=%s&clientver=20000&cmd=123&dfid=-" +
//            "&ext=mp4&hash=%s&ismp3=0&key=kugoumvcloud&mid=%s&pid=6&srcappid=2919&ssl=1&uuid=%s";
    private final String MV_URL_KG_API = "http://m.kugou.com/app/i/mv.php?cmd=100&hash=%s&ismp3=1&ext=mp4";
    // MV 视频链接获取 API (QQ)
    private final String MV_URL_QQ_API = SdkCommon.prefixQQ33 + "/mv/url?id=%s";
    // MV 视频链接获取 API (酷我)
    private final String MV_URL_KW_API = "http://www.kuwo.cn/api/v1/www/music/playUrl?mid=%s&type=mv&httpsStatus=1";
    // MV 视频链接获取 API (千千)
    private final String MV_URL_QI_API = "https://music.91q.com/v1/video/info?appid=16073360&assetCode=%s&timestamp=%s";
    // MV 视频链接获取 API (5sing)
    private final String MV_URL_FS_API = "http://service.5sing.kugou.com/mv/play?mvId=%s&type=1";
    // MV 视频链接获取 API (好看)
    private final String MV_URL_HK_API = "https://haokan.baidu.com/v?vid=%s&_format=json";
    // 视频 bvid 获取 cid (哔哩哔哩)
    private final String VIDEO_CID_BI_API = "https://api.bilibili.com/x/player/pagelist?bvid=%s";
    // MV 视频链接获取 API (哔哩哔哩)
    private final String VIDEO_URL_BI_API = "https://api.bilibili.com/x/player/playurl?bvid=%s&cid=%s&qn=64";
    
    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        String mvId = netMvInfo.getId();
        String bvId = netMvInfo.getBvid();
        boolean isVideo = netMvInfo.isVideo();
        boolean isMlog = netMvInfo.isMlog();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            if (isVideo || isMlog) {
                // Mlog 需要先获取视频 id，并转为视频类型
                if (isMlog) {
                    String body = HttpRequest.get(String.format(MLOG_TO_VIDEO_API, mvId))
                            .execute()
                            .body();
                    mvId = JSONObject.fromObject(body).getString("data");
                    netMvInfo.setId(mvId);
                    netMvInfo.setType(MvInfoType.VIDEO);
                }

                String mvBody = HttpRequest.get(String.format(VIDEO_URL_API, mvId))
                        .execute()
                        .body();
                JSONObject mvJson = JSONObject.fromObject(mvBody);
                JSONArray urls = mvJson.getJSONArray("urls");
                String url = urls.getJSONObject(0).getString("url");
                if (!"null".equals(url)) return url;
            }
//            else if (isMlog) {
//                String mvBody = HttpRequest.get(String.format(MLOG_URL_API, mvId))
//                        .execute()
//                        .body();
//                JSONObject mvJson = JSONObject.fromObject(mvBody);
//                JSONArray urls = mvJson.getJSONObject("data")
//                        .getJSONObject("resource")
//                        .getJSONObject("content")
//                        .getJSONObject("video")
//                        .getJSONArray("urlInfos");
//                String url = null;
//                int r = 0;
//                for (int i = 0, s = urls.size(); i < s; i++) {
//                    JSONObject urlJson = urls.getJSONObject(i);
//                    int r1 = urlJson.getInt("r");
//                    if (r < r1) {
//                        r = r1;
//                        url = urlJson.getString("url");
//                    }
//                }
//                if (!"null".equals(url)) return url;
//            }
            else {
                String mvBody = HttpRequest.get(String.format(MV_URL_API, mvId))
                        .execute()
                        .body();
                JSONObject mvJson = JSONObject.fromObject(mvBody);
                JSONObject data = mvJson.getJSONObject("data");
                String url = data.getString("url");
                if (!"null".equals(url)) return url;
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            long ct = System.currentTimeMillis();
//            String mvBody = HttpRequest.get(buildKgUrl(String.format(MV_URL_KG_API, ct, mvId, ct, ct)))
//                    .header("x-router", "trackermv.kugou.com")
//                    .execute()
//                    .body();
//            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("data");
//            return data.getJSONObject(mvId.toLowerCase()).getString("downurl");

            String mvBody = HttpRequest.get(String.format(MV_URL_KG_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("mvdata");
            JSONObject mvJson;
            // 高画质优先
            mvJson = data.getJSONObject("rq");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("sq");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("sd");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("le");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("hd");
            return mvJson.optString("downurl");
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvBody = HttpRequest.get(String.format(MV_URL_QQ_API, mvId))
                    .execute()
                    .body();
            JSONArray mp4Array = JSONObject.fromObject(mvBody)
                    .getJSONObject("data")
                    .getJSONArray(mvId);
            return mp4Array.getString(mp4Array.size() - 1);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String mvBody = SdkCommon.kwRequest(String.format(MV_URL_KW_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).optJSONObject("data");
            if (data != null) return data.getString("url");
        }

        // 咪咕 (暂时没有 MV url 的获取方式)
        else if (source == NetMusicSource.MG) {
            return null;
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String mvBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(MV_URL_QI_API, mvId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONArray data = JSONObject.fromObject(mvBody).getJSONArray("data");
            JSONArray urls = data.getJSONObject(0).getJSONArray("allRate");
            return urls.getJSONObject(0).getString("path");
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            return SdkUtil.getRedirectUrl(String.format(MV_URL_FS_API, mvId));
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String mvBody = HttpRequest.get(String.format(MV_URL_HK_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("data");
            JSONArray urls = data.getJSONObject("apiData").getJSONObject("curVideoMeta").getJSONArray("clarityUrl");
            return urls.getJSONObject(urls.size() - 1).getString("url");
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            // 先通过 bvid 获取 cid
            if (StringUtil.isEmpty(mvId)) {
                String cidBody = HttpRequest.get(String.format(VIDEO_CID_BI_API, bvId))
                        .cookie(SdkCommon.BI_COOKIE)
                        .execute()
                        .body();
                netMvInfo.setId(mvId = JSONObject.fromObject(cidBody).getJSONArray("data").getJSONObject(0).getString("cid"));
            }

            String mvBody = HttpRequest.get(String.format(VIDEO_URL_BI_API, bvId, mvId))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("data");
            JSONArray urls = data.getJSONArray("durl");
            String url = urls.getJSONObject(0).getString("url");
            // 根据 url 判断视频的格式
            netMvInfo.setFormat(url.contains(".mp4?") ? Format.MP4 : Format.FLV);
            return url;
        }

        return null;
    }
}
