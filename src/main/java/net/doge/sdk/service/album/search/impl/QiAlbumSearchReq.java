package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QiAlbumSearchReq {
    private static QiAlbumSearchReq instance;

    private QiAlbumSearchReq() {
    }

    public static QiAlbumSearchReq getInstance() {
        if (instance == null) instance = new QiAlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API (千千)
    private final String SEARCH_ALBUM_QI_API = "https://music.91q.com/v1/search";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageNo", page);
        params.put("pageSize", limit);
        params.put("type", 3);
        params.put("word", keyword);
        String albumInfoBody = SdkCommon.qiRequest(SEARCH_ALBUM_QI_API, params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("typeAlbum");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumAssetCode");
            String albumName = albumJson.getString("title");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String rd = albumJson.getString("releaseDate");
            String publishTime = StringUtil.notEmpty(rd) ? rd.split("T")[0] : "";
            String coverImgThumbUrl = albumJson.getString("pic");
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
            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }
}
