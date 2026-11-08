package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.constant.qs.QsSearchType;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QsPlaylistSearchReq {
    private static QsPlaylistSearchReq instance;

    private QsPlaylistSearchReq() {
    }

    public static QsPlaylistSearchReq getInstance() {
        if (instance == null) instance = new QsPlaylistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        String playlistInfoBody = SdkCommon.qsSearchRequest(QsSearchType.PLAYLIST, keyword, page, limit)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray resultGroups = playlistInfoJson.getJSONArray("result_groups");
        if (JsonUtil.notEmpty(resultGroups)) {
            JSONObject result = resultGroups.getJSONObject(0);
            JSONArray playlistArray = result.getJSONArray("data");
            t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("entity").getJSONObject("playlist");
                JSONObject owner = playlistJson.getJSONObject("owner");
                JSONObject stats = playlistJson.getJSONObject("stats");

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                String creator = JsonUtil.isEmpty(owner) ? "" : owner.getString("nickname");
                String creatorId = JsonUtil.isEmpty(owner) ? "" : owner.getString("id");
                Long playCount = stats.getLong("count_visible");
                Integer trackCount = playlistJson.getIntValue("count_tracks");
                // 封面
                JSONObject coverJson = playlistJson.getJSONObject("url_cover");
                String uri = coverJson.getString("uri");
                String url = coverJson.getJSONArray("urls").getString(0);
                String templatePrefix = coverJson.getString("template_prefix");
                String coverImgThumbUrl = String.format("%s%s~%s-resize:500:500.jpg", url, uri, templatePrefix);

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.QS);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
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
