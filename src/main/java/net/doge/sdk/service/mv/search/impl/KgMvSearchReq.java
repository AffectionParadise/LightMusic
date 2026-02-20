package net.doge.sdk.service.mv.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.MvInfoType;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KgMvSearchReq {
    private static KgMvSearchReq instance;

    private KgMvSearchReq() {
    }

    public static KgMvSearchReq getInstance() {
        if (instance == null) instance = new KgMvSearchReq();
        return instance;
    }

    // 关键词搜索 MV API (酷狗)
    private final String SEARCH_MV_KG_API = "http://msearch.kugou.com/api/v3/search/mv?version=9108&keyword=%s&page=%s&pagesize=%s&sver=2";
    //    private final String SEARCH_MV_KG_API = "/v1/search/mv";

    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(String keyword, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_KG_API, encodedKeyword, page, limit))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONObject data = mvInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray mvArray = data.getJSONArray("info");
        for (int i = 0, len = mvArray.size(); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("hash");
            // 酷狗返回的名称含有 HTML 标签，需要去除
            String mvName = HtmlUtil.removeHtmlLabel(mvJson.getString("filename"));
            String artistName = HtmlUtil.removeHtmlLabel(mvJson.getString("singername"));
            String creatorId = mvJson.getString("userid");
            Long playCount = mvJson.getLong("historyheat");
            Double duration = mvJson.getDouble("duration");
            String pubTime = mvJson.getString("publishdate").split(" ")[0];
            String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.KG);
            // 酷狗搜索 MV 只给了用户 id，默认全是视频
            mvInfo.setType(MvInfoType.VIDEO);
            mvInfo.setId(mvId);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(mvInfo);
        }

        // 新接口部分参数获取不到，暂时使用旧接口
//            Map<String, Object> params = new TreeMap<>();
//            params.put("platform", "AndroidFilter");
//            params.put("keyword", keyword);
//            params.put("page", page);
//            params.put("pagesize", limit);
//            params.put("category", 1);
//            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(SEARCH_MV_KG_API);
//            String mvInfoBody = SdkCommon.kgRequest(params, null, options)
//                    .header("x-router", "complexsearch.kugou.com")
//                    .executeAsync()
//                    .body();
//            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//            JSONObject data = mvInfoJson.getJSONObject("data");
//            t = data.getIntValue("total");
//            JSONArray mvArray = data.getJSONArray("lists");
//            for (int i = 0, len = mvArray.size(); i < len; i++) {
//                JSONObject mvJson = mvArray.getJSONObject(i);
//
//                String mvId = mvJson.getString("MvHash");
//                String mvName = mvJson.getString("MvName");
//                String artistName = SdkUtil.parseArtist(mvJson);
//                String creatorId = SdkUtil.parseArtistId(mvJson);
//                Long playCount = mvJson.getLong("HistoryHeat");
//                Double duration = mvJson.getDouble("Duration");
//                String pubTime = mvJson.getString("PublishDate").split(" ")[0];
//                String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");
//
//                NetMvInfo mvInfo = new NetMvInfo();
//                mvInfo.setSource(NetMusicSource.KG);
//                mvInfo.setId(mvId);
//                mvInfo.setName(mvName);
//                mvInfo.setArtist(artistName);
//                mvInfo.setCreatorId(creatorId);
//                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
//                mvInfo.setPubTime(pubTime);
//                mvInfo.setCoverImgUrl(coverImgUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                    mvInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(mvInfo);
//            }
        return new CommonResult<>(r, t);
    }
}
