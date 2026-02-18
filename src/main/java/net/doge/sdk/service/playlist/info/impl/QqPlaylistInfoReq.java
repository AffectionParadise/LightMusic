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
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqPlaylistInfoReq {
    private static QqPlaylistInfoReq instance;

    private QqPlaylistInfoReq() {
    }

    public static QqPlaylistInfoReq getInstance() {
        if (instance == null) instance = new QqPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (QQ)
    private final String PLAYLIST_DETAIL_QQ_API = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
        if (JsonUtil.notEmpty(cdlist)) {
            JSONObject playlistJson = cdlist.getJSONObject(0);
            String playlistId = playlistJson.getString("disstid");
            String name = playlistJson.getString("dissname");
            String creator = playlistJson.getString("nickname");
            Long playCount = playlistJson.getLong("visitnum");
            Integer trackCount = playlistJson.getIntValue("songnum");
            String coverImgThumbUrl = playlistJson.getString("logo");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.QQ);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(name);
            playlistInfo.setCreator(creator);
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
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
        if (JsonUtil.isEmpty(cdlist)) return;
        JSONObject data = cdlist.getJSONObject(0);

        String coverImgUrl = data.getString("logo");
        String description = data.getString("desc").replace("<br>", "\n");

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
        if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getIntValue("songnum"));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total = 0;

        String id = playlistInfo.getId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                .header(Header.REFERER, "https://y.qq.com/n/yqq/playlist")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray cdlist = playlistInfoJson.getJSONArray("cdlist");
        if (JsonUtil.notEmpty(cdlist)) {
            JSONObject data = cdlist.getJSONObject(0);
            total = data.getIntValue("songnum");
            JSONArray songArray = data.getJSONArray("songlist");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("songmid");
                String name = songJson.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("albumname");
                String albumId = songJson.getString("albummid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getString("vid");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("size5_1") != 0) qualityType = AudioQuality.HR;
                else if (songJson.getLong("sizeflac") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("size320") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("size128") != 0) qualityType = AudioQuality.LQ;

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
        }

        return new CommonResult<>(res, total);
    }
}
