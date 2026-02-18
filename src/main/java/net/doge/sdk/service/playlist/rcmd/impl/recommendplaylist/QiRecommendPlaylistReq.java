package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiRecommendPlaylistReq {
    private static QiRecommendPlaylistReq instance;

    private QiRecommendPlaylistReq() {
    }

    public static QiRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new QiRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (千千)
    private final String REC_PLAYLIST_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";

    /**
     * 推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = SdkCommon.qiRequest(String.format(REC_PLAYLIST_QI_API, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray dataArray = playlistInfoJson.getJSONArray("data");
        JSONObject data = SdkUtil.findFeatureObj(dataArray, "type", "tracklist");
        t = data.getIntValue("module_nums");
        JSONArray playlistArray = data.getJSONArray("result");
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("title");
            Integer trackCount = playlistJson.getIntValue("trackCount");
            String coverImgThumbUrl = playlistJson.getString("pic");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.QI);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
