package net.doge.sdk.service.album.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwAlbumSearchReq {
    private static KwAlbumSearchReq instance;

    private KwAlbumSearchReq() {
    }

    public static KwAlbumSearchReq getInstance() {
        if (instance == null) instance = new KwAlbumSearchReq();
        return instance;
    }

    // 关键词搜索专辑 API (酷我)
    private final String SEARCH_ALBUM_KW_API = "https://kuwo.cn/api/www/search/searchAlbumBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 根据关键词获取专辑
     */
    public CommonResult<NetAlbumInfo> searchAlbums(String keyword, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_ALBUM_KW_API, encodedKeyword, page, limit)).execute();
        if (resp.isSuccessful()) {
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("albumList");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = HtmlUtil.removeHtmlLabel(albumJson.getString("album"));
                String artist = albumJson.getString("artist").replace("&", "、");
                String artistId = albumJson.getString("artistid");
                String publishTime = albumJson.getString("releaseDate");
                String coverImgThumbUrl = albumJson.getString("pic");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KW);
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
                r.add(albumInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
