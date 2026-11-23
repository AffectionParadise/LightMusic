package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KwArtistMenuReq {
    private static KwArtistMenuReq instance;

    private KwArtistMenuReq() {
    }

    public static KwArtistMenuReq getInstance() {
        if (instance == null) instance = new KwArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (酷我)
    private final String ARTIST_ALBUMS_KW_API = "https://kuwo.cn/api/www/artist/artistAlbum";
    // 歌手 MV API (酷我)
    private final String ARTIST_MVS_KW_API = "https://kuwo.cn/api/www/artist/artistMv";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total = 0;

        String id = artistInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("artistid", id);
        params.put("pn", page);
        params.put("rn", limit);
        params.put("httpsStatus", 1);
        HttpResponse resp = SdkCommon.kwRequest(ARTIST_ALBUMS_KW_API)
                .queryParams(params)
                .referer("https://kuwo.cn/singer_detail/" + UrlUtil.encodeAll(id) + "/album")
                .execute();
        if (resp.isSuccessful()) {
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("albumList");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = HtmlUtil.stripHtml(albumJson.getString("album"));
                String artist = HtmlUtil.stripHtml(albumJson.getString("artist")).replace("&", "、");
                String artistId = albumJson.getString("artistid");
                String publishTime = albumJson.getString("releaseDate");
                String coverImgThumbUrl = albumJson.getString("pic");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetResourceSource.KW);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int total = 0;

        String id = artistInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("artistid", id);
        params.put("pn", page);
        params.put("rn", limit);
        params.put("httpsStatus", 1);
        HttpResponse resp = SdkCommon.kwRequest(ARTIST_MVS_KW_API)
                .queryParams(params)
                .referer("https://kuwo.cn/singer_detail/" + UrlUtil.encodeAll(id) + "/mv")
                .execute();
        if (resp.isSuccessful()) {
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("mvlist");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = mvJson.getString("artist").replace("&", "、");
                String creatorId = mvJson.getString("artistid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("mvPlayCnt");
                Double duration = mvJson.getDouble("duration");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.KW);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        return new CommonResult<>(res, total);
    }
}
