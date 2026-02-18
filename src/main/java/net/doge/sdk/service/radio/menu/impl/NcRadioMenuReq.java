package net.doge.sdk.service.radio.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcRadioMenuReq {
    private static NcRadioMenuReq instance;

    private NcRadioMenuReq() {
    }

    public static NcRadioMenuReq getInstance() {
        if (instance == null) instance = new NcRadioMenuReq();
        return instance;
    }

    // 电台订阅者 API (网易云)
    private final String RADIO_SUBSCRIBERS_NC_API = "https://music.163.com/api/djradio/subscriber";

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo radioInfo, int page, int limit) {
        List<NetUserInfo> res = new LinkedList<>();
        int t;

        String id = radioInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String userInfoBody = SdkCommon.ncRequest(Method.POST, RADIO_SUBSCRIBERS_NC_API,
                        String.format("{\"id\":\"%s\",\"time\":-1,\"limit\":1000,\"total\":true}", id), options)
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONArray userArray = userInfoJson.getJSONArray("subscribers");
        t = userArray.size();
        for (int i = (page - 1) * limit, len = Math.min(userArray.size(), page * limit); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("userId");
            String userName = userJson.getString("nickname");
            Integer gen = userJson.getIntValue("gender");
            String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
            String avatarThumbUrl = userJson.getString("avatarUrl");
//                Integer follow = userJson.getIntValue("follows");
//                Integer fan = userJson.getIntValue("followeds");
//                Integer playlistCount = userJson.getIntValue("playlistCount");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
//                userInfo.setFollow(follow);
//                userInfo.setFan(fan);
//                userInfo.setPlaylistCount(playlistCount);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            res.add(userInfo);
        }

        return new CommonResult<>(res, t);
    }
}
