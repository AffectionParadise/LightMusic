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

public class MeRadioSearchReq {
    private static MeRadioSearchReq instance;

    private MeRadioSearchReq() {
    }

    public static MeRadioSearchReq getInstance() {
        if (instance == null) instance = new MeRadioSearchReq();
        return instance;
    }

    // 关键词搜索电台 API(猫耳)
    private final String SEARCH_RADIO_ME_API = "https://www.missevan.com/dramaapi/search?s=%s&page=%s&page_size=%s";

    /**
     * 根据关键词获取电台
     */
    public CommonResult<NetRadioInfo> searchRadios(String keyword, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_ME_API, encodedKeyword, page, limit))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info");
        t = data.getJSONObject("pagination").getIntValue("count");
        JSONArray radioArray = data.getJSONArray("Datas");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = radioJson.getString("author");
            String coverImgThumbUrl = radioJson.getString("cover");
            Long playCount = radioJson.getLong("view_count");
            String category = radioJson.getString("catalog_name");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetResourceSource.ME);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setPlayCount(playCount);
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
