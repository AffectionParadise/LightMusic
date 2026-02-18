package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.LangUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgHighQualityPlaylistReq {
    private static MgHighQualityPlaylistReq instance;

    private MgHighQualityPlaylistReq() {
    }

    public static MgHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new MgHighQualityPlaylistReq();
        return instance;
    }

    // 推荐歌单 API(最热) (咪咕)
    private final String REC_HOT_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=1";
    // 分类歌单 API (咪咕)
    private final String CAT_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-listbytag?tagId=%s&pageNumber=%s&templateVersion=1";

    /**
     * 推荐歌单(最热)
     */
    public CommonResult<NetPlaylistInfo> getRecHotPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(String.format(REC_HOT_PLAYLIST_MG_API, page, limit))
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
//    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(String tag, int page, int limit) {
//        List<NetPlaylistInfo> r = new LinkedList<>();
//        int t;

    //            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
//                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
//            t = data.getIntValue("countSize");
//            JSONArray playlistArray = data.getJSONArray("playlist");
//            for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                String playlistId = playlistJson.getString("playListId");
//                String playlistName = playlistJson.getString("playListName");
//                String creator = playlistJson.getString("createName");
//                Long playCount = playlistJson.getLong("playCount");
//                Integer trackCount = playlistJson.getIntValue("contentCount");
//                String coverImgThumbUrl = playlistJson.getString("image");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.MG);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(playlistName);
//                playlistInfo.setCreator(creator);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(playlistInfo);
//            }
//            return new CommonResult<>(res, t);
//    }

    /**
     * 分类歌单(每页 10 条)
     */
    public CommonResult<NetPlaylistInfo> getCatPlaylists(String tag, int page) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTag.get(tag);

        if (StringUtil.notEmpty(s[6])) {
            String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_MG_API, s[6], page))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data").getJSONObject("contentItemList");
            t = 300;
            JSONArray playlistArray = data.getJSONArray("itemList");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getJSONObject("logEvent").getString("contentId");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("subTitle");
                String fs = playlistJson.getString("playNum");
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
        }
        return new CommonResult<>(r, t);
    }
}
