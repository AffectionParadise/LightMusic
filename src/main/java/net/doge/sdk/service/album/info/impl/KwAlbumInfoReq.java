package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwAlbumInfoReq {
    private static KwAlbumInfoReq instance;

    private KwAlbumInfoReq() {
    }

    public static KwAlbumInfoReq getInstance() {
        if (instance == null) instance = new KwAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (酷我)
    private final String ALBUM_DETAIL_KW_API = "https://kuwo.cn/api/www/album/albumInfo?albumId=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, id, 1, 1))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String albumId = albumJson.getString("albumid");
        String name = albumJson.getString("album");
        String artist = albumJson.getString("artist").replace("&", "、");
        String artistId = albumJson.getString("artistid");
        String publishTime = albumJson.getString("releaseDate");
        String coverImgThumbUrl = albumJson.getString("pic");
        Integer songNum = albumJson.getInteger("total");

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetMusicSource.KW);
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
        String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, id, 1, 1))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");

        if (JsonUtil.notEmpty(data)) {
            String coverImgUrl = data.getString("pic");
            String description = data.getString("albuminfo");
            Integer songNum = data.getIntValue("total");

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
            albumInfo.setSongNum(songNum);
        }
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = albumInfo.getId();
        String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, id, page, limit))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("musicList");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("rid");
            String name = songJson.getString("name");
            String artist = songJson.getString("artist").replace("&", "、");
            String artistId = songJson.getString("artistid");
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumid");
            Double duration = songJson.getDouble("duration");
            String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
            int qualityType;
            if (songJson.getBoolean("hasLossless")) qualityType = AudioQuality.SQ;
            else qualityType = AudioQuality.HQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KW);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
