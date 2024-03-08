package net.doge.sdk.entity.mv.info;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.VideoQuality;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

public class MvUrlReq {
    private static MvUrlReq instance;

    private MvUrlReq() {
    }

    public static MvUrlReq getInstance() {
        if (instance == null) instance = new MvUrlReq();
        return instance;
    }

    // mlog id 转视频 id API
    private final String MLOG_TO_VIDEO_API = "https://music.163.com/weapi/mlog/video/convert/id";
    // MV 视频链接 API
    private final String MV_URL_API = "https://music.163.com/weapi/song/enhance/play/mv/url";
    // 视频链接 API
    private final String VIDEO_URL_API = "https://music.163.com/weapi/cloudvideo/playurl";
    // Mlog 链接 API
//    private final String MLOG_URL_API = SdkCommon.PREFIX + "/mlog/url?id=%s";
    // MV 视频链接获取 API (酷狗)
    private final String MV_URL_KG_API = "http://m.kugou.com/app/i/mv.php?cmd=100&hash=%s&ismp3=1&ext=mp4";
    //    private final String MV_URL_KG_API = "/v2/interface/index";
    // MV 视频链接获取 API (酷我)
    private final String MV_URL_KW_API = "https://kuwo.cn/api/v1/www/music/playUrl?mid=%s&type=mv&httpsStatus=1";
    // MV 视频链接获取 API (千千)
    private final String MV_URL_QI_API = "https://music.91q.com/v1/video/info?appid=16073360&assetCode=%s&timestamp=%s";
    // MV 视频链接获取 API (5sing)
    private final String MV_URL_FS_API = "http://service.5sing.kugou.com/mv/play?mvId=%s";
    // MV 视频链接获取 API (好看)
    private final String MV_URL_HK_API = "https://haokan.baidu.com/v?vid=%s&_format=json";
    // 视频 bvid 获取 cid (哔哩哔哩)
    private final String VIDEO_CID_BI_API = "https://api.bilibili.com/x/player/pagelist?bvid=%s";
    // MV 视频链接获取 API (哔哩哔哩)
    private final String VIDEO_URL_BI_API = "https://api.bilibili.com/x/player/playurl?bvid=%s&cid=%s&qn=%s";
    // MV 视频链接获取 API (哔哩哔哩)
    private final String MV_URL_YY_API = "https://video-api.yinyuetai.com/video/get?id=%s";
    // 视频链接获取 API (发姐)
    private final String VIDEO_URL_FA_API = "https://www.chatcyf.com/topics/%s/";
    // 视频链接获取 API (李志)
    private final String VIDEO_URL_LZ_API = "https://www.lizhinb.com/live/%s/";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        int source = mvInfo.getSource();
        String id = mvInfo.getId();
        String bvId = mvInfo.getBvid();
        boolean isVideo = mvInfo.isVideo();
        boolean isMlog = mvInfo.isMlog();

