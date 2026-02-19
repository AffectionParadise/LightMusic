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
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqArtistListReq {
    private static QqArtistListReq instance;

    private QqArtistListReq() {
    }

    public static QqArtistListReq getInstance() {
        if (instance == null) instance = new QqArtistListReq();
        return instance;
    }

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 歌手榜单
     */
    public CommonResult<NetArtistInfo> getArtistRank(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        if (StringUtil.notEmpty(s[5])) {
            final int lim = 80, p = (page - 1) / 4 + 1;
            String[] sp = s[5].split(" ");
            String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":0},\"singerList\":{\"module\":\"Music.SingerListServer\",\"method\":\"get_singer_list\"," +
                            "\"param\":{\"sex\":%s,\"genre\":%s,\"index\":%s,\"area\":%s,\"sin\":%s,\"cur_page\":%s}}}", sp[0], sp[1], sp[2], sp[3], (p - 1) * lim, p))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("singerList").getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("singerlist");
            for (int i = (page - 1) * limit % lim, len = Math.min(artistArray.size(), (page - 1) * limit % lim + limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("singer_mid");
                String artistName = artistJson.getString("singer_name");
                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QQ);
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
