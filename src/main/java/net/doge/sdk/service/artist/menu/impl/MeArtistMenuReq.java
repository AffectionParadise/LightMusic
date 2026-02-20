package net.doge.sdk.service.artist.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeArtistMenuReq {
    private static MeArtistMenuReq instance;

    private MeArtistMenuReq() {
    }

    public static MeArtistMenuReq getInstance() {
        if (instance == null) instance = new MeArtistMenuReq();
        return instance;
    }

    // 社团职员 API (猫耳)
    private final String ORGANIZATION_STAFFS_ME_API = "https://www.missevan.com/organization/staff?organization_id=%s&page=%s";
    // 社团声优 API (猫耳)
    private final String ORGANIZATION_CVS_ME_API = "https://www.missevan.com/organization/cast?organization_id=%s&page=%s";
    // 社团电台 API (猫耳)
    private final String ORGANIZATION_RADIOS_ME_API = "https://www.missevan.com/organization/drama?organization_id=%s&page=%s";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t;

        String id = artistInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(ORGANIZATION_STAFFS_ME_API, id, page))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("info");
        JSONArray userArray = data.getJSONArray("staff");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = (page - 1) * limit, len = Math.min(page * limit, userArray.size()); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("name");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("avatar");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetResourceSource.ME);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            res.add(userInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page, int limit) {
        List<NetArtistInfo> res = new LinkedList<>();
        int t;

        String id = netArtistInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_CVS_ME_API, id, page))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject info = artistInfoJson.getJSONObject("info");
        t = info.getJSONObject("pagination").getIntValue("count");
        JSONArray artistArray = info.getJSONArray("cast");
        for (int i = (page - 1) * limit, len = Math.min(page * limit, artistArray.size()); i < len; i++) {
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

            res.add(artistInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        List<NetRadioInfo> res = new LinkedList<>();
        int t;

        String id = artistInfo.getId();
        if (artistInfo.isOrganization()) {
            String radioInfoBody = HttpRequest.get(String.format(ORGANIZATION_RADIOS_ME_API, id, page))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            t = data.getJSONObject("pagination").getIntValue("count");
            JSONArray radioArray = data.getJSONArray("drama");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, radioArray.size()); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = artistInfo.getName();
                String coverImgThumbUrl = "https:" + radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
        } else {
            String radioInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("dramas");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i).getJSONObject("drama");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String category = radioJson.getString("catalog_name");
                String dj = artistInfo.getName();
                Long playCount = radioJson.getLong("view_count");
                String coverImgThumbUrl = radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setCategory(category);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
