package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcUserMenuReq {
    private static NcUserMenuReq instance;

    private NcUserMenuReq() {
    }

    public static NcUserMenuReq getInstance() {
        if (instance == null) instance = new NcUserMenuReq();
        return instance;
    }

    // 用户歌单 API (网易云)
    private final String USER_PLAYLIST_NC_API = "https://music.163.com/api/user/playlist";
    // 用户电台 API (网易云)
    private final String USER_RADIO_NC_API = "https://music.163.com/weapi/djradio/get/byuser";
    // 用户关注 API (网易云)
    private final String USER_FOLLOWS_NC_API = "https://music.163.com/weapi/user/getfollows/%s";
    // 用户粉丝 API (网易云)
    private final String USER_FANS_NC_API = "https://music.163.com/eapi/user/getfolloweds/%s";

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        List<NetPlaylistInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String playlistInfoBody = SdkCommon.ncRequest(Method.POST, USER_PLAYLIST_NC_API,
                        String.format("{\"uid\":\"%s\",\"offset\":%s,\"limit\":%s,\"includeVideo\":true}", id, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
        total = playlistInfoJson.getBooleanValue("more") ? page * limit + 1 : page * limit;
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);
            JSONObject creatorJson = playlistJson.getJSONObject("creator");

            String playlistId = playlistJson.getString("id");
            String playlistName = playlistJson.getString("name");
            String creator = creatorJson.getString("nickname");
            String creatorId = creatorJson.getString("userId");
            Long playCount = playlistJson.getLong("playCount");
            Integer trackCount = playlistJson.getIntValue("trackCount");
            String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setId(playlistId);
            playlistInfo.setName(playlistName);
            playlistInfo.setCreator(creator);
            playlistInfo.setCreatorId(creatorId);
            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            playlistInfo.setPlayCount(playCount);
            playlistInfo.setTrackCount(trackCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                playlistInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(playlistInfo);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        List<NetRadioInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String radioInfoBody = SdkCommon.ncRequest(Method.POST, USER_RADIO_NC_API, String.format("{\"userId\":\"%s\"}", id), options)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
        total = radioInfoJson.getIntValue("count");
        for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);
            JSONObject djJson = radioJson.getJSONObject("dj");

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = djJson.getString("nickname");
            String djId = djJson.getString("userId");
            Long playCount = radioJson.getLong("playCount");
            Integer trackCount = radioJson.getIntValue("programCount");
            String category = radioJson.getString("category");
            if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
            String coverImgThumbUrl = radioJson.getString("picUrl");
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setDjId(djId);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setPlayCount(playCount);
            radioInfo.setTrackCount(trackCount);
            radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(radioInfo);
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
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_FOLLOWS_NC_API, id),
                        String.format("{\"offset\":%s,\"limit\":%s,\"order\":true}", (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        t = userInfoJson.getBooleanValue("more") ? page * limit + 1 : page * limit;
        JSONArray userArray = userInfoJson.getJSONArray("follow");
        if (JsonUtil.notEmpty(userArray)) {
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getIntValue("follows");
                Integer fan = userJson.getIntValue("followeds");
                Integer playlistCount = userJson.getIntValue("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
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
        int t = 0;

        String id = netUserInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/user/getfolloweds");
        String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_FANS_NC_API, id),
                        String.format("{\"userId\":\"%s\",\"time\":0,\"offset\":%s,\"limit\":%s,\"getcounts\":true}", id, (page - 1) * limit, limit), options)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONArray userArray = userInfoJson.getJSONArray("followeds");
        if (JsonUtil.notEmpty(userArray)) {
            t = userInfoJson.getIntValue("size");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getIntValue("follows");
                Integer fan = userJson.getIntValue("followeds");
                Integer playlistCount = userJson.getIntValue("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
