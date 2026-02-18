package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqPlaylistSearchReq {
    private static QqPlaylistSearchReq instance;

    private QqPlaylistSearchReq() {
    }

    public static QqPlaylistSearchReq getInstance() {
        if (instance == null) instance = new QqPlaylistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 3))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
        t = data.getJSONObject("meta").getIntValue("sum");
        JSONArray playlistArray = data.getJSONObject("body").getJSONObject("songlist").getJSONArray("list");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("dissid");
            String playlistName = playlistJson.getString("dissname");
            String creator = playlistJson.getJSONObject("creator").getString("name");
            Long playCount = playlistJson.getLong("listennum");
            Integer trackCount = playlistJson.getIntValue("song_count");
            String coverImgThumbUrl = playlistJson.getString("imgurl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.QQ);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
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
