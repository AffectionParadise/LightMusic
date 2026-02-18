package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class FsPlaylistSearchReq {
    private static FsPlaylistSearchReq instance;

    private FsPlaylistSearchReq() {
    }

    public static FsPlaylistSearchReq getInstance() {
        if (instance == null) instance = new FsPlaylistSearchReq();
        return instance;
    }

    // 关键词搜索歌单 API (5sing)
    private final String SEARCH_PLAYLIST_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=0&type=1";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_FS_API, encodedKeyword, page))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(playlistInfoBody);
        t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
        JSONArray playlistArray = data.getJSONArray("list");
        if (JsonUtil.notEmpty(playlistArray)) {
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("songListId");
                String playlistName = HtmlUtil.removeHtmlLabel(playlistJson.getString("title"));
                String creator = playlistJson.getString("userName");
                String creatorId = playlistJson.getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("songCnt");
                String coverImgThumbUrl = playlistJson.getString("pictureUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.FS);
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