        // 网易云
        if (source == NetMusicSource.NC) {
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
            if (isVideo || isMlog) {
                // Mlog 需要先获取视频 id，并转为视频类型
                if (isMlog) {
                    Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                    String body = SdkCommon.ncRequest(Method.POST, MLOG_TO_VIDEO_API, String.format("{\"mlogId\":\"%s\"}", id), options)
                            .executeAsync()
                            .body();
                    id = JSONObject.parseObject(body).getString("data");
                    mvInfo.setId(id);
                    mvInfo.setType(MvInfoType.VIDEO);
                }

                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String mvBody = SdkCommon.ncRequest(Method.POST, VIDEO_URL_API, String.format("{\"ids\":\"['%s']\",\"resolution\":\"%s\"}", id, quality), options)
                        .executeAsync()
                        .body();
                JSONObject mvJson = JSONObject.parseObject(mvBody);
                JSONArray urls = mvJson.getJSONArray("urls");
                String url = urls.getJSONObject(0).getString("url");
                if (StringUtil.notEmpty(url)) return url;
            }
//            else if (isMlog) {
//                String mvBody = HttpRequest.get(String.format(MLOG_URL_API, id))
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
                String mvBody = SdkCommon.ncRequest(Method.POST, MV_URL_API, String.format("{\"id\":\"%s\",\"r\":\"%s\"}", id, quality), options)
                        .executeAsync()
                        .body();
                JSONObject mvJson = JSONObject.parseObject(mvBody);
                JSONObject data = mvJson.getJSONObject("data");
                String url = data.getString("url");
                if (StringUtil.notEmpty(url)) return url;
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String mvBody = HttpRequest.get(String.format(MV_URL_KG_API, id))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("mvdata");
            // 高画质优先
            JSONObject mvJson = VideoQuality.quality <= VideoQuality.FHD ? data.getJSONObject("rq") : null;
            if (JsonUtil.isEmpty(mvJson))
                mvJson = VideoQuality.quality <= VideoQuality.HD ? data.getJSONObject("sq") : null;
            if (JsonUtil.isEmpty(mvJson))
                mvJson = VideoQuality.quality <= VideoQuality.SD ? data.getJSONObject("hd") : null;
            if (JsonUtil.isEmpty(mvJson))
                mvJson = VideoQuality.quality <= VideoQuality.SD ? data.getJSONObject("sd") : null;
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

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"getMvUrl\":{\"module\":\"gosrf.Stream.MvUrlProxy\",\"method\":\"GetMvUrls\"," +
                            "\"param\":{\"vids\":[\"%s\"],\"request_typet\":10001}}}", id))
                    .executeAsync()
                    .body();
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
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String mvBody = SdkCommon.kwRequest(String.format(MV_URL_KW_API, id))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
            if (JsonUtil.notEmpty(data)) return data.getString("url");
        }

        // 咪咕 (暂时没有 MV url 的获取方式)
        else if (source == NetMusicSource.MG) {

        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String mvBody = SdkCommon.qiRequest(String.format(MV_URL_QI_API, id, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
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
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String mvBody = HttpRequest.get(String.format(MV_URL_FS_API, id))
                    .executeAsync()
                    .body();
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
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String mvBody = HttpRequest.get(String.format(MV_URL_HK_API, id))
                    .executeAsync()
                    .body();
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
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            // 先通过 bvid 获取 cid
            if (StringUtil.isEmpty(id)) {
                String cidBody = HttpRequest.get(String.format(VIDEO_CID_BI_API, bvId))
                        .cookie(SdkCommon.BI_COOKIE)
                        .executeAsync()
                        .body();
                mvInfo.setId(id = JSONObject.parseObject(cidBody).getJSONArray("data").getJSONObject(0).getString("cid"));
            }

            String quality;
            switch (VideoQuality.quality) {
                case VideoQuality.UHD:
                case VideoQuality.FHD:
                    quality = "116";
                    break;
                case VideoQuality.HD:
                    quality = "74";
                    break;
                case VideoQuality.SD:
                    quality = "32";
                    break;
                default:
                    quality = "16";
                    break;
            }
            String mvBody = HttpRequest.get(String.format(VIDEO_URL_BI_API, bvId, id, quality))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
            JSONArray urlArray = data.getJSONArray("durl");
            return urlArray.getJSONObject(0).getString("url");
        }

        // 音悦台
        else if (source == NetMusicSource.YY) {
            String mvBody = HttpRequest.get(String.format(MV_URL_YY_API, id))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(mvBody).getJSONObject("data");
            JSONArray urlArray = data.getJSONObject("fullClip").getJSONArray("urls");
            for (int i = 0, s = urlArray.size(); i < s; i++) {
                JSONObject urlJson = urlArray.getJSONObject(i);
                int streamType = urlJson.getIntValue("streamType");
                if (VideoQuality.quality <= VideoQuality.FHD && streamType <= 1
                        || VideoQuality.quality > VideoQuality.FHD && streamType > 1)
                    return urlJson.getString("url");
            }
        }

        // 发姐
        else if (source == NetMusicSource.FA) {
            String mvBody = HttpRequest.get(String.format(VIDEO_URL_FA_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(mvBody);
            Elements vs = doc.select("video source");
            if (vs.isEmpty()) vs = doc.select("video");
            if (!vs.isEmpty()) return StringUtil.urlEncodeBlank(vs.attr("src"));
        }

        // 李志
        else if (source == NetMusicSource.LZ) {
            String mvBody = HttpRequest.get(String.format(VIDEO_URL_LZ_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(mvBody);
            Elements video = doc.select("video");
            return StringUtil.urlEncodeBlank(video.attr("src"));
        }

        return "";
    }
}
