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

    // 歌单广场 API (咪咕)
    private final String SQUARE_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/pc/bmw/page-data/playlist-square-recommend/v1.0?templateVersion=2";
    // 首页推荐歌单 API (咪咕)
    private final String INDEX_REC_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/resource-dataloader/recommend-playlist/v1.0?scene=recommend_playlist";
    // 推荐歌单 API(最新) (咪咕)
    private final String REC_NEW_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=2";
    // 推荐歌单(最热) API (咪咕)
//    private final String RECOMMEND_PLAYLIST_MG_API = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127315&startIndex=%s";
    // 最新歌单 API (咪咕)
//    private final String NEW_PLAYLIST_MG_API = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127272&startIndex=%s";

    /**
     * 歌单广场
     */
    public CommonResult<NetPlaylistInfo> getSquarePlaylists(int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(SQUARE_PLAYLIST_MG_API)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        JSONArray contents = data.getJSONArray("contents");
        t = limit;
        for (int i = 0, len = contents.size(); i < len; i++) {
            JSONObject content = contents.getJSONObject(i);
            if (content.getString("title").contains("标题")) continue;
            JSONArray subContents = content.getJSONArray("contents");
            for (int j = 0, s = subContents.size(); j < s; j++) {
                JSONObject subContent = subContents.getJSONObject(j);
                if (subContent.containsKey("title") && subContent.getString("title").contains("标题")) continue;

                String playlistId = null;
                String playlistName = null;
                String coverImgThumbUrl = null;

                if (subContent.containsKey("contents")) {
                    JSONArray ssContents = subContent.getJSONArray("contents");
                    for (int k = 0, ss = ssContents.size(); k < ss; k++) {
                        JSONObject playlistJson = ssContents.getJSONObject(k);
                        playlistId = playlistJson.getString("resId");
                        playlistName = playlistJson.getString("txt");
                        coverImgThumbUrl = playlistJson.getString("img");
                    }
                } else {
                    playlistId = subContent.getString("resId");
                    playlistName = subContent.getString("txt");
                    coverImgThumbUrl = subContent.getString("img");
                }

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                String finalCoverImgThumbUrl = coverImgThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(finalCoverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 首页推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getIndexRecPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(INDEX_REC_PLAYLIST_MG_API)
                .header("DeviceId", "D05C5D20-E570-48DF-A717-78D00B4FC5C5")
                .header("Recommendstatus", "1")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        JSONArray playlistArray = data.getJSONArray("playLists");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("simpleMusicListItem");

            String playlistId = playlistJson.getString("musicListId");
            String playlistName = playlistJson.getString("title");
//            String creator = playlistJson.getString("createName");
            String creatorId = playlistJson.getString("ownerId");
            Long playCount = playlistJson.getJSONObject("opNumItem").getLong("playNum");
            Integer trackCount = playlistJson.getIntValue("musicNum");
            String coverImgThumbUrl = playlistJson.getJSONObject("imgItem").getString("img");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.MG);
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
//            playlistInfo.setCreator(creator);
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

//    /**
//     * 推荐歌单(每页固定 10 条)
//     */
//    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int page) {
//        List<NetPlaylistInfo> r = new LinkedList<>();
//        int t;
//
//        String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
//                .header(Header.REFERER, "https://m.music.migu.cn/")
//                .executeAsStr();
//        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//        JSONObject data = playlistInfoJson.getJSONObject("retMsg");
//        t = data.getIntValue("countSize");
//        JSONArray playlistArray = data.getJSONArray("playlist");
//        for (int i = 0, len = playlistArray.size(); i < len; i++) {
//            JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//            String playlistId = playlistJson.getString("playListId");
//            String playlistName = playlistJson.getString("playListName");
//            String creator = playlistJson.getString("createName");
//            Long playCount = playlistJson.getLong("playCount");
//            Integer trackCount = playlistJson.getIntValue("contentCount");
//            String coverImgThumbUrl = playlistJson.getString("image");
//
//            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//            playlistInfo.setSource(NetMusicSource.MG);
//            playlistInfo.setId(playlistId);
//            playlistInfo.setName(playlistName);
//            playlistInfo.setCreator(creator);
//            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//            playlistInfo.setPlayCount(playCount);
//            playlistInfo.setTrackCount(trackCount);
//            GlobalExecutors.imageExecutor.execute(() -> {
//                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                playlistInfo.setCoverImgThumb(coverImgThumb);
//            });
//
//            r.add(playlistInfo);
//        }
//        return new CommonResult<>(r, t);
//    }

//    /**
//     * 最新歌单(每页固定 10 条)
//     */
//    public CommonResult<NetPlaylistInfo> getNewPlaylists(int page) {
//        List<NetPlaylistInfo> r = new LinkedList<>();
//        int t;
//
//        String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_MG_API, (page - 1) * 10))
//                .header(Header.REFERER, "https://m.music.migu.cn/")
//                .executeAsStr();
//        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//        JSONObject data = playlistInfoJson.getJSONObject("retMsg");
//        t = data.getIntValue("countSize");
//        JSONArray playlistArray = data.getJSONArray("playlist");
//        for (int i = 0, len = playlistArray.size(); i < len; i++) {
//            JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//            String playlistId = playlistJson.getString("playListId");
//            String playlistName = playlistJson.getString("playListName");
//            String creator = playlistJson.getString("createName");
//            Long playCount = playlistJson.getLong("playCount");
//            Integer trackCount = playlistJson.getIntValue("contentCount");
//            String coverImgThumbUrl = playlistJson.getString("image");
//
//            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//            playlistInfo.setSource(NetMusicSource.MG);
//            playlistInfo.setId(playlistId);
//            playlistInfo.setName(playlistName);
//            playlistInfo.setCreator(creator);
//            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//            playlistInfo.setPlayCount(playCount);
//            playlistInfo.setTrackCount(trackCount);
//            GlobalExecutors.imageExecutor.execute(() -> {
//                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                playlistInfo.setCoverImgThumb(coverImgThumb);
//            });
//
//            r.add(playlistInfo);
//        }
//        return new CommonResult<>(r, t);
//    }
}
