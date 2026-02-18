package net.doge.sdk.service.playlist.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwPlaylistSearchReq {
    private static KwPlaylistSearchReq instance;

    private KwPlaylistSearchReq() {
    }

    public static KwPlaylistSearchReq getInstance() {
        if (instance == null) instance = new KwPlaylistSearchReq();
        return instance;
    }

    // 关键词搜索歌单 API (酷我)
//    private final String SEARCH_PLAYLIST_KW_API = "https://kuwo.cn/api/www/search/searchPlayListBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String SEARCH_PLAYLIST_KW_API = "http://search.kuwo.cn/r.s?all=%s&pn=%s&rn=%s&rformat=json&encoding=utf8&ver=mbox&vipver=MUSIC_8.7.7.0_BCS37&plat=pc&devid=28156413&ft=playlist&pay=0&needliveshow=0";

    /**
     * 根据关键词获取歌单
     */
    public CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        //            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page, limit)).executeAsync();
//            if (resp.isSuccessful()) {
//                String playlistInfoBody = resp.body();
//                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//                JSONObject data = playlistInfoJson.getJSONObject("data");
//                t = data.getIntValue("total");
//                JSONArray playlistArray = data.getJSONArray("list");
//                for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("id");
//                    String playlistName = StringUtil.removeHTMLLabel(playlistJson.getString("name"));
//                    String creator = playlistJson.getString("uname");
//                    Long playCount = playlistJson.getLong("listencnt");
//                    Integer trackCount = playlistJson.getIntValue("total");
//                    String coverImgThumbUrl = playlistJson.getString("img");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.KW);
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
//                    r.add(playlistInfo);
//                }
//            }
        String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page - 1, limit))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(playlistInfoBody);
        t = data.getIntValue("TOTAL");
        JSONArray playlistArray = data.getJSONArray("abslist");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("playlistid");
            String playlistName = HtmlUtil.removeHtmlLabel(playlistJson.getString("name"));
            String creator = playlistJson.getString("nickname");
            Long playCount = playlistJson.getLong("playcnt");
            Integer trackCount = playlistJson.getIntValue("songnum");
            String coverImgThumbUrl = playlistJson.getString("pic");

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

        return new CommonResult<>(r, t);
    }
}
