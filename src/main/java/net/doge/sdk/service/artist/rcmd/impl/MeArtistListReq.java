package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeArtistListReq {
    private static MeArtistListReq instance;

    private MeArtistListReq() {
    }

    public static MeArtistListReq getInstance() {
        if (instance == null) instance = new MeArtistListReq();
        return instance;
    }

    // CV 广场 API (猫耳)
    private final String CAT_CV_ME_API = "https://www.missevan.com/organization/getseiys?initial=%s&p=%s&page_size=%s";
    // 社团广场 API (猫耳)
    private final String CAT_ORGANIZATIONS_ME_API = "https://www.missevan.com/organization/getorganizations?initial=%s&p=%s&page_size=%s";

    /**
     * 分类声优
     */
    public CommonResult<NetArtistInfo> getCatCVs(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[9])) {
            String artistInfoBody = HttpRequest.get(String.format(CAT_CV_ME_API, s[9].trim(), page, limit))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject info = artistInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getIntValue("count");
            JSONArray artistArray = info.getJSONArray("Datas");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("avatar");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
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
     * 分类社团
     */
    public CommonResult<NetArtistInfo> getCatOrganizations(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTag.get(tag);

        if (StringUtil.notEmpty(s[9])) {
            String artistInfoBody = HttpRequest.get(String.format(CAT_ORGANIZATIONS_ME_API, s[9].trim(), page, limit))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject info = artistInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getIntValue("count");
            JSONArray artistArray = info.getJSONArray("Datas");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("avatar");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
                artistInfo.setOrganization(true);
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
