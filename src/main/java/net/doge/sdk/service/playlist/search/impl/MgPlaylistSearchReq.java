package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgPlaylistSearchReq {
    private static MgPlaylistSearchReq instance;

    private MgPlaylistSearchReq() {
    }

    public static MgPlaylistSearchReq getInstance() {
        if (instance == null) instance = new MgPlaylistSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = SdkCommon.mgSearchRequest("playlist", keyword, page, limit)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody).getJSONObject("songListResultData");
        t = playlistInfoJson.getIntValue("totalCount");
        JSONArray playlistArray = playlistInfoJson.getJSONArray("result");
        if (JsonUtil.notEmpty(playlistArray)) {
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getString("userName");
                String creatorId = playlistJson.getString("userId");
                Long playCount = playlistJson.getLong("playNum");
                Integer trackCount = playlistJson.getIntValue("musicNum");
                String coverImgThumbUrl = playlistJson.getString("musicListPicUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
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
        //            String playlistInfoBody = SdkCommon.mgSearchRequest("playlist", keyword, page, limit)
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("songListResultData");
//            t = data.getIntValue("totalCount");
//            JSONArray playlistArray = data.getJSONArray("result");
//            if (JsonUtil.notEmpty(playlistArray)) {
//                for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("id");
//                    String playlistName = playlistJson.getString("name");
//                    String creator = playlistJson.getString("userName");
//                    String creatorId = playlistJson.getString("userId");
//                    Long playCount = playlistJson.getLong("playNum");
//                    Integer trackCount = playlistJson.getIntValue("musicNum");
//                    String coverImgThumbUrl = playlistJson.getString("musicListPicUrl");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.MG);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCreatorId(creatorId);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//                    r.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
    }
}
