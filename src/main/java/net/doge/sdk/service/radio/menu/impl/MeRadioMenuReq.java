package net.doge.sdk.service.radio.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeRadioMenuReq {
    private static MeRadioMenuReq instance;

    private MeRadioMenuReq() {
    }

    public static MeRadioMenuReq getInstance() {
        if (instance == null) instance = new MeRadioMenuReq();
        return instance;
    }

    // 电台 CV API (猫耳)
    private final String RADIO_CVS_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 相似电台 API (猫耳)
    private final String SIMILAR_RADIO_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";

    /**
     * 获取相似电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        List<NetRadioInfo> res = new LinkedList<>();
        int t;

        String id = radioInfo.getId();
        String radioInfoBody = HttpRequest.get(String.format(SIMILAR_RADIO_ME_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info");
        JSONArray radioArray = data.getJSONArray("recommend");
        t = radioArray.size();
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String coverImgThumbUrl = radioJson.getString("front_cover");
            Long playCount = radioJson.getLong("view_count");

            NetRadioInfo ri = new NetRadioInfo();
            ri.setSource(NetMusicSource.ME);
            ri.setId(radioId);
            ri.setName(radioName);
            ri.setPlayCount(playCount);
            ri.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                ri.setCoverImgThumb(coverImgThumb);
            });

            res.add(ri);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo radioInfo) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t;

        String id = radioInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(RADIO_CVS_ME_API, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("info");
        JSONArray artistArray = data.getJSONArray("cvs");
        t = artistArray.size();
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("cv_info");

            String artistId = artistJson.getString("id");
            String artistName = artistJson.getString("name");
            String avatarThumbUrl = artistJson.getString("icon");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetMusicSource.ME);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(avatarThumbUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                artistInfo.setCoverImgThumb(avatarThumb);
            });

            res.add(artistInfo);
        }

        return new CommonResult<>(res, t);
    }
}
