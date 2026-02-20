package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiAlbumInfoReq {
    private static QiAlbumInfoReq instance;

    private QiAlbumInfoReq() {
    }

    public static QiAlbumInfoReq getInstance() {
        if (instance == null) instance = new QiAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (千千)
    private final String ALBUM_DETAIL_QI_API = "https://music.91q.com/v1/album/info?albumAssetCode=%s&appid=16073360&timestamp=%s";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = SdkCommon.qiRequest(String.format(ALBUM_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String albumId = albumJson.getString("albumAssetCode");
        String name = albumJson.getString("title");
        String artist = SdkUtil.parseArtist(albumJson);
        String artistId = SdkUtil.parseArtistId(albumJson);
        String publishTime = albumJson.getString("releaseDate").split("T")[0];
        String coverImgThumbUrl = albumJson.getString("pic");
        Integer songNum = albumJson.getJSONArray("trackList").size();

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetResourceSource.QI);
        albumInfo.setId(albumId);
        albumInfo.setName(name);
        albumInfo.setArtist(artist);
        albumInfo.setArtistId(artistId);
        albumInfo.setPublishTime(publishTime);
        albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        albumInfo.setSongNum(songNum);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            albumInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(albumInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = SdkCommon.qiRequest(String.format(ALBUM_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String coverImgUrl = albumJson.getString("pic");
        String description = albumJson.getString("introduce");
        if (!albumInfo.hasSongNum()) {
            JSONArray trackList = albumJson.getJSONArray("trackList");
            Integer songNum = JsonUtil.notEmpty(trackList) ? trackList.size() : null;
            albumInfo.setSongNum(songNum);
        }
        if (!albumInfo.hasPublishTime())
            albumInfo.setPublishTime(albumJson.getString("releaseDate").split("T")[0]);

        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        albumInfo.setDescription(description);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = albumInfo.getId();
        String albumInfoBody = SdkCommon.qiRequest(String.format(ALBUM_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        JSONArray songArray = data.getJSONArray("trackList");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("assetId");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = data.getString("title");
            String albumId = data.getString("albumAssetCode");
            Double duration = songJson.getDouble("duration");
            int qualityType = AudioQuality.UNKNOWN;
            JSONObject rateFileInfo = songJson.getJSONObject("rateFileInfo");
            if (rateFileInfo.containsKey("3000")) qualityType = AudioQuality.SQ;
            else if (rateFileInfo.containsKey("320")) qualityType = AudioQuality.HQ;
            else if (rateFileInfo.containsKey("128")) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QI);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
