package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgArtistListReq {
    private static MgArtistListReq instance;

    private MgArtistListReq() {
    }

    public static MgArtistListReq getInstance() {
        if (instance == null) instance = new MgArtistListReq();
        return instance;
    }

    // 来电新声榜 API (咪咕)
    private final String ARTIST_LIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425062";
    // 来电唱作榜 API (咪咕)
    private final String ARTIST_LIST_MG_API_2 = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425072";
    // 分类歌手 API (咪咕)
    private final String CAT_ARTIST_LIST_MG_API = "http://app.c.nf.migu.cn/pc/bmw/singer-index/list/v1.0?tab=%s";

    /**
     * 来电新声榜
     */
    public CommonResult<NetArtistInfo> getArtistRank(int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;

        String artistInfoBody = HttpRequest.get(ARTIST_LIST_MG_API)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("columnInfo");
        if (JsonUtil.notEmpty(data)) {
            t = data.getIntValue("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
     * 来电唱作榜
     */
    public CommonResult<NetArtistInfo> getArtistRank2(int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;

        String artistInfoBody = HttpRequest.get(ARTIST_LIST_MG_API_2)
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("columnInfo");
        if (JsonUtil.notEmpty(data)) {
            t = data.getIntValue("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
     * 分类歌手
     */
    public CommonResult<NetArtistInfo> getCatArtists(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.CAT_ARTIST_MG];
        if (StringUtil.notEmpty(param)) {
            String artistInfoBody = HttpRequest.get(String.format(CAT_ARTIST_LIST_MG_API, param))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            JSONArray artistArray = data.getJSONArray("contents");
            t = artistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("resId");
                String artistName = artistJson.getString("txt");
                String coverImgThumbUrl = artistJson.getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
