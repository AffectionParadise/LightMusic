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

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiArtistInfoReq {
    private static QiArtistInfoReq instance;

    private QiArtistInfoReq() {
    }

    public static QiArtistInfoReq getInstance() {
        if (instance == null) instance = new QiArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (千千)
    private final String ARTIST_DETAIL_QI_API = "https://music.91q.com/v1/artist/info?appid=16073360&artistCode=%s&timestamp=%s";
    // 歌手歌曲 API (千千)
    private final String ARTIST_SONGS_QI_API = "https://music.91q.com/v1/artist/song?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject artistJson = artistInfoJson.getJSONObject("data");

        String artistId = artistJson.getString("artistCode");
        String name = artistJson.getString("name");
        String coverImgThumbUrl = artistJson.getString("pic");
        Integer songNum = artistJson.getIntValue("trackTotal");
        Integer albumNum = artistJson.getIntValue("albumTotal");
        Integer mvNum = artistJson.getIntValue("videoTotal");

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetMusicSource.QI);
        artistInfo.setId(artistId);
        artistInfo.setName(name);
        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        artistInfo.setSongNum(songNum);
        artistInfo.setAlbumNum(albumNum);
        artistInfo.setMvNum(mvNum);
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
        String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");

        String description = data.getString("introduce");
        String coverImgUrl = data.getString("pic");

        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("trackTotal"));
        if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumTotal"));
        if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("videoTotal"));
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_SONGS_QI_API, id, page, limit, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("data");
        total = data.getIntValue("total");
        JSONArray songArray = data.getJSONArray("result");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("TSID");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("albumTitle");
            String albumId = songJson.getString("albumAssetCode");
            Double duration = songJson.getDouble("duration");
            int qualityType = AudioQuality.UNKNOWN;
            String allRate = songJson.getJSONArray("allRate").toString();
            if (allRate.contains("3000")) qualityType = AudioQuality.SQ;
            else if (allRate.contains("320")) qualityType = AudioQuality.HQ;
            else if (allRate.contains("128")) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.QI);
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
