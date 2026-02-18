package net.doge.sdk.service.user.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiUserInfoReq {
    private static BiUserInfoReq instance;

    private BiUserInfoReq() {
    }

    public static BiUserInfoReq getInstance() {
        if (instance == null) instance = new BiUserInfoReq();
        return instance;
    }

    // 用户信息 API (哔哩哔哩)
    private final String USER_DETAIL_BI_API = "https://api.bilibili.com/x/web-interface/card?mid=%s&photo=true";
    // 用户音频 API (哔哩哔哩)
    private final String USER_AUDIO_BI_API = "https://api.bilibili.com/audio/music-service/web/song/upper?order=%s&uid=%s&pn=%s&ps=%s";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_BI_API, id))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONObject card = data.getJSONObject("card");

        if (!userInfo.hasLevel()) userInfo.setLevel(card.getJSONObject("level_info").getIntValue("current_level"));
        if (!userInfo.hasGender()) userInfo.setGender(card.getString("sex"));
        if (!userInfo.hasBirthday()) userInfo.setBirthday(card.getString("birthday"));
        if (!userInfo.hasSign()) userInfo.setSign(card.getString("sign"));
        if (!userInfo.hasFollow()) userInfo.setFollow(card.getIntValue("attention"));
        if (!userInfo.hasFan()) userInfo.setFan(card.getIntValue("fans"));
        if (!userInfo.hasProgramCount()) userInfo.setProgramCount(data.getIntValue("archive_count"));

        String avatarUrl = card.getString("face");
        if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

        String bgImgUrl = data.getJSONObject("space").getString("s_img");
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
        String userInfoBody = HttpRequest.get(String.format(USER_AUDIO_BI_API, recordType + 1, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        total = data.getIntValue("totalSize");
        JSONArray songArray = data.getJSONArray("data");
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("title");
                String artist = songJson.getString("uname");
                String artistId = songJson.getString("uid");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.BI);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);
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

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_BI_API, id))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONObject card = data.getJSONObject("card");

        String userId = card.getString("mid");
        String userName = card.getString("name");
        String gender = card.getString("sex");
        gender = "男".equals(gender) ? "♂ " + gender : "女".equals(gender) ? "♀ " + gender : "保密";
        String avatarThumbUrl = card.getString("face");
        Integer follow = card.getIntValue("attention");
        Integer fan = card.getIntValue("fans");
        Integer programCount = data.getIntValue("archive_count");

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(NetMusicSource.BI);
        userInfo.setId(userId);
        userInfo.setName(userName);
        userInfo.setGender(gender);
        userInfo.setAvatarThumbUrl(avatarThumbUrl);
        userInfo.setFollow(follow);
        userInfo.setFan(fan);
        userInfo.setProgramCount(programCount);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
            userInfo.setAvatarThumb(coverImgThumb);
        });

        res.add(userInfo);

        return new CommonResult<>(res, t);
    }
}
