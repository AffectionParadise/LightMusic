package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwRecommendPlaylistReq {
    private static KwRecommendPlaylistReq instance;

    private KwRecommendPlaylistReq() {
    }

    public static KwRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new KwRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (酷我)
    private final String RECOMMEND_PLAYLIST_KW_API = "https://kuwo.cn/api/www/rcm/index/playlist?loginUid=0&httpsStatus=1";
    // 推荐歌单(最新) API (酷我)
    private final String NEW_PLAYLIST_KW_API = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=new";

    /**
     * 推荐歌单(程序分页)
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = SdkCommon.kwRequest(RECOMMEND_PLAYLIST_KW_API).execute();
        if (resp.isSuccessful()) {
            String playlistInfoBody = resp.body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getString("uname");
                Long playCount = playlistJson.getLong("listencnt");
                Integer trackCount = playlistJson.getIntValue("total");
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.KW);
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
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 推荐歌单(最新)(程序分页)
     */
    public CommonResult<NetPlaylistInfo> getNewPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = HttpRequest.get(String.format(NEW_PLAYLIST_KW_API, page, limit)).execute();
        if (resp.isSuccessful()) {
            String playlistInfoBody = resp.body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray playlistArray = data.getJSONArray("data");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getString("uname");
                Long playCount = playlistJson.getLong("listencnt");
                Integer trackCount = playlistJson.getIntValue("total");
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.KW);
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
        }
        return new CommonResult<>(r, t);
    }
}
