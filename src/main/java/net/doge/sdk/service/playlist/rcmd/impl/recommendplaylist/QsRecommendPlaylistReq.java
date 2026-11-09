package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QsRecommendPlaylistReq {
    private static QsRecommendPlaylistReq instance;

    private QsRecommendPlaylistReq() {
    }

    public static QsRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new QsRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (汽水)
    private final String REC_PLAYLIST_QS_API = "https://api.qishui.com/luna/feed/playlist-square";
    // 发现歌单 API (汽水)
    private final String DISCOVER_PLAYLIST_QS_API = "https://api.qishui.com/luna/pc/discover/mix";

    /**
     * 推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.post(REC_PLAYLIST_QS_API)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONArray("items");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("entity").getJSONObject("playlist");
            JSONObject owner = playlistJson.getJSONObject("owner");
            JSONObject stats = playlistJson.getJSONObject("stats");

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("title");
            String creator = JsonUtil.isEmpty(owner) ? "" : owner.getString("nickname");
            String creatorId = JsonUtil.isEmpty(owner) ? "" : owner.getString("id");
            Long playCount = stats.getLong("count_visible");
            Integer trackCount = playlistJson.getIntValue("count_tracks");
            String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(playlistJson);

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
        return new CommonResult<>(r, t);
    }

    /**
     * 发现歌单
     */
    public CommonResult<NetPlaylistInfo> getDiscoverPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.post(DISCOVER_PLAYLIST_QS_API)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONArray("inner_block");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject blockJson = playlistArray.getJSONObject(i);
            JSONObject resource = blockJson.getJSONArray("resources").getJSONObject(0);
            JSONObject playlistJson = resource.getJSONObject("entity").getJSONObject("playlist");
            JSONObject owner = playlistJson.getJSONObject("owner");
            JSONObject stats = playlistJson.getJSONObject("stats");

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("title");
            String creator = JsonUtil.isEmpty(owner) ? "" : owner.getString("nickname");
            String creatorId = JsonUtil.isEmpty(owner) ? "" : owner.getString("id");
            Long playCount = stats.getLong("count_visible");
            Integer trackCount = playlistJson.getIntValue("count_tracks");
            String coverImgThumbUrl = SdkUtil.parseQsCoverImgUrl(playlistJson);

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
        return new CommonResult<>(r, t);
    }
}
