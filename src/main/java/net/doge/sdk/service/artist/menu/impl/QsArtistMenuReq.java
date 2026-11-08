package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QsArtistMenuReq {
    private static QsArtistMenuReq instance;

    private QsArtistMenuReq() {
    }

    public static QsArtistMenuReq getInstance() {
        if (instance == null) instance = new QsArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (汽水)
    private final String ARTIST_ALBUMS_QS_API = "https://api.qishui.com/luna/artists/%s/albums";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("cursor", (page - 1) * limit);
        params.put("count", limit);
        String albumInfoBody = SdkCommon.qsRequest(String.format(ARTIST_ALBUMS_QS_API, id), params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray albumArray = albumInfoJson.getJSONArray("albums");
        total = page * limit + (albumInfoJson.getBooleanValue("has_more") ? 1 : 0);
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = TimeUtil.msToDate(albumJson.getLong("release_date") * 1000);
            Integer songNum = albumJson.getIntValue("count_tracks");
            String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(albumJson);

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.QS);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setArtistId(artistId);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            albumInfo.setPublishTime(publishTime);
            albumInfo.setSongNum(songNum);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(albumInfo);
        }

        return new CommonResult<>(res, total);
    }
}
