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
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiArtistMenuReq {
    private static QiArtistMenuReq instance;

    private QiArtistMenuReq() {
    }

    public static QiArtistMenuReq getInstance() {
        if (instance == null) instance = new QiArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (千千)
    private final String ARTIST_ALBUMS_QI_API = "https://music.91q.com/v1/artist/album?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String albumInfoBody = SdkCommon.qiRequest(String.format(ARTIST_ALBUMS_QI_API, id, page, limit, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("result");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumAssetCode");
            String albumName = albumJson.getString("title");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String coverImgThumbUrl = albumJson.getString("pic");
            String publishTime = albumJson.getString("releaseDate").split("T")[0];
            JSONArray trackList = albumJson.getJSONArray("trackList");
            Integer songNum = JsonUtil.notEmpty(trackList) ? trackList.size() : null;

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.QI);
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
