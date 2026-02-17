package net.doge.sdk.service.album.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqNewAlbumReq {
    private static QqNewAlbumReq instance;

    private QqNewAlbumReq() {
    }

    public static QqNewAlbumReq getInstance() {
        if (instance == null) instance = new QqNewAlbumReq();
        return instance;
    }

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 新碟上架
     */
    public CommonResult<NetAlbumInfo> getNewAlbums(String tag, int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newAlbumTag.get(tag);

        if (StringUtil.notEmpty(s[5])) {
            String albumInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody(String.format("{\"comm\":{\"ct\":24},\"new_album\":{\"module\":\"newalbum.NewAlbumServer\",\"method\":\"get_new_album_info\"," +
                            "\"param\":{\"area\":%s,\"sin\":0,\"num\":100}}}", s[5]))
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONObject("new_album").getJSONObject("data").getJSONArray("albums");
            t = albumArray.size();
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("mid");
                String albumName = albumJson.getString("name");
                String artist = SdkUtil.parseArtist(albumJson);
                String artistId = SdkUtil.parseArtistId(albumJson);
                String publishTime = albumJson.getString("release_time");
//            Integer songNum = albumJson.getJSONObject("ex").getIntValue("track_nums");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
//            albumInfo.setSongNum(songNum);
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
