package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class DtUserMenuReq {
    private static DtUserMenuReq instance;

    private DtUserMenuReq() {
    }

    public static DtUserMenuReq getInstance() {
        if (instance == null) instance = new DtUserMenuReq();
        return instance;
    }

    // 用户专辑 API (堆糖)
    private final String USER_ALBUM_DT_API = "https://www.duitang.com/napi/album/list/by_user/?user_id=%s&start=%s&limit=%s";
    // 用户关注 API (堆糖)
    private final String USER_FOLLOWS_DT_API = "https://www.duitang.com/napi/friendship/follows/?user_id=%s&start=%s&limit=%s";
    // 用户粉丝 API (堆糖)
    private final String USER_FANS_DT_API = "https://www.duitang.com/napi/friendship/fans/?user_id=%s&start=%s&limit=%s";

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page, int limit) {
        List<NetAlbumInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DT_API, id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONObject("data");
        JSONArray albumArray = data.getJSONArray("object_list");
        total = page * limit;
        if (data.getIntValue("more") == 1) total++;
        for (int i = 0, len = albumArray.size(); i < len; i++) {
            JSONObject albumJson = albumArray.getJSONObject(i);
            JSONObject user = albumJson.getJSONObject("user");

            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            String artist = user.getString("username");
            String artistId = user.getString("id");
            String publishTime = TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000);
            String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
            Integer songNum = albumJson.getIntValue("count");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetMusicSource.DT);
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
            res.add(albumInfo);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFollows(NetUserInfo netUserInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t;

        String id = netUserInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_DT_API, id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray userArray = data.getJSONArray("object_list");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("username");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("avatar");
            Integer follow = userJson.getIntValue("followCount");
            Integer fan = userJson.getIntValue("beFollowCount");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.DT);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setFollow(follow);
            userInfo.setFan(fan);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            res.add(userInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFans(NetUserInfo netUserInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t;

        String id = netUserInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_FANS_DT_API, id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        t = data.getIntValue("total");
        JSONArray userArray = data.getJSONArray("object_list");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("username");
            String gender = "保密";
            String avatarThumbUrl = userJson.getString("avatar");
            Integer follow = userJson.getIntValue("followCount");
            Integer fan = userJson.getIntValue("beFollowCount");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.DT);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setFollow(follow);
            userInfo.setFan(fan);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            res.add(userInfo);
        }

        return new CommonResult<>(res, t);
    }
}
