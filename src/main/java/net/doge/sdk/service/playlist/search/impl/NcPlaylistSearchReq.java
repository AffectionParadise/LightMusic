package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcPlaylistSearchReq {
    private static NcPlaylistSearchReq instance;

    private NcPlaylistSearchReq() {
    }

    public static NcPlaylistSearchReq getInstance() {
        if (instance == null) instance = new NcPlaylistSearchReq();
        return instance;
    }

    // 关键词搜索歌单 API (网易云)
    private final String CLOUD_SEARCH_NC_API = "https://interface.music.163.com/eapi/cloudsearch/pc";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_NC_API,
                        String.format("{\"s\":\"%s\",\"type\":1000,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject result = playlistInfoJson.getJSONObject("result");
        if (result.containsKey("playlists")) {
            t = result.getIntValue("playlistCount");
            JSONArray playlistArray = result.getJSONArray("playlists");
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
