package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcArtistListReq {
    private static NcArtistListReq instance;

    private NcArtistListReq() {
    }

    public static NcArtistListReq getInstance() {
        if (instance == null) instance = new NcArtistListReq();
        return instance;
    }

    // 歌手榜 API (网易云)
    private final String ARTIST_RANK_LIST_NC_API = "https://music.163.com/weapi/toplist/artist";
    // 热门歌手 API (网易云)
    private final String HOT_ARTIST_LIST_NC_API = "https://music.163.com/weapi/artist/top";
    // 分类歌手 API (网易云)
    private final String CAT_ARTIST_NC_API = "https://music.163.com/api/v1/artist/list";
    // 曲风歌手 API (网易云)
    private final String STYLE_ARTIST_NC_API = "https://music.163.com/api/style-tag/home/artist";

    /**
     * 歌手榜
     */
    public CommonResult<NetArtistInfo> getArtistRank(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_RANK_LIST_NC_API,
                            String.format("{\"type\":\"%s\",\"offset\":0,\"limit\":100,\"total\":true}", s[0]), options)
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONObject("list").getJSONArray("artists");
            t = artistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 热门歌手
     */
    public CommonResult<NetArtistInfo> getHotArtist(int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String artistInfoBody = SdkCommon.ncRequest(Method.POST, HOT_ARTIST_LIST_NC_API,
                        String.format("{\"offset\":%s,\"limit\":%s,\"total\":true}", (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONArray artistArray = artistInfoJson.getJSONArray("artists");
        t = artistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("id");
            String artistName = artistJson.getString("name");
            Integer songNum = artistJson.getIntValue("musicSize");
            Integer albumNum = artistJson.getIntValue("albumSize");
            String coverImgThumbUrl = artistJson.getString("img1v1Url");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            artistInfo.setSongNum(songNum);
            artistInfo.setAlbumNum(albumNum);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                artistInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(artistInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分类歌手
     */
    public CommonResult<NetArtistInfo> getCatArtist(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            String[] sp = s[1].split(" ");
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, CAT_ARTIST_NC_API,
                            String.format("{\"type\":\"%s\",\"area\":\"%s\",\"initial\":\"%s\",\"offset\":%s,\"limit\":%s,\"total\":true}",
                                    sp[0], sp[1], sp[2], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("artists");
            t = artistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 曲风歌手
     */
    public CommonResult<NetArtistInfo> getStyleArtist(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, STYLE_ARTIST_NC_API,
                            String.format("{\"tagId\":\"%s\",\"cursor\":%s,\"size\":%s,\"sort\":0}", s[2], (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            JSONArray artistArray = data.getJSONArray("artists");
            t = data.getJSONObject("page").getIntValue("total");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
