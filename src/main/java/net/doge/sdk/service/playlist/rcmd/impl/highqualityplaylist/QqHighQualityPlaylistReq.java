package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqHighQualityPlaylistReq {
    private static QqHighQualityPlaylistReq instance;

    private QqHighQualityPlaylistReq() {
    }

    public static QqHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new QqHighQualityPlaylistReq();
        return instance;
    }

    // 分类歌单 API (QQ)
    private final String CAT_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";

    /**
     * 分类推荐歌单(接口分页)
     */
    public CommonResult<NetPlaylistInfo> getCatPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        String param = s[TagType.CAT_PLAYLIST_QQ];
        if (StringUtil.notEmpty(param)) {
            boolean isAll = "10000000".equals(param);
            String url;
            if (isAll) {
                url = CAT_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
                        "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                "\"playlist\":{" +
                                "\"method\":\"get_playlist_by_tag\"," +
                                "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":5,\"cur_page\":%s}," +
                                "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
            } else {
                url = CAT_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
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
