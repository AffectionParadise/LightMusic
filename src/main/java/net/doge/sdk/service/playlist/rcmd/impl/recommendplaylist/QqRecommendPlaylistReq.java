package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqRecommendPlaylistReq {
    private static QqRecommendPlaylistReq instance;

    private QqRecommendPlaylistReq() {
    }

    public static QqRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new QqRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (QQ)
//    private final String RECOMMEND_PLAYLIST_QQ_API
//            = SdkCommon.PREFIX_QQ + "/recommend/playlist?id=%s&pageNo=1&pageSize=120";
    // 推荐歌单(最新) API (QQ)
    private final String NEW_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";

    /**
     * 每日推荐(程序分页)
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylistsQqDaily(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody("{\"comm\":{\"ct\":24},\"recomPlaylist\":{\"method\":\"get_hot_recommend\",\"param\":{\"async\":1,\"cmd\":2},\"module\":\"playlist.HotRecommendServer\"}}")
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONObject("recomPlaylist").getJSONObject("data").getJSONArray("v_hot");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("content_id");
            String playlistName = playlistJson.getString("title");
            String creator = playlistJson.getString("username");
            Long playCount = playlistJson.getLong("listen_num");
            String coverImgThumbUrl = playlistJson.getString("cover");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetMusicSource.QQ);
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
//     * 推荐歌单(程序分页)
//     */
//    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(String tag, int page, int limit) {
//        List<NetPlaylistInfo> r = new LinkedList<>();
//        int t = 0;
//        String[] s = Tags.recPlaylistTag.get(tag);

    //            if (StringUtil.notEmpty(s[0])) {
//                String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_QQ_API, s[0]))
//                        .executeAsync()
//                        .body();
//                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
//                t = playlistArray.size();
//                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("tid");
//                    String playlistName = playlistJson.getString("title");
//                    String creator = playlistJson.getJSONObject("creator_info").getString("nick");
//                    Long playCount = playlistJson.getLong("access_num");
//                    Integer trackCount = playlistJson.getJSONArray("song_ids").size();
//                    String coverImgThumbUrl = playlistJson.getString("cover_url_big");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.QQ);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    r.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//    }

    /**
     * 分类推荐歌单(最新)(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getNewPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        String param = s[TagType.NEW_PLAYLIST_QQ];
        if (StringUtil.notEmpty(param)) {
            boolean isAll = "10000000".equals(param);
            String url;
            if (isAll) {
                url = NEW_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
                        "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                "\"playlist\":{" +
                                "\"method\":\"get_playlist_by_tag\"," +
                                "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":2,\"cur_page\":%s}," +
                                "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
            } else {
                url = NEW_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
                        "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                "\"playlist\":{" +
                                "\"method\":\"get_category_content\"," +
                                "\"param\":{" +
                                "\"titleid\":%s," +
                                "\"caller\":\"0\"," +
                                "\"category_id\":%s," +
                                "\"size\":%s," +
                                "\"page\":%s," +
                                "\"use_page\":1}," +
                                "\"module\":\"playlist.PlayListCategoryServer\"}}", param, param, limit, page - 1));
            }
            String playlistInfoBody = HttpRequest.get(url)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            if (isAll) {
                JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray playlistArray = data.getJSONArray("v_playlist");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getJSONObject("creator_info").getString("nick");
                    Long playCount = playlistJson.getLong("access_num");
                    String coverImgThumbUrl = playlistJson.getString("cover_url_small");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
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
            } else {
                JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data").getJSONObject("content");
                t = data.getIntValue("total_cnt");
                JSONArray playlistArray = data.getJSONArray("v_item");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("basic");

                    String playlistId = playlistJson.getString("tid");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getJSONObject("creator").getString("nick");
                    Long playCount = playlistJson.getLong("play_cnt");
                    String coverImgThumbUrl = playlistJson.getJSONObject("cover").getString("small_url");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
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
        }
        return new CommonResult<>(r, t);
    }
}
