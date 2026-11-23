package net.doge.sdk.service.radio.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmRadioSearchReq {
    private static XmRadioSearchReq instance;

    private XmRadioSearchReq() {
    }

    public static XmRadioSearchReq getInstance() {
        if (instance == null) instance = new XmRadioSearchReq();
        return instance;
    }

    // 关键词搜索电台 API(喜马拉雅)
    private final String SEARCH_RADIO_XM_API
            = "https://www.ximalaya.com/revision/search/main";

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("core", "album");
        params.put("kw", keyword);
        params.put("page", page);
        params.put("spellchecker", true);
        params.put("rows", limit);
        params.put("condition", "relation");
        params.put("device", "iPhone");
        params.put("fq", "");
        params.put("paidFilter", false);
        String radioInfoBody = HttpRequest.get(SEARCH_RADIO_XM_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("data").getJSONObject("album");
        t = data.getIntValue("total");
        JSONArray radioArray = data.getJSONArray("docs");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("albumId");
            String radioName = radioJson.getString("title");
            String dj = radioJson.getString("nickname");
            String djId = radioJson.getString("uid");
            String coverImgThumbUrl = radioJson.getString("coverPath").replaceFirst("http:", "https:");
            Long playCount = radioJson.getLong("playCount");
            Integer trackCount = radioJson.getIntValue("tracksCount");
            String category = radioJson.getString("categoryTitle");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetResourceSource.XM);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setDjId(djId);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);
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
