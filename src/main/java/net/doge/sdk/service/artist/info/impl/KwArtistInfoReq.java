package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwArtistInfoReq {
    private static KwArtistInfoReq instance;

    private KwArtistInfoReq() {
    }

    public static KwArtistInfoReq getInstance() {
        if (instance == null) instance = new KwArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (酷我)
    private final String ARTIST_DETAIL_KW_API = "https://kuwo.cn/api/www/artist/artist?artistid=%s&httpsStatus=1";
    // 歌手歌曲 API (酷我)
    private final String ARTIST_SONGS_KW_API = "https://kuwo.cn/api/www/artist/artistMusic?artistid=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public void preloadArtistInfo(NetArtistInfo artistInfo) {
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl());
            if (coverImgThumb == null)
                coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgUrl().replaceFirst("/300/", "/0/"));
            artistInfo.setCoverImgThumb(coverImgThumb);
        });
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("data");

        String artistId = artistJson.getString("id");
        String name = artistJson.getString("name");
        String coverImgThumbUrl = artistJson.getString("pic300");
        Integer songNum = artistJson.getIntValue("musicNum");
        Integer albumNum = artistJson.getIntValue("albumNum");
        Integer mvNum = artistJson.getIntValue("mvNum");

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetMusicSource.KW);
        artistInfo.setId(artistId);
        artistInfo.setName(name);
        artistInfo.setSongNum(songNum);
        artistInfo.setAlbumNum(albumNum);
        artistInfo.setMvNum(mvNum);
        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            artistInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(artistInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");

        String description = HtmlUtil.removeHtmlLabel(data.getString("info"));
        String coverImgUrl = data.getString("pic300");

        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("musicNum"));
        if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumNum"));
        if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvNum"));
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_SONGS_KW_API, id, page, limit))
                .header(Header.REFERER, "https://kuwo.cn/singer_detail/" + UrlUtil.encodeAll(id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("list");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("rid");
            // 酷我歌名中可能含有 HTML 标签，先去除
            String name = HtmlUtil.removeHtmlLabel(songJson.getString("name"));
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
            musicInfo.setArtistId(artistId);
            musicInfo.setArtist(artist);
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
