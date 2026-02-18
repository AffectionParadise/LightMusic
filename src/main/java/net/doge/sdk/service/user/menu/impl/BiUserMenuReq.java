package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMvInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.time.TimeUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class BiUserMenuReq {
    private static BiUserMenuReq instance;

    private BiUserMenuReq() {
    }

    public static BiUserMenuReq getInstance() {
        if (instance == null) instance = new BiUserMenuReq();
        return instance;
    }

    // 用户视频 API (哔哩哔哩)
    private final String USER_VIDEO_BI_API = "https://api.bilibili.com/x/space/wbi/arc/search?order=%s&mid=%s&pn=%s&ps=%s";
    // 用户关注 API (哔哩哔哩)
    private final String USER_FOLLOWS_BI_API = "https://api.bilibili.com/x/relation/followings?vmid=%s&pn=%s&ps=%s";
    // 用户粉丝 API (哔哩哔哩)
    private final String USER_FANS_BI_API = "https://api.bilibili.com/x/relation/followers?vmid=%s&pn=%s&ps=%s";

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int sortType, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int total = 0;

        String[] orders = {"pubdate", "click", "stow"};
        String id = userInfo.getId();
        String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_BI_API, orders[sortType], id, page, limit))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
        if (JsonUtil.notEmpty(data)) {
            total = data.getJSONObject("page").getIntValue("count");
            JSONArray mvArray = data.getJSONObject("list").getJSONArray("vlist");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("play");
                Double duration = DurationUtil.toSeconds(mvJson.getString("length"));
                String pubTime = TimeUtil.msToDate(mvJson.getLong("created") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
                mvInfo.setBvId(bvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
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
        int t = 0;

        String id = netUserInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_BI_API, id, page, limit))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(data)) {
            t = data.getIntValue("total");
            JSONArray userArray = data.getJSONArray("list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("mid");
                String userName = userJson.getString("uname");
                String avatarThumbUrl = userJson.getString("face");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.BI);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

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
        String userInfoBody = HttpRequest.get(String.format(USER_FANS_BI_API, id, page, limit))
                .cookie(SdkCommon.BI_COOKIE)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        if (JsonUtil.notEmpty(data)) {
            t = data.getIntValue("total");
            JSONArray userArray = data.getJSONArray("list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("mid");
                String userName = userJson.getString("uname");
                String avatarThumbUrl = userJson.getString("face");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.BI);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

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
