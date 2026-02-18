package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiPlaylistInfoReq {
    private static BiPlaylistInfoReq instance;

    private BiPlaylistInfoReq() {
    }

    public static BiPlaylistInfoReq getInstance() {
        if (instance == null) instance = new BiPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (哔哩哔哩)
    private final String PLAYLIST_DETAIL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/menu/info?sid=%s";
    // 歌单歌曲 API (哔哩哔哩)
    private final String PLAYLIST_SONGS_BI_API = "https://www.bilibili.com/audio/music-service-c/web/song/of-menu?sid=%s&pn=%s&ps=%s";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_BI_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(playlistJson)) {
            JSONObject stat = playlistJson.getJSONObject("statistic");

            String playlistId = playlistJson.getString("menuId");
            String name = playlistJson.getString("title");
            String creator = playlistJson.getString("uname");
            String creatorId = playlistJson.getString("uid");
            Integer trackCount = playlistJson.getIntValue("snum", -1);
            Long playCount = stat.getLong("play");
            String coverImgThumbUrl = playlistJson.getString("cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.BI);
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
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_BI_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");

        String coverImgUrl = data.getString("cover");
        String description = data.getString("intro");

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        playlistInfo.setTag("");
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_BI_API, id, page, limit))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        total = data.getIntValue("totalSize");
        JSONArray songArray = data.getJSONArray("data");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("title");
            String artist = songJson.getString("uname");
            String artistId = songJson.getString("uid");
            Double duration = songJson.getDouble("duration");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.BI);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setDuration(duration);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
