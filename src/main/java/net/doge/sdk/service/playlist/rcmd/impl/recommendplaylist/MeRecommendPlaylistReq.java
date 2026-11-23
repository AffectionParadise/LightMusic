package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MeRecommendPlaylistReq {
    private static MeRecommendPlaylistReq instance;

    private MeRecommendPlaylistReq() {
    }

    public static MeRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new MeRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (猫耳)
    private final String REC_PLAYLIST_ME_API = "https://www.missevan.com/site/homepage";
    // 分类歌单(最新) API (猫耳)
    private final String NEW_PLAYLIST_ME_API = "https://www.missevan.com/explore/tagalbum";

    /**
     * 推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(REC_PLAYLIST_ME_API)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject info = playlistInfoJson.getJSONObject("info");
        JSONArray playlistArray = info.getJSONArray("albums");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("username");
            String creatorId = playlistJson.getString("user_id");
            Integer trackCount = playlistJson.getIntValue("music_count");
            Long playCount = playlistJson.getLong("view_count");
            String coverImgThumbUrl = playlistJson.getString("front_cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.ME);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            playlistInfo.setTrackCount(trackCount);
            playlistInfo.setPlayCount(playCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(playlistInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类歌单(最新)
     */
    public CommonResult<NetPlaylistInfo> getNewPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        String param = s[TagModule.NEW_PLAYLIST_ME];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("order", 1);
            params.put("tid", param.trim());
            params.put("p", page);
            params.put("pagesize", limit);
            String playlistInfoBody = HttpRequest.get(NEW_PLAYLIST_ME_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = playlistInfoJson.getJSONObject("pagination").getIntValue("count");
            JSONArray playlistArray = playlistInfoJson.getJSONArray("albums");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("username");
                String creatorId = playlistJson.getString("user_id");
                Integer trackCount = playlistJson.getIntValue("music_count");
                String coverImgThumbUrl = playlistJson.getString("front_cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.ME);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
