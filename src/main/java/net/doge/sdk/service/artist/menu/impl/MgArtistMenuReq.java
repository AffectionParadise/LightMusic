package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgArtistMenuReq {
    private static MgArtistMenuReq instance;

    private MgArtistMenuReq() {
    }

    public static MgArtistMenuReq getInstance() {
        if (instance == null) instance = new MgArtistMenuReq();
        return instance;
    }

    // 歌手专辑 API (咪咕)
//    private final String ARTIST_ALBUMS_MG_API = "http://music.migu.cn/v3/music/artist/%s/album?page=%s";
    private final String ARTIST_ALBUMS_MG_API = "http://app.c.nf.migu.cn/pc/bmw/singer/album/v1.0?singerId=%s&pageNo=%s";

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
//        String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_MG_API, id, page))
//                .executeAsStr();
//        Document doc = Jsoup.parse(albumInfoBody);
//        Elements pageElem = doc.select(".views-pagination .pagination-item");
//        total = !pageElem.isEmpty() ? Integer.parseInt(pageElem.get(pageElem.size() - 1).text()) * limit : limit;
//        Elements albumArray = doc.select(".artist-album-list li");
//        for (int i = 0, len = albumArray.size(); i < len; i++) {
//            Element album = albumArray.get(i);
//            Elements a = album.select("a.album-name");
//            Elements sa = album.select(".album-singers a");
//            Elements img = album.select(".thumb-link img");
//
//            String albumId = RegexUtil.getGroup1("album/(\\d+)", a.attr("href"));
//            String albumName = a.text();
//            StringJoiner sj = new StringJoiner("、");
//            sa.forEach(aElem -> sj.add(aElem.text()));
//            String artist = sj.toString();
//            String artistId = sa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/music/artist/(\\d+)", sa.get(0).attr("href"));
//            String coverImgThumbUrl = "https:" + img.attr("data-original");
//
//            NetAlbumInfo albumInfo = new NetAlbumInfo();
//            albumInfo.setSource(NetMusicSource.MG);
//            albumInfo.setId(albumId);
//            albumInfo.setName(albumName);
//            albumInfo.setArtist(artist);
//            albumInfo.setArtistId(artistId);
//            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//            GlobalExecutors.imageExecutor.execute(() -> {
//                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                albumInfo.setCoverImgThumb(coverImgThumb);
//            });
//            res.add(albumInfo);
//        }

        String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_MG_API, id, page))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(albumInfoBody).getJSONObject("data");
        // 根据是否返回下一页 url 判断
        boolean hasNextPage = data.getJSONObject("header").containsKey("nextPageUrl");
        total = page * limit + (hasNextPage ? 1 : 0);
        JSONArray albumArray = data.getJSONArray("contents");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);

            String albumId = albumJson.getString("resId");
            String albumName = albumJson.getString("txt");
            String artist = albumJson.getString("txt2");
            String publishTime = albumJson.getString("txt3");
            String coverImgThumbUrl = albumJson.getString("img");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.MG);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setPublishTime(publishTime);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });
            res.add(albumInfo);
        }

        return new CommonResult<>(res, total);
    }
}
