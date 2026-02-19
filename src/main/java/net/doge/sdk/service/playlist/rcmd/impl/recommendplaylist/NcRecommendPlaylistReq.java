package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.text.LangUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRecommendPlaylistReq {
    private static NcRecommendPlaylistReq instance;

    private NcRecommendPlaylistReq() {
    }

    public static NcRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new NcRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (网易云)
    private final String RECOMMEND_PLAYLIST_NC_API = "https://music.163.com/weapi/personalized/playlist";
    // 发现歌单 API (网易云)
    private final String DISCOVER_PLAYLIST_NC_API = "https://music.163.com/discover/playlist/?order=hot&offset=%s&limit=%s";
    // 曲风歌单 API (网易云)
    private final String STYLE_PLAYLIST_NC_API = "https://music.163.com/api/style-tag/home/playlist";

    /**
     * 发现歌单
     */
    public CommonResult<NetPlaylistInfo> getDiscoverPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        final int lim = Math.min(35, limit);
        String playlistInfoBody = HttpRequest.get(String.format(DISCOVER_PLAYLIST_NC_API, (page - 1) * lim, lim))
                .executeAsStr();
        Document doc = Jsoup.parse(playlistInfoBody);
        Elements playlists = doc.select("ul.m-cvrlst.f-cb li");
        Elements a = doc.select(".u-page a");
        t = Integer.parseInt(a.get(a.size() - 2).text()) * limit;
        for (int i = 0, len = playlists.size(); i < len; i++) {
            Element playlist = playlists.get(i);
            Element pa = playlist.select("p.dec a.tit.f-thide.s-fc0").first();
            Element nb = playlist.select(".bottom span.nb").first();
            Element fc = playlist.select("a.nm.nm-icn.f-thide.s-fc3").first();
            Element img = playlist.select(".u-cover.u-cover-1 img").first();

            String playlistId = RegexUtil.getGroup1("id=(\\d+)", pa.attr("href"));
            String playlistName = pa.text();
            String creator = fc.text();
            String creatorId = RegexUtil.getGroup1("id=(\\d+)", fc.attr("href"));
            Long playCount = LangUtil.parseNumber(nb.text());
            String coverImgThumbUrl = img.attr("src");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
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
     * 发现歌单
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, RECOMMEND_PLAYLIST_NC_API, "{\"n\":1000,\"limit\":100,\"total\":true}", options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONArray("result");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("name");
            Long playCount = playlistJson.getLong("playCount");
            Integer trackCount = playlistJson.getIntValue("trackCount");
            String coverImgThumbUrl = playlistJson.getString("picUrl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
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
     * 曲风歌单
     */
    public CommonResult<NetPlaylistInfo> getStylePlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_PLAYLIST_NC_API,
                            String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", s[0], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("playlist");
            t = data.getJSONObject("page").getIntValue("total");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getString("userName");
                String creatorId = playlistJson.getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getIntValue("songCount");
                String coverImgThumbUrl = playlistJson.getString("cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
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
    }
}
