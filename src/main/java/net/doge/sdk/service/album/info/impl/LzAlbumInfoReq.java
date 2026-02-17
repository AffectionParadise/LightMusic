package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class LzAlbumInfoReq {
    private static LzAlbumInfoReq instance;

    private LzAlbumInfoReq() {
    }

    public static LzAlbumInfoReq getInstance() {
        if (instance == null) instance = new LzAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (李志)
    private final String ALBUM_DETAIL_LZ_API = "https://www.lizhinb.com/%s/";
    // 专辑歌曲 API (李志)
    private final String ALBUM_SONGS_LZ_API = "https://www.lizhinb.com/?audioigniter_playlist_id=%s";

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_LZ_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Element tc = doc.select(".zaxu-alert-tips-content").first();

        String description = HtmlUtil.getPrettyText(tc);

        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(albumInfo.getCoverImgThumbUrl())));
        albumInfo.setDescription(description);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total = 0;

        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_LZ_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements ai = doc.select(".audioigniter-root");
        String aid = RegexUtil.getGroup1("audioigniter-(\\d+)", ai.attr("id"));

        if (StringUtil.notEmpty(aid)) {
            String albumSongBody = HttpRequest.get(String.format(ALBUM_SONGS_LZ_API, aid))
                    .executeAsStr();
            JSONArray songArray = JSONArray.parseArray(albumSongBody);
            total = songArray.size();
            // 获取专辑歌曲同时填充专辑歌曲数
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(total);
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = aid + "_" + i;
                String name = songJson.getString("title");
                String artist = "李志";
                String albumName = albumInfo.getName();
                String albumId = id;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.LZ);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, total);
    }
}
