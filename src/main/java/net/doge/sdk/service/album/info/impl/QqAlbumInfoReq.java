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
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqAlbumInfoReq {
    private static QqAlbumInfoReq instance;

    private QqAlbumInfoReq() {
    }

    public static QqAlbumInfoReq getInstance() {
        if (instance == null) instance = new QqAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (QQ)
    private final String ALBUM_DETAIL_QQ_API = "https://c.y.qq.com/v8/fcg-bin/musicmall.fcg?_=1689937314930&cv=4747474&ct=24&format=json&inCharset=utf-8" +
            "&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&cmd=get_album_buy_page&albummid=%s&albumid=0";
    // 歌曲封面信息 API (QQ)
    private final String SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String albumId = albumJson.getString("album_mid");
        String name = albumJson.getString("album_name");
        String artist = SdkUtil.parseArtist(albumJson);
        String artistId = SdkUtil.parseArtistId(albumJson);
        String publishTime = albumJson.getString("publictime");
        String coverImgThumbUrl = String.format(SONG_IMG_QQ_API, albumId);
        Integer songNum = albumJson.getJSONArray("songlist").size();

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetMusicSource.QQ);
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
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");

        // QQ 专辑封面图片 url 获取方式与歌曲相同
        String coverImgUrl = String.format(SONG_IMG_QQ_API, id);
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
        String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
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
            musicInfo.setSource(NetMusicSource.QQ);
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
