package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcHighQualityPlaylistReq {
    private static NcHighQualityPlaylistReq instance;

    private NcHighQualityPlaylistReq() {
    }

    public static NcHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new NcHighQualityPlaylistReq();
        return instance;
    }

    // 精品歌单 API (网易云)
    private final String HIGH_QUALITY_PLAYLIST_NC_API = "https://music.163.com/api/playlist/highquality/list";
    // 网友精选碟(最热/最新) API (网易云)
    private final String PICKED_PLAYLIST_NC_API = "https://music.163.com/weapi/playlist/list";

    /**
     * 精品歌单(程序分页)
     */
    public CommonResult<NetPlaylistInfo> getHighQualityPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, HIGH_QUALITY_PLAYLIST_NC_API,
                            String.format("{\"cat\":\"%s\",\"lasttime\":0,\"limit\":%s,\"total\":true}", s[0], limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);
                JSONObject ct = playlistJson.getJSONObject("creator");

                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("name");
                String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 网友精选碟(最热)(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getHotPickedPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_NC_API,
                            String.format("{\"cat\":\"%s\",\"order\":\"hot\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[1], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = playlistInfoJson.getIntValue("total");
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);
                JSONObject ct = playlistJson.getJSONObject("creator");

                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("name");
                String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 网友精选碟(最新)(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getNewPickedPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_NC_API,
                            String.format("{\"cat\":\"%s\",\"order\":\"new\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[1], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = playlistInfoJson.getIntValue("total");
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);
                JSONObject ct = playlistJson.getJSONObject("creator");

                String playlistId = playlistJson.getString("id");
                String name = playlistJson.getString("name");
                String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(name);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
