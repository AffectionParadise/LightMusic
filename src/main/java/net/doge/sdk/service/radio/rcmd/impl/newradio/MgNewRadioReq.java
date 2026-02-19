package net.doge.sdk.service.radio.rcmd.impl.newradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgNewRadioReq {
    private static MgNewRadioReq instance;

    private MgNewRadioReq() {
    }

    public static MgNewRadioReq getInstance() {
        if (instance == null) instance = new MgNewRadioReq();
        return instance;
    }

    // 时序电台 API (咪咕)
    private final String TIMING_RADIO_MG_API = "https://app.c.nf.migu.cn/pc/bmw/page-data/music-radio/v1.0?templateVersion=1";

    /**
     * 时序电台
     */
    public CommonResult<NetRadioInfo> getTimingRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        String radioInfoBody = HttpRequest.get(TIMING_RADIO_MG_API)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("data");
        JSONArray contents = data.getJSONArray("contents");
        JSONArray radioArray = SdkUtil.findFeatureObj(contents, "title", "时序电台").getJSONArray("contents");
        t = radioArray.size();
        for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("resId");
            String radioName = radioJson.getString("txt");
            String coverImgThumbUrl = radioJson.getString("img");
            String coverImgUrl = coverImgThumbUrl;

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.MG);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            // 咪咕无电台详情接口，提前写入封面图片 url
            radioInfo.setCoverImgUrl(coverImgUrl);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }
}
