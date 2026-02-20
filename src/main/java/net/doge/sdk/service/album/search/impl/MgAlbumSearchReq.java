package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgAlbumSearchReq {
    private static MgAlbumSearchReq instance;

    private MgAlbumSearchReq() {
    }

    public static MgAlbumSearchReq getInstance() {
        if (instance == null) instance = new MgAlbumSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = SdkCommon.mgSearchRequest("album", keyword, page, limit)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody).getJSONObject("albumResultData");
        t = albumInfoJson.getIntValue("totalCount");
        JSONArray albumArray = albumInfoJson.getJSONArray("result");
        if (JsonUtil.notEmpty(albumArray)) {
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = albumJson.getString("singer");
//                    String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = albumJson.getString("publishDate");
//                    Integer songNum = albumJson.getIntValue("songNum");
                JSONArray imgItems = albumJson.getJSONArray("imgItems");
                String coverImgThumbUrl = JsonUtil.isEmpty(imgItems) ? null : SdkUtil.findFeatureObj(imgItems, "imgSizeType", "03").getString("img");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetResourceSource.MG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
//                    albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
//                    albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                r.add(albumInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
