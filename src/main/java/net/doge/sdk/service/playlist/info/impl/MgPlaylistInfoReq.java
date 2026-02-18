package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgPlaylistInfoReq {
    private static MgPlaylistInfoReq instance;

    private MgPlaylistInfoReq() {
    }

    public static MgPlaylistInfoReq getInstance() {
        if (instance == null) instance = new MgPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (咪咕)
//    private final String PLAYLIST_DETAIL_MG_API = PREFIX_MG + "/playlist?id=%s";
    private final String PLAYLIST_DETAIL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?needSimple=00&resourceType=2021&resourceId=%s";
    // 歌单歌曲 API (咪咕)
    private final String PLAYLIST_SONGS_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/user/queryMusicListSongs.do?musicListId=%s&pageNo=%s&pageSize=%s";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray resource = playlistInfoJson.getJSONArray("resource");
        if (JsonUtil.notEmpty(resource)) {
            JSONObject playlistJson = resource.getJSONObject(0);

            String playlistId = playlistJson.getString("musicListId");
            String name = playlistJson.getString("title");
            String creator = playlistJson.getString("ownerName");
            String creatorId = playlistJson.getString("ownerId");
            Long playCount = playlistJson.getJSONObject("opNumItem").getLong("playNum");
            Integer trackCount = playlistJson.getIntValue("musicNum");
            String coverImgThumbUrl = playlistJson.getJSONObject("imgItem").getString("img");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.MG);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(name);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setTrackCount(trackCount);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(playlistInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONArray("resource").getJSONObject(0);

        String coverImgUrl = data.getJSONObject("imgItem").getString("img");
        String summary = data.getString("summary");
        String description = StringUtil.isEmpty(summary) ? "" : summary;

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_MG_API, id, page, limit))
                .header(Header.REFERER, "https://m.music.migu.cn/")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray songArray = playlistInfoJson.getJSONArray("list");
        total = playlistInfoJson.getIntValue("totalCount");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("copyrightId");
            String name = songJson.getString("songName");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumId");
            Double duration = DurationUtil.toSeconds(songJson.getString("length"));
            // 咪咕音乐没有 mv 时，该字段不存在！
            String mvId = songJson.getString("mvId");
            int qualityType = AudioQuality.UNKNOWN;
            JSONArray newRateFormats = songJson.getJSONArray("newRateFormats");
            for (int k = newRateFormats.size() - 1; k >= 0; k--) {
                String formatType = newRateFormats.getJSONObject(k).getString("formatType");
                if ("ZQ".equals(formatType)) qualityType = AudioQuality.HR;
                else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                else if ("PQ".equals(formatType)) qualityType = AudioQuality.MQ;
                if (qualityType != AudioQuality.UNKNOWN) break;
            }

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.MG);
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
