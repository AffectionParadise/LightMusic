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
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QqAlbumInfoReq {
    private static QqAlbumInfoReq instance;

    private QqAlbumInfoReq() {
    }

    public static QqAlbumInfoReq getInstance() {
        if (instance == null) instance = new QqAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (QQ)
    private final String ALBUM_DETAIL_QQ_API = "https://c.y.qq.com/v8/fcg-bin/musicmall.fcg";

    private HttpRequest buildAlbumInfoRequest(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("_", 1689937314930L);
        params.put("cv", 4747474);
        params.put("ct", 24);
        params.put("format", "json");
        params.put("inCharset", "utf-8");
        params.put("outCharset", "utf-8");
        params.put("notice", 0);
        params.put("platform", "yqq.json");
        params.put("needNewCode", 1);
        params.put("uin", 0);
        params.put("g_tk_new_20200303", 5381);
        params.put("g_tk", 5381);
        params.put("cmd", "get_album_buy_page");
        params.put("albummid", id);
        params.put("albumid", 0);
        return HttpRequest.get(ALBUM_DETAIL_QQ_API)
                .queryParams(params);
    }

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = buildAlbumInfoRequest(id)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String albumId = albumJson.getString("album_mid");
        String name = albumJson.getString("album_name");
        String artist = SdkUtil.parseArtist(albumJson);
        String artistId = SdkUtil.parseArtistId(albumJson);
        String publishTime = albumJson.getString("publictime");
        String coverImgThumbUrl = String.format(SdkCommon.SONG_IMG_QQ_API, albumId);
        Integer songNum = albumJson.getJSONArray("songlist").size();

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetResourceSource.QQ);
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
        String albumInfoBody = buildAlbumInfoRequest(id)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");

        // QQ 专辑封面图片 url 获取方式与歌曲相同
        String coverImgUrl = String.format(SdkCommon.SONG_IMG_QQ_API, id);
        String description = data.getString("desc");

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
        String albumInfoBody = HttpRequest.post(SdkCommon.MAIN_QQ_API)
                .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":10000},\"albumSonglist\":{\"method\":\"GetAlbumSongList\",\"param\":" +
                        "{\"albumMid\":\"%s\",\"albumID\":0,\"begin\":0,\"num\":999,\"order\":2},\"module\":\"music.musichallAlbum.AlbumSongList\"}}", id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("albumSonglist").getJSONObject("data");
        total = data.getIntValue("totalNum");
        JSONArray songArray = data.getJSONArray("songList");
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i).getJSONObject("songInfo");
            JSONObject albumJson = songJson.getJSONObject("album");
            JSONObject fileJson = songJson.getJSONObject("file");

            String songId = songJson.getString("mid");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("title");
            String albumId = albumJson.getString("mid");
            Double duration = songJson.getDouble("interval");
            String mvId = songJson.getJSONObject("mv").getString("vid");
            int qualityType = AudioQuality.UNKNOWN;
            if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
            else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
            else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
            else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.QQ);
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
