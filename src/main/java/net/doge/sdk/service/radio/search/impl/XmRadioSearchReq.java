package net.doge.sdk.service.radio.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

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
            = "https://www.ximalaya.com/revision/search/main?core=album&kw=%s&page=%s&spellchecker=true&rows=%s&condition=relation&device=iPhone&fq=&paidFilter=false";

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_XM_API, encodedKeyword, page, limit))
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
