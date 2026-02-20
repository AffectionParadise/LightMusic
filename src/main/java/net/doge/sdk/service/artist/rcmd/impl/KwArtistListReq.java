package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpResponse;
import net.doge.util.core.http.constant.Header;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class KwArtistListReq {
    private static KwArtistListReq instance;

    private KwArtistListReq() {
    }

    public static KwArtistListReq getInstance() {
        if (instance == null) instance = new KwArtistListReq();
        return instance;
    }

    // 歌手推荐 API (酷我)
    private final String ARTIST_LIST_KW_API = "https://kuwo.cn/api/www/artist/artistInfo?category=%s&pn=%s&rn=%s&httpsStatus=1";
    // 全部歌手 API (酷我)
    private final String ALL_ARTISTS_LIST_KW_API = "https://kuwo.cn/api/www/artist/artistInfo?category=%s&prefix=%s&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 推荐歌手
     */
    public CommonResult<NetArtistInfo> getArtistRank(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.ARTIST_LIST_KW];
        if (StringUtil.notEmpty(param)) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_LIST_KW_API, param, page, limit)).execute();
            if (resp.isSuccessful()) {
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("artistList");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = artistJson.getString("pic300");
                    Integer songNum = artistJson.getIntValue("musicNum");
                    Integer albumNum = artistJson.getIntValue("albumNum");
                    Integer mvNum = artistJson.getIntValue("mvNum");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KW);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(artistInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 全部歌手
     */
    public CommonResult<NetArtistInfo> getAllArtists(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.ALL_ARTIST_KW];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            HttpResponse resp = SdkCommon.kwRequest(String.format(ALL_ARTISTS_LIST_KW_API, sp[0], sp[1], page, limit))
                    .header(Header.REFERER, StringUtil.notEmpty(sp[1]) ? "https://kuwo.cn/singers" : "")
                    .execute();
            if (resp.isSuccessful()) {
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("artistList");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = artistJson.getString("pic300");
                    Integer songNum = artistJson.getIntValue("musicNum");
                    Integer albumNum = artistJson.getIntValue("albumNum");
                    Integer mvNum = artistJson.getIntValue("mvNum");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KW);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(artistInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }
}
