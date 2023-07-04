package net.doge.sdk.playlist.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetPlaylistInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.ListUtil;
import net.doge.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaylistSearchReq {
    // 关键词搜索歌单 API
    private final String SEARCH_PLAYLIST_API = SdkCommon.prefix + "/cloudsearch?type=1000&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索歌单 API (酷狗)
    private final String SEARCH_PLAYLIST_KG_API = "http://mobilecdnbj.kugou.com/api/v3/search/special?filter=0&keyword=%s&page=%s&pagesize=%s";
    // 关键词搜索歌单 API (酷我)
    private final String SEARCH_PLAYLIST_KW_API = "http://www.kuwo.cn/api/www/search/searchPlayListBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索歌单 API (咪咕)
    private final String SEARCH_PLAYLIST_MG_API = SdkCommon.prefixMg + "/search?type=playlist&keyword=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌单 API (5sing)
    private final String SEARCH_PLAYLIST_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=0&type=1";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(int src, String keyword, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.encode(keyword);

        // 网易云
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject result = playlistInfoJson.getJSONObject("result");
            if (result.has("playlists")) {
                t = result.getInt("playlistCount");
                JSONArray playlistArray = result.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.optJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = ct != null ? ct.getString("nickname") : "";
                    String creatorId = ct != null ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray playlistArray = data.getJSONArray("info");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.getInt("songcount");
                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
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
                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.post(String.format(SdkCommon.qqSearchApi))
                    .body(String.format(SdkCommon.qqSearchJson, page, limit, keyword, 3))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray playlistArray = data.getJSONObject("body").getJSONObject("songlist").getJSONArray("list");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("dissid");
                String playlistName = playlistJson.getString("dissname");
                String creator = playlistJson.getJSONObject("creator").getString("name");
                Long playCount = playlistJson.getLong("listennum");
                Integer trackCount = playlistJson.getInt("song_count");
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
                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("list");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = StringUtil.removeHTMLLabel(playlistJson.getString("name"));
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
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
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.optJSONObject("data");
            if (data != null) {
                t = data.optInt("total");
                JSONArray playlistArray = data.getJSONArray("list");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("name");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("songCount");
                    String coverImgThumbUrl = playlistJson.getString("picUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 5sing
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsFs = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_FS_API, encodedKeyword, page))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(playlistInfoBody);
            t = data.getJSONObject("pageInfo").getInt("totalPages") * limit;
            JSONArray playlistArray = data.optJSONArray("list");
            if (playlistArray != null) {
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("songListId");
                    String playlistName = StringUtil.removeHTMLLabel(playlistJson.getString("title"));
                    String creator = playlistJson.getString("userName");
                    String creatorId = playlistJson.getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("songCnt");
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
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylists));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKw));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsMg));
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsFs));

        List<List<NetPlaylistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetPlaylistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        playlistInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(playlistInfos, total.get());
    }
}
