package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqAlbumSearchReq {
    private static QqAlbumSearchReq instance;

    private QqAlbumSearchReq() {
    }

    public static QqAlbumSearchReq getInstance() {
        if (instance == null) instance = new QqAlbumSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 2))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray albumArray = data.getJSONObject("body").getJSONObject("album").getJSONArray("list");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumMID");
            String albumName = albumJson.getString("albumName");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String publishTime = albumJson.getString("publicTime");
            Integer songNum = albumJson.getIntValue("song_count");
            String coverImgThumbUrl = albumJson.getString("albumPic").replaceFirst("http:", "https:");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.QQ);
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
            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }
}
