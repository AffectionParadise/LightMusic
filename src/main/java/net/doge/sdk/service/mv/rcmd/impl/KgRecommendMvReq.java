package net.doge.sdk.service.mv.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgRecommendMvReq {
    private static KgRecommendMvReq instance;

    private KgRecommendMvReq() {
    }

    public static KgRecommendMvReq getInstance() {
        if (instance == null) instance = new KgRecommendMvReq();
        return instance;
    }

    // 推荐 MV API (酷狗)
    private final String RECOMMEND_MV_KG_API = "http://mobilecdnbj.kugou.com/api/v5/video/list?sort=4&id=%s&page=%s&pagesize=%s";
    // 编辑精选 MV API (酷狗)
    private final String IP_MV_KG_API = "/openapi/v1/ip/videos";

    /**
     * 推荐 MV
     */
    public CommonResult<NetMvInfo> getRecommendMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.RECOMMEND_MV_KG];
        if (StringUtil.notEmpty(param)) {
            String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_KG_API, param, page, limit))
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("mvhash");
                String mvName = mvJson.getString("videoname");
                String artistName = mvJson.getString("singername");
                String creatorId = SdkUtil.parseArtistId(mvJson);
                Long playCount = mvJson.getLong("playcount");
                Double duration = mvJson.getDouble("duration") / 1000;
                String pubTime = mvJson.getString("publish").split(" ")[0];
                String coverImgUrl = mvJson.getString("img").replace("/{size}", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.KG);
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
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 编辑精选 MV
     */
    public CommonResult<NetMvInfo> getIpMv(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.IP_MV_KG];
        if (StringUtil.notEmpty(param)) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_MV_KG_API);
            String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", param, page, limit);
            String mvInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            t = mvInfoJson.getIntValue("total");
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                JSONObject base = mvJson.getJSONObject("base");
                JSONObject h264 = mvJson.getJSONObject("h264");
                JSONObject extra = mvJson.getJSONObject("extra");

                String mvId = h264.getString("sd_hash");
                String mvName = base.getString("mv_name");
                String artistName = base.getString("singer");
                Long playCount = extra.getLong("hit");
                Double duration = base.getDouble("duration") / 1000;
                String pubTime = base.getString("publish_time").split(" ")[0];
                String coverImgUrl = base.getString("hdpic").replace("/{size}", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.KG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
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
        }
        return new CommonResult<>(r, t);
    }
}
