package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.text.LangUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgRecommendPlaylistReq {
    private static MgRecommendPlaylistReq instance;

    private MgRecommendPlaylistReq() {
    }

    public static MgRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new MgRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API(最新) (咪咕)
    private final String REC_NEW_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=2";
    // 推荐歌单(最热) API (咪咕)
    private final String RECOMMEND_PLAYLIST_MG_API = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127315&startIndex=%s";
    // 最新歌单 API (咪咕)
    private final String NEW_PLAYLIST_MG_API = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127272&startIndex=%s";

    /**
     * 推荐歌单(最新)
     */
    public CommonResult<NetPlaylistInfo> getRecNewPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(REC_NEW_PLAYLIST_MG_API, page, limit))
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data").getJSONArray("contentItemList").getJSONObject(0);
        t = 1000;
        JSONArray playlistArray = data.getJSONArray("itemList");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = RegexUtil.getGroup1("id=(\\d+)", playlistJson.getString("actionUrl"));
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("subTitle");
            String fs = playlistJson.getJSONArray("barList").getJSONObject(0).getString("title");
            Long playCount = LangUtil.parseNumber(fs);
            String coverImgThumbUrl = playlistJson.getString("imageUrl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.MG);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
     * 推荐歌单(每页固定 10 条)
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int page) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
                .header(Header.REFERER, "https://m.music.migu.cn/")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("retMsg");
        t = data.getIntValue("countSize");
        JSONArray playlistArray = data.getJSONArray("playlist");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("playListId");
            String playlistName = playlistJson.getString("playListName");
            String creator = playlistJson.getString("createName");
            Long playCount = playlistJson.getLong("playCount");
            Integer trackCount = playlistJson.getIntValue("contentCount");
            String coverImgThumbUrl = playlistJson.getString("image");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.MG);
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

    /**
     * 最新歌单(每页固定 10 条)
     */
    public CommonResult<NetPlaylistInfo> getNewPlaylists(int page) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_MG_API, (page - 1) * 10))
                .header(Header.REFERER, "https://m.music.migu.cn/")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("retMsg");
        t = data.getIntValue("countSize");
        JSONArray playlistArray = data.getJSONArray("playlist");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("playListId");
            String playlistName = playlistJson.getString("playListName");
            String creator = playlistJson.getString("createName");
            Long playCount = playlistJson.getLong("playCount");
            Integer trackCount = playlistJson.getIntValue("contentCount");
            String coverImgThumbUrl = playlistJson.getString("image");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.MG);
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
