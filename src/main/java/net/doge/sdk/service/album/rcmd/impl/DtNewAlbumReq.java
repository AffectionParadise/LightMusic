package net.doge.sdk.service.album.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DtNewAlbumReq {
    private static DtNewAlbumReq instance;

    private DtNewAlbumReq() {
    }

    public static DtNewAlbumReq getInstance() {
        if (instance == null) instance = new DtNewAlbumReq();
        return instance;
    }

    // 热门推荐专辑 API (堆糖)
    private final String REC_ALBUM_DT_API = "https://www.duitang.com/napi/index/hot/?include_fields=top_comments,is_root,source_link,item," +
            "buyable,root_id,status,like_count,sender,album&start=%s&limit=%s&_=%s";
    // 分类专辑 API (堆糖)
    private final String CAT_ALBUM_DT_API
            = "https://www.duitang.com/napi/blog/list/by_filter_id/?include_fields=top_comments,is_root,source_link,item,buyable,root_id," +
            "status,like_count,sender,album,reply_count&filter_id=%s&start=%s&limit=%s&_=%s";

    /**
     * 推荐专辑
     */
    public CommonResult<NetAlbumInfo> getRecAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        HttpResponse resp = HttpRequest.get(String.format(REC_ALBUM_DT_API, (page - 1) * limit, limit, System.currentTimeMillis())).execute();
        String albumInfoBody = resp.body();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray albumArray = data.getJSONArray("object_list");
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject mainJson = albumArray.getJSONObject(i);
            JSONObject albumJson = mainJson.getJSONObject("album");
            JSONObject sender = mainJson.getJSONObject("sender");

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = sender.getString("username");
            String artistId = sender.getString("id");
//                String publishTime = TimeUtils.msToDate(mainJson.getLong("add_datetime_ts") * 1000);
            String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
            Integer songNum = albumJson.getIntValue("count");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.DT);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setArtistId(artistId);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setPublishTime(publishTime);
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
     * 分类专辑
     */
    public CommonResult<NetAlbumInfo> getCatAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTags.get(tag);

        if (StringUtil.notEmpty(s[7])) {
            HttpResponse resp = HttpRequest.get(String.format(CAT_ALBUM_DT_API, s[7], (page - 1) * limit, limit, System.currentTimeMillis())).execute();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("object_list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject mainJson = albumArray.getJSONObject(i);
                JSONObject albumJson = mainJson.getJSONObject("album");
                JSONObject sender = mainJson.getJSONObject("sender");

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = sender.getString("username");
                String artistId = sender.getString("id");
                String publishTime = TimeUtil.msToDate(mainJson.getLong("add_datetime_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
                Integer songNum = albumJson.getIntValue("count");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
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
        }
        return new CommonResult<>(r, t);
    }
}
