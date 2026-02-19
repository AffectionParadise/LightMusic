package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwHighQualityPlaylistReq {
    private static KwHighQualityPlaylistReq instance;

    private KwHighQualityPlaylistReq() {
    }

    public static KwHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new KwHighQualityPlaylistReq();
        return instance;
    }

    // 热门歌单 API (酷我)
    private final String HOT_PLAYLIST_KW_API = "https://kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot&httpsStatus=1";
    // 默认歌单(热门) API (酷我)
    private final String DEFAULT_PLAYLIST_KW_API = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot";
    // 分类歌单 API (酷我)
    private final String CAT_PLAYLIST_KW_API = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?loginUid=0&loginSid=0&appUid=76039576&id=%s&pn=%s&rn=%s";
    // 分类歌单 API 2 (酷我)
    private final String CAT_PLAYLIST_KW_API_2 = "http://mobileinterfaces.kuwo.cn/er.s?type=get_pc_qz_data&f=web&id=%s&prod=pc";

    /**
     * 热门歌单
     */
    public CommonResult<NetPlaylistInfo> getHotPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = HttpRequest.get(String.format(HOT_PLAYLIST_KW_API, page, limit))
                .execute();
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

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 默认歌单(热门)(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getDefaultPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;

        HttpResponse resp = HttpRequest.get(String.format(DEFAULT_PLAYLIST_KW_API, page, limit)).execute();
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

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类歌单(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getCatPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[5])) {
            String[] sp = s[5].split(" ");
            // 根据 digest 信息请求不同的分类歌单接口
            if ("43".equals(sp[1])) {
                HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API_2, sp[0])).execute();
                if (resp.isSuccessful()) {
                    String playlistInfoBody = resp.body();
                    JSONArray playlistArray = JSONArray.parseArray(playlistInfoBody);
                    for (int i = 0, l = playlistArray.size(); i < l; i++) {
                        JSONArray list = playlistArray.getJSONObject(i).getJSONArray("list");
                        for (int j = 0, k = list.size(); j < k; j++, t++) {
                            if (t >= (page - 1) * limit && t < page * limit) {
                                JSONObject playlistJson = list.getJSONObject(j);

                                String playlistId = playlistJson.getString("id");
                                String playlistName = playlistJson.getString("name");
                                String coverImgThumbUrl = playlistJson.getString("img");

                                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                                playlistInfo.setSource(NetMusicSource.KW);
                                playlistInfo.setId(playlistId);
                                playlistInfo.setName(playlistName);
                                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                                GlobalExecutors.imageExecutor.execute(() -> {
                                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                                    playlistInfo.setCoverImgThumb(coverImgThumb);
                                });

                                r.add(playlistInfo);
                            }
                        }
                    }
                }
            } else {
                HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API, sp[0], page, limit)).execute();
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

                        r.add(playlistInfo);
                    }
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
