package net.doge.sdk.service.playlist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KgPlaylistInfoReq {
    private static KgPlaylistInfoReq instance;

    private KgPlaylistInfoReq() {
    }

    public static KgPlaylistInfoReq getInstance() {
        if (instance == null) instance = new KgPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (酷狗)
    private final String PLAYLIST_DETAIL_KG_API = "https://mobiles.kugou.com/api/v5/special/info_v2";
    // 歌单歌曲 API (酷狗)
    private final String PLAYLIST_SONGS_KG_API = "http://pubsongscdn.tx.kugou.com/v2/get_other_list_file";

    /**
     * 根据歌单 id 获取歌单
     */
    public CommonResult<NetPlaylistInfo> getPlaylistInfo(String id) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 1;

        Map<String, Object> params = new TreeMap<>();
        params.put("specialid", "0");
        params.put("global_specialid", id);
        params.put("format", "jsonp");
        params.put("srcappid", "2919");
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.webGet(PLAYLIST_DETAIL_KG_API);
        String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject playlistJson = playlistInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(playlistJson)) {
            String playlistId = playlistJson.getString("global_specialid");
            String name = playlistJson.getString("specialname");
            String creator = playlistJson.getString("nickname");
            Integer trackCount = playlistJson.getIntValue("songcount");
            Long playCount = playlistJson.getLong("playcount");
            String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.KG);
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
        Map<String, Object> params = new TreeMap<>();
        params.put("specialid", "0");
        params.put("global_specialid", id);
        params.put("format", "jsonp");
        params.put("srcappid", "2919");
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.webGet(PLAYLIST_DETAIL_KG_API);
        String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");

        String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
        String description = data.getString("intro");

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(SdkUtil.parseTag(data));
        if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getIntValue("songcount"));
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        Map<String, Object> params = new TreeMap<>();
        params.put("specialid", "0");
        params.put("need_sort", "1");
        params.put("module", "CloudMusic");
        params.put("type", "1");
        params.put("area_code", "1");
        params.put("global_collection_id", id);
        params.put("page", page);
        params.put("pagesize", limit);
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidGet(PLAYLIST_SONGS_KG_API);
        String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        total = data.getIntValue("count");
        JSONArray songArray = data.getJSONArray("info");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("albuminfo");
            JSONArray relateGoods = songJson.getJSONArray("relate_goods");

            String hash = songJson.getString("hash");
            String songId = songJson.getString("mixsongid");
            String name = songJson.getString("name");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("name");
            String albumId = albumJson.getString("id");
            Double duration = songJson.getDouble("timelen") / 1000;
            String mvId = songJson.getString("mvhash");
            int qualityType = AudioQuality.UNKNOWN;
            if (JsonUtil.hasObject(relateGoods, "level", 6)) qualityType = AudioQuality.HR;
            else if (JsonUtil.hasObject(relateGoods, "level", 5)) qualityType = AudioQuality.SQ;
            else if (JsonUtil.hasObject(relateGoods, "level", 4)) qualityType = AudioQuality.HQ;
            else if (JsonUtil.hasObject(relateGoods, "level", 2)) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.KG);
            musicInfo.setHash(hash);
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
