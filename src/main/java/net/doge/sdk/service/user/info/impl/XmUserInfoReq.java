package net.doge.sdk.service.user.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class XmUserInfoReq {
    private static XmUserInfoReq instance;

    private XmUserInfoReq() {
    }

    public static XmUserInfoReq getInstance() {
        if (instance == null) instance = new XmUserInfoReq();
        return instance;
    }

    // 用户信息 API (喜马拉雅)
    private final String USER_DETAIL_XM_API = "https://www.ximalaya.com/revision/user/basic?uid=%s";
    // 用户节目 API (喜马拉雅)
    private final String USER_PROGRAMS_XM_API = "https://www.ximalaya.com/revision/user/track?uid=%s&orderType=%s&page=%s&pageSize=%s&keyWord=";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, id))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        if (!userInfo.hasLevel()) userInfo.setLevel(data.getIntValue("anchorGrade"));
        if (!userInfo.hasGender()) {
            Integer gen = data.getIntValue("gender");
            String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
            userInfo.setGender(gender);
        }
        if (!userInfo.hasBirthday())
            userInfo.setBirthday(data.getIntValue("birthMonth") <= 0 ? null : data.getString("birthMonth") + "-" + data.getString("birthDay"));
        if (!userInfo.hasArea()) {
            String area = (data.containsKey("province") ? data.getString("province") : "") + (data.containsKey("city") ? " - " + data.getString("city") : "");
            userInfo.setArea(StringUtil.isEmpty(area) ? "未知" : area);
        }
        if (!userInfo.hasSign()) userInfo.setSign(data.getString("personalSignature"));
        if (!userInfo.hasFollow()) userInfo.setFollow(data.getIntValue("followingCount"));
        if (!userInfo.hasFan()) userInfo.setFan(data.getIntValue("fansCount"));
        if (!userInfo.hasRadioCount()) userInfo.setRadioCount(data.getIntValue("albumsCount"));
        if (!userInfo.hasProgramCount()) userInfo.setProgramCount(data.getIntValue("tracksCount"));

        String avatarUrl = "https:" + data.getString("cover");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

        String bgImgUrl = "https:" + data.getString("background");
        if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_XM_API, id, recordType + 1, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONArray songArray = data.getJSONArray("trackList");
        total = data.getIntValue("totalCount");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("trackId");
            String name = songJson.getString("title");
            String artist = songJson.getString("nickname");
            String artistId = songJson.getString("anchorUid");
            String albumName = songJson.getString("albumTitle");
            String albumId = songJson.getString("albumId");
            Double duration = songJson.getDouble("length");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.XM);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            res.add(musicInfo);
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

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, id))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");

        String userId = data.getString("uid");
        String userName = data.getString("nickName");
        Integer gen = data.getIntValue("gender");
        String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
        String avatarThumbUrl = "https:" + data.getString("cover");
        Integer follow = data.getIntValue("followingCount");
        Integer fan = data.getIntValue("fansCount");
        Integer radioCount = data.getIntValue("albumsCount");
        Integer programCount = data.getIntValue("tracksCount");

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(NetResourceSource.XM);
        userInfo.setId(userId);
        userInfo.setName(userName);
        userInfo.setGender(gender);
        userInfo.setAvatarThumbUrl(avatarThumbUrl);
        userInfo.setFollow(follow);
        userInfo.setFan(fan);
        userInfo.setRadioCount(radioCount);
        userInfo.setProgramCount(programCount);

        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
            userInfo.setAvatarThumb(avatarThumb);
        });

        res.add(userInfo);

        return new CommonResult<>(res, t);
    }
}
