package net.doge.sdk.service.user.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.AreaUtil;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcUserInfoReq {
    private static NcUserInfoReq instance;

    private NcUserInfoReq() {
    }

    public static NcUserInfoReq getInstance() {
        if (instance == null) instance = new NcUserInfoReq();
        return instance;
    }

    // 用户信息 API (网易云)
    private final String USER_DETAIL_NC_API = "https://music.163.com/weapi/v1/user/detail/%s";
    // 用户歌曲 API (网易云)
    private final String USER_SONGS_NC_API = "https://music.163.com/weapi/v1/play/record";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_DETAIL_NC_API, id), "{}", options)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject profileJson = userInfoJson.getJSONObject("profile");
        if (!userInfo.hasLevel()) userInfo.setLevel(userInfoJson.getIntValue("level"));
        if (!userInfo.hasAccAge()) userInfo.setAccAge(TimeUtil.getAccAge(profileJson.getLong("createTime")));
        if (!userInfo.hasBirthday()) userInfo.setBirthday(TimeUtil.msToDate(profileJson.getLong("birthday")));
        if (!userInfo.hasArea())
            userInfo.setArea(AreaUtil.getArea(profileJson.getIntValue("province"), profileJson.getIntValue("city")));
        if (!userInfo.hasSign()) userInfo.setSign(profileJson.getString("signature"));
        if (!userInfo.hasFollow()) userInfo.setFollow(profileJson.getIntValue("follows"));
        if (!userInfo.hasFan()) userInfo.setFan(profileJson.getIntValue("followeds"));
        if (!userInfo.hasPlaylistCount()) userInfo.setPlaylistCount(profileJson.getIntValue("playlistCount"));

        String avatarUrl = profileJson.getString("avatarUrl");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

        String bgImgUrl = profileJson.getString("backgroundUrl");
        if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total = 0;

        boolean isAll = recordType == 1;
        String id = userInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String userInfoBody = SdkCommon.ncRequest(Method.POST, USER_SONGS_NC_API,
                        String.format("{\"uid\":\"%s\",\"type\":%s}", id, recordType ^ 1), options)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONArray songArray = userInfoJson.getJSONArray(isAll ? "allData" : "weekData");
        if (JsonUtil.notEmpty(songArray)) {
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("song");
                JSONObject albumJson = songJson.getJSONObject("al");

                String songId = songJson.getString("id");
                String name = songJson.getString("name").trim();
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");
                int qualityType = AudioQuality.UNKNOWN;
                if (JsonUtil.notEmpty(songJson.getJSONObject("hr"))) qualityType = AudioQuality.HR;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("sq"))) qualityType = AudioQuality.SQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("h"))) qualityType = AudioQuality.HQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("m"))) qualityType = AudioQuality.MQ;
                else if (JsonUtil.notEmpty(songJson.getJSONObject("l"))) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setQualityType(qualityType);

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id) {
        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_DETAIL_NC_API, id), "{}", options)
                .executeAsStr();
        JSONObject userJson = JSONObject.parseObject(userInfoBody).getJSONObject("profile");

        String userId = userJson.getString("userId");
        String userName = userJson.getString("nickname");
        Integer gen = userJson.getIntValue("gender");
        String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
        String accAge = TimeUtil.getAccAge(userJson.getLong("createTime"));
        String avatarThumbUrl = userJson.getString("avatarUrl");
        Integer follow = userJson.getIntValue("follows");
        Integer fan = userJson.getIntValue("followeds");
        Integer playlistCount = userJson.getIntValue("playlistCount");

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setId(userId);
        userInfo.setName(userName);
        userInfo.setGender(gender);
        userInfo.setAccAge(accAge);
        userInfo.setAvatarThumbUrl(avatarThumbUrl);
        userInfo.setFollow(follow);
        userInfo.setFan(fan);
        userInfo.setPlaylistCount(playlistCount);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
            userInfo.setAvatarThumb(avatarThumb);
        });

        res.add(userInfo);

        return new CommonResult<>(res, t);
    }
}
