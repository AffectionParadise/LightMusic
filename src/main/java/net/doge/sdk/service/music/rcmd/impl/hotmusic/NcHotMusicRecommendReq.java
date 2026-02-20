package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.service.playlist.info.PlaylistInfoReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcHotMusicRecommendReq {
    private static NcHotMusicRecommendReq instance;

    private NcHotMusicRecommendReq() {
    }

    public static NcHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new NcHotMusicRecommendReq();
        return instance;
    }

    // 曲风歌曲(最热) API (网易云)
    private final String STYLE_HOT_SONG_NC_API = "https://music.163.com/api/style-tag/home/song";

    /**
     * 飙升榜
     */
    public CommonResult<NetMusicInfo> getUpMusic(int page, int limit) {
        // 榜单就是歌单，固定榜单 id 直接请求歌单音乐接口，接口分页
        return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(String.valueOf(19723756), NetResourceSource.NC, page, limit);
    }

    /**
     * 热歌榜
     */
    public CommonResult<NetMusicInfo> getHotMusic(int page, int limit) {
        // 榜单就是歌单，固定榜单 id 直接请求歌单音乐接口，接口分页
        return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(String.valueOf(3778678), NetResourceSource.NC, page, limit);
    }

    /**
     * 曲风歌曲(最热)
     */
    public CommonResult<NetMusicInfo> getStyleHotSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTags.get(tag);

        String param = s[TagType.STYLE_HOT_SONG_NC];
        if (StringUtil.notEmpty(param)) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String musicInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_HOT_SONG_NC_API,
                            String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", param, (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("songs");
            t = data.getJSONObject("page").getIntValue("total");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("al");

                String songId = songJson.getString("id");
                String songName = songJson.getString("name").trim();
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");
                int qualityType = AudioQuality.UNKNOWN;
                if (JsonUtil.notEmpty(songJson.getJSONObject("hr"))) qualityType = AudioQuality.HR;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("sq"))) qualityType = AudioQuality.SQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("h"))) qualityType = AudioQuality.HQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("m"))) qualityType = AudioQuality.MQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("l"))) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
        }

        return new CommonResult<>(r, t);
    }
}
