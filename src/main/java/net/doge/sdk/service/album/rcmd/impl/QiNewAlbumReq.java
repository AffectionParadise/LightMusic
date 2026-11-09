package net.doge.sdk.service.album.rcmd.impl;

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

public class QiNewAlbumReq {
    private static QiNewAlbumReq instance;

    private QiNewAlbumReq() {
    }

    public static QiNewAlbumReq getInstance() {
        if (instance == null) instance = new QiNewAlbumReq();
        return instance;
    }

    // 首页最新专辑 API (千千)
    private final String INDEX_NEW_ALBUM_QI_API = "https://music.91q.com/v1/index";
    // 秀动发行 API (千千)
    private final String XD_ALBUM_QI_API = "https://music.91q.com/v1//album/xdpublish";
    // 新专辑推荐 API (千千)
    private final String NEW_ALBUM_QI_API = "https://music.91q.com/v1/album/list";

    /**
     * 首页新专辑
     */
    public CommonResult<NetAlbumInfo> getIndexNewAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageSize", 12);
        params.put("type", "song");
        String albumInfoBody = SdkCommon.qiRequest(INDEX_NEW_ALBUM_QI_API, params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONArray dataArray = albumInfoJson.getJSONArray("data");
        JSONObject data = SdkUtil.findFeatureObj(dataArray, "type", "album");
        JSONArray albumArray = data.getJSONArray("result");
//        // 首页秀动发行
//        JSONObject xdData = dataArray.getJSONObject(2);
//        albumArray.addAll(xdData.getJSONArray("result"));
        t = albumArray.size();
        for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumAssetCode");
            String albumName = albumJson.getString("title");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String coverImgThumbUrl = albumJson.getString("pic");
            String releaseDate = albumJson.getString("releaseDate");
            if (StringUtil.isEmpty(releaseDate)) releaseDate = albumJson.getString("pushTime");
            String publishTime = releaseDate.split("T")[0];
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

    /**
     * 秀动发行
     */
    public CommonResult<NetAlbumInfo> getXDAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("module_name", "秀动发行");
        params.put("moreApi", "v1/album/xdpublish");
        params.put("pageNo", page);
        params.put("pageSize", limit);
        params.put("type", "showstart");
        String albumInfoBody = SdkCommon.qiRequest(XD_ALBUM_QI_API, params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("result");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumAssetCode");
            String albumName = albumJson.getString("title");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String coverImgThumbUrl = albumJson.getString("pic");
            String publishTime = albumJson.getString("releaseDate").split("T")[0];
            Integer songNum = albumJson.getIntValue("trackCount");

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

    /**
     * 新专辑推荐
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new TreeMap<>();
        params.put("pageNo", page);
        params.put("pageSize", limit);
        String albumInfoBody = SdkCommon.qiRequest(NEW_ALBUM_QI_API, params)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("result");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("albumAssetCode");
            String albumName = albumJson.getString("title");
            String artist = SdkUtil.parseArtist(albumJson);
            String artistId = SdkUtil.parseArtistId(albumJson);
            String coverImgThumbUrl = albumJson.getString("pic");
            String publishTime = albumJson.getString("releaseDate").split("T")[0];
            Integer songNum = albumJson.getIntValue("trackCount");

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
