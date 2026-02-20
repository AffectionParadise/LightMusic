package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class XmUserMenuReq {
    private static XmUserMenuReq instance;

    private XmUserMenuReq() {
    }

    public static XmUserMenuReq getInstance() {
        if (instance == null) instance = new XmUserMenuReq();
        return instance;
    }

    // 用户电台 API (喜马拉雅)
    private final String USER_RADIO_XM_API = "https://www.ximalaya.com/revision/user/pub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户收藏电台 API (喜马拉雅)
    private final String USER_SUB_RADIO_XM_API = "https://www.ximalaya.com/revision/user/sub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户关注 API (喜马拉雅)
    private final String USER_FOLLOWS_XM_API = "https://www.ximalaya.com/revision/user/following?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户粉丝 API (喜马拉雅)
    private final String USER_FANS_XM_API = "https://www.ximalaya.com/revision/user/fans?uid=%s&page=%s&pageSize=%s&keyWord=";

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        String id = userInfo.getId();
        Callable<CommonResult<NetRadioInfo>> getCreatedRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            int t;

            String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_XM_API, id, page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            JSONArray radioArray = data.getJSONArray("albumList");
            t = data.getIntValue("totalCount");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getString("anchorNickName");
                String djId = radioJson.getString("anchorUid");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("trackCount");
//                String category = radioJson.getString("category");
                String coverImgThumbUrl = "https:" + radioJson.getString("coverPath");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }

            return new CommonResult<>(r, t);
        };
        Callable<CommonResult<NetRadioInfo>> getSubRadios = () -> {
            List<NetRadioInfo> r = new LinkedList<>();
            int t;

            String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_XM_API, id, page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            JSONArray radioArray = data.getJSONArray("albumsInfo");
            t = data.getIntValue("totalCount");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getJSONObject("anchor").getString("anchorNickName");
                String djId = radioJson.getJSONObject("anchor").getString("anchorUid");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("trackCount");
                String category = radioJson.getString("categoryTitle");
                String coverImgThumbUrl = "https://imagev2.xmcdn.com/" + radioJson.getString("coverPath");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }

            return new CommonResult<>(r, t);
        };

        MultiCommonResultCallableExecutor<NetRadioInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getCreatedRadios);
        executor.submit(getSubRadios);
        return executor.getResult();
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
        String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_XM_API, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONArray userArray = data.getJSONArray("followingsPageInfo");
        t = data.getIntValue("totalCount");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("uid");
            String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getIntValue("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
            String avatarThumbUrl = "https:" + userJson.getString("coverPath");
            Integer follow = userJson.getIntValue("followingCount");
            Integer fan = userJson.getIntValue("followerCount");
            Integer radioCount = userJson.getIntValue("albumCount");
            Integer programCount = userJson.getIntValue("trackCount");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetResourceSource.XM);
            userInfo.setId(userId);
            userInfo.setName(userName);
//                userInfo.setGender(gender);
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
        String userInfoBody = HttpRequest.get(String.format(USER_FANS_XM_API, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("data");
        JSONArray userArray = data.getJSONArray("fansPageInfo");
        t = data.getIntValue("totalCount");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("uid");
            String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getIntValue("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
            String avatarThumbUrl = "https:" + userJson.getString("coverPath");
            Integer follow = userJson.getIntValue("followingCount");
            Integer fan = userJson.getIntValue("followerCount");
            Integer radioCount = userJson.getIntValue("albumCount");
            Integer programCount = userJson.getIntValue("trackCount");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetResourceSource.XM);
            userInfo.setId(userId);
            userInfo.setName(userName);
//                userInfo.setGender(gender);
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
        }

        return new CommonResult<>(res, t);
    }
}
