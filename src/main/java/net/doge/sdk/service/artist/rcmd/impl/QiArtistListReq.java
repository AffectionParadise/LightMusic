package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpResponse;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QiArtistListReq {
    private static QiArtistListReq instance;

    private QiArtistListReq() {
    }

    public static QiArtistListReq getInstance() {
        if (instance == null) instance = new QiArtistListReq();
        return instance;
    }

    // 推荐歌手 API (千千)
    private final String REC_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 分类歌手 API (千千)
    private final String CAT_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/artist/list?appid=16073360&" +
            "artistFristLetter=%s&artistGender=%s&artistRegion=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    /**
     * 推荐歌手
     */
    public CommonResult<NetArtistInfo> getRecArtists(int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t;

        HttpResponse resp = SdkCommon.qiRequest(String.format(REC_ARTISTS_LIST_QI_API, System.currentTimeMillis()))
                .execute();
        String artistInfoBody = resp.body();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONArray("data").getJSONObject(6);
        t = data.getIntValue("module_nums");
        JSONArray artistArray = data.getJSONArray("result");
        for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
            JSONObject artistJson = artistArray.getJSONObject(i);

            String artistId = artistJson.getString("artistCode");
            String artistName = artistJson.getString("name");
            String coverImgThumbUrl = artistJson.getString("pic");
            Integer songNum = artistJson.getIntValue("trackTotal");
            Integer albumNum = artistJson.getIntValue("albumTotal");

            NetArtistInfo artistInfo = new NetArtistInfo();
            artistInfo.setSource(NetMusicSource.QI);
            artistInfo.setId(artistId);
            artistInfo.setName(artistName);
            artistInfo.setSongNum(songNum);
            artistInfo.setAlbumNum(albumNum);
            artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
    public CommonResult<NetArtistInfo> getCatArtists(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[8])) {
            // 分割时保留空串
            String[] sp = s[8].split(" ", -1);
            String artistInfoBody = SdkCommon.qiRequest(String.format(CAT_ARTISTS_LIST_QI_API, sp[0], sp[2], sp[1], page, limit, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("result");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("artistCode");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("pic");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QI);
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
