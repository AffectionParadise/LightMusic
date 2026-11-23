package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MeArtistListReq {
    private static MeArtistListReq instance;

    private MeArtistListReq() {
    }

    public static MeArtistListReq getInstance() {
        if (instance == null) instance = new MeArtistListReq();
        return instance;
    }

    // CV 广场 API (猫耳)
    private final String CAT_CV_ME_API = "https://www.missevan.com/organization/getseiys";
    // 社团广场 API (猫耳)
    private final String CAT_ORGANIZATIONS_ME_API = "https://www.missevan.com/organization/getorganizations";

    /**
     * 分类声优
     */
    public CommonResult<NetArtistInfo> getCatCVs(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagModule.CAT_CV_ME];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("initial", param.trim());
            params.put("p", page);
            params.put("page_size", limit);
            String artistInfoBody = HttpRequest.get(CAT_CV_ME_API)
                    .queryParams(params)
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
                artistInfo.setSource(NetResourceSource.ME);
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
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagModule.CAT_CV_ME];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("initial", param.trim());
            params.put("p", page);
            params.put("page_size", limit);
            String artistInfoBody = HttpRequest.get(CAT_ORGANIZATIONS_ME_API)
                    .queryParams(params)
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
                artistInfo.setSource(NetResourceSource.ME);
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
