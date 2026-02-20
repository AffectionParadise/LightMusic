package net.doge.sdk.service.album.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgNewAlbumReq {
    private static KgNewAlbumReq instance;

    private KgNewAlbumReq() {
    }

    public static KgNewAlbumReq getInstance() {
        if (instance == null) instance = new KgNewAlbumReq();
        return instance;
    }

    // 新碟上架 API (酷狗)
    private final String NEW_ALBUM_KG_API = "/musicadservice/v1/mobile_newalbum_sp";
    // 编辑精选专辑 API (酷狗)
    private final String IP_ALBUM_KG_API = "/openapi/v1/ip/albums";

    /**
     * 新碟上架
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTags.get(tag);

        String param = s[TagType.NEW_ALBUM_KG];
        if (StringUtil.notEmpty(param)) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(NEW_ALBUM_KG_API);
            String dat = String.format("{\"apiver\":%s,\"token\":\"\",\"page\":%s,\"pagesize\":%s,\"withpriv\":1}", KugouReqBuilder.apiver, page, limit);
            String albumInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray albumArray = data.getJSONArray(param);
            t = albumArray.size();
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
                String artistId = albumJson.getString("singerid");
                String publishTime = albumJson.getString("publishtime").split(" ")[0];
                Integer songNum = albumJson.getIntValue("songcount");
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
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

    /**
     * 编辑精选专辑
     */
    public CommonResult<NetAlbumInfo> getIpAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTags.get(tag);

        String param = s[TagType.IP_ALBUM_KG];
        if (StringUtil.notEmpty(param)) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_ALBUM_KG_API);
            String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", param, page, limit);
            String albumInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            t = albumInfoJson.getIntValue("total");
            JSONArray albumArray = albumInfoJson.getJSONArray("data");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);
                JSONObject base = albumJson.getJSONObject("base");

                String albumId = base.getString("album_id");
                String albumName = base.getString("album_name");
                String artist = base.getString("author_name");
                String publishTime = base.getString("publish_date");
                String coverImgThumbUrl = base.getString("cover").replace("/{size}", "");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
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
