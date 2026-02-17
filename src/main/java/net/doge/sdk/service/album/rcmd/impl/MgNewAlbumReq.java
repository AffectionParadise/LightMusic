package net.doge.sdk.service.album.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgNewAlbumReq {
    private static MgNewAlbumReq instance;

    private MgNewAlbumReq() {
    }

    public static MgNewAlbumReq getInstance() {
        if (instance == null) instance = new MgNewAlbumReq();
        return instance;
    }

    // 新碟推荐 API (咪咕)
    private final String NEW_ALBUM_MG_API = "http://m.music.migu.cn/migu/remoting/cms_list_tag?nid=23854016&pageNo=%s&pageSize=%s&type=2003";
    // 新专辑榜 API (咪咕)
    private final String NEW_ALBUM_RANKING_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=23218151";

    /**
     * 新碟推荐(接口分页)
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = HttpRequest.get(String.format(NEW_ALBUM_MG_API, page - 1, limit))
                .header(Header.REFERER, "https://m.music.migu.cn/")
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("result");
        t = data.getIntValue("totalCount");
        JSONArray albumArray = data.getJSONArray("results");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i).getJSONObject("albumData");

            String albumId = albumJson.getString("albumId");
            String albumName = albumJson.getString("albumName");
            String[] des = albumJson.getString("albumsDes").split("\n");
            String artist = des[1].split("：")[1];
            String artistId = albumJson.getString("singerId");
            String publishTime = des[5].split("：")[1];
            Integer songNum = Integer.parseInt(des[4].split("：")[1]);
            String coverImgThumbUrl = albumJson.getString("albumsPicUrl");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.MG);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setArtistId(artistId);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            albumInfo.setPublishTime(publishTime);
            albumInfo.setSongNum(songNum);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 新专辑榜(程序分页)
     */
    public CommonResult<NetAlbumInfo> getNewAlbumsRanking(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = HttpRequest.get(NEW_ALBUM_RANKING_MG_API)
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("columnInfo");
        t = data.getIntValue("contentsCount");
        JSONArray albumArray = data.getJSONArray("contents");
        for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i).getJSONObject("objectInfo");

            String albumId = albumJson.getString("albumId");
            String albumName = albumJson.getString("title");
            String artist = albumJson.getString("singer").replace("|", "、");
            String artistId = albumJson.getString("singerId").split("\\|")[0];
            String publishTime = albumJson.getString("publishTime");
            Integer songNum = albumJson.getIntValue("totalCount");
            String coverImgThumbUrl = albumJson.getJSONArray("imgItems").getJSONObject(0).getString("img");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.MG);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setArtistId(artistId);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            albumInfo.setPublishTime(publishTime);
            albumInfo.setSongNum(songNum);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }
}
