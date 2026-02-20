package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MePlaylistInfoReq {
    private static MePlaylistInfoReq instance;

    private MePlaylistInfoReq() {
    }

    public static MePlaylistInfoReq getInstance() {
        if (instance == null) instance = new MePlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (猫耳)
    private final String PLAYLIST_DETAIL_ME_API = "https://www.missevan.com/sound/soundAllList?albumid=%s";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("info");
        if (JsonUtil.notEmpty(playlistJson)) {
            JSONObject album = playlistJson.getJSONObject("album");

            String playlistId = album.getString("id");
            String name = album.getString("title");
            String creator = album.getString("username");
            String creatorId = album.getString("user_id");
            Integer trackCount = album.getIntValue("music_count");
            Long playCount = album.getLong("view_count");
            String coverImgThumbUrl = album.getString("front_cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.ME);
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
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject info = playlistInfoJson.getJSONObject("info");
        if (JsonUtil.isEmpty(info)) return;
        JSONObject album = info.getJSONObject("album");

        String coverImgUrl = album.getString("front_cover");
        String description = HtmlUtil.removeHtmlLabel(album.getString("intro"));

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(info));
        if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(album.getIntValue("music_count"));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("info");
        JSONArray songArray = data.getJSONArray("sounds");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("soundstr");
            Double duration = songJson.getDouble("duration") / 1000;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.ME);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setDuration(duration);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
