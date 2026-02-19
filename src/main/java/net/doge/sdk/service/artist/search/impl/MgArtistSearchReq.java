package net.doge.sdk.service.artist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgArtistSearchReq {
    private static MgArtistSearchReq instance;

    private MgArtistSearchReq() {
    }

    public static MgArtistSearchReq getInstance() {
        if (instance == null) instance = new MgArtistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(String keyword, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        String artistInfoBody = SdkCommon.mgSearchRequest("artist", keyword, page, limit)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody).getJSONObject("singerResultData");
        t = artistInfoJson.getIntValue("totalCount");
        JSONArray artistArray = artistInfoJson.getJSONArray("result");
        if (JsonUtil.notEmpty(artistArray)) {
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("songCount");
                Integer albumNum = artistJson.getIntValue("albumCount");
                Integer mvNum = artistJson.getIntValue("mvCount");
                JSONArray singerPicUrls = artistJson.getJSONArray("singerPicUrl");
                String coverImgThumbUrl = JsonUtil.isEmpty(singerPicUrls) ? null : SdkUtil.findFeatureObj(singerPicUrls, "imgSizeType", "03").getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);

//            String artistInfoBody = SdkCommon.mgSearchRequest("artist", keyword, page, limit)
//                    .executeAsync()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("singerResultData");
//            t = data.getIntValue("totalCount");
//            JSONArray artistArray = data.getJSONArray("result");
//            if (JsonUtil.notEmpty(artistArray)) {
//                for (int i = 0, len = artistArray.size(); i < len; i++) {
//                    JSONObject artistJson = artistArray.getJSONObject(i);
//
//                    String artistId = artistJson.getString("id");
//                    String artistName = artistJson.getString("name");
//                    Integer songNum = artistJson.getIntValue("songCount");
//                    Integer albumNum = artistJson.getIntValue("albumCount");
//                    Integer mvNum = artistJson.getIntValue("mvCount");
//                    String coverImgThumbUrl = artistJson.getJSONArray("singerPicUrl").getJSONObject(0).getString("img");
//
//                    NetArtistInfo artistInfo = new NetArtistInfo();
//                    artistInfo.setSource(NetMusicSource.MG);
//                    artistInfo.setId(artistId);
//                    artistInfo.setName(artistName);
//                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    artistInfo.setSongNum(songNum);
//                    artistInfo.setAlbumNum(albumNum);
//                    artistInfo.setMvNum(mvNum);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        artistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//                    r.add(artistInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
    }
}
