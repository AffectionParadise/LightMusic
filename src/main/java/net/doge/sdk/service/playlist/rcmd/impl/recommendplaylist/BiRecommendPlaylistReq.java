package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiRecommendPlaylistReq {
    private static BiRecommendPlaylistReq instance;

    private BiRecommendPlaylistReq() {
    }

    public static BiRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new BiRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (哔哩哔哩)
    private final String NEW_PLAYLIST_BI_API = "https://www.bilibili.com/audio/music-service-c/web/menu/hit?pn=%s&ps=%s";
    // 热门歌单 API (哔哩哔哩)
    private final String HOT_PLAYLIST_BI_API = "https://www.bilibili.com/audio/music-service-c/web/menu/rank?pn=%s&ps=%s";
    // 全部歌单 API (哔哩哔哩)
    private final String ALL_PLAYLIST_BI_API = "https://www.bilibili.com/audio/music-service-c/web/home/list-rank?pn=%s&ps=%s";

    /**
     * 推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_BI_API, page, limit))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        t = data.getIntValue("totalSize");
        JSONArray playlistArray = data.getJSONArray("data");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);
            JSONObject statistic = playlistJson.getJSONObject("statistic");

            String playlistId = statistic.getString("sid");
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("uname");
            String creatorId = playlistJson.getString("uid");
            Integer trackCount = playlistJson.getIntValue("snum");
            Long playCount = statistic.getLong("play");
            String coverImgThumbUrl = playlistJson.getString("cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.BI);
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
     * 热门歌单
     */
    public CommonResult<NetPlaylistInfo> getHotPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(HOT_PLAYLIST_BI_API, page, limit))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        t = data.getIntValue("totalSize");
        JSONArray playlistArray = data.getJSONArray("data");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);
            JSONObject statistic = playlistJson.getJSONObject("statistic");

            String playlistId = statistic.getString("sid");
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("uname");
            String creatorId = playlistJson.getString("uid");
            Integer trackCount = playlistJson.getIntValue("snum");
            Long playCount = statistic.getLong("play");
            String coverImgThumbUrl = playlistJson.getString("cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.BI);
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
     * 全部歌单
     */
    public CommonResult<NetPlaylistInfo> getAllPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(ALL_PLAYLIST_BI_API, page, limit))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        t = data.getIntValue("totalSize");
        JSONArray playlistArray = data.getJSONArray("data");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);
            JSONObject statistic = playlistJson.getJSONObject("statistic");

            String playlistId = statistic.getString("sid");
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("uname");
            String creatorId = playlistJson.getString("uid");
            Integer trackCount = playlistJson.getIntValue("snum");
            Long playCount = statistic.getLong("play");
            String coverImgThumbUrl = playlistJson.getString("cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.BI);
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
}
