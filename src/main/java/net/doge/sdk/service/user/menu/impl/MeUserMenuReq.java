package net.doge.sdk.service.user.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class MeUserMenuReq {
    private static MeUserMenuReq instance;

    private MeUserMenuReq() {
    }

    public static MeUserMenuReq getInstance() {
        if (instance == null) instance = new MeUserMenuReq();
        return instance;
    }

    // 用户创建音单 API (猫耳)
    private final String USER_CREATED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=0&p=%s&page_size=%s";
    // 用户收藏音单 API (猫耳)
    private final String USER_COLLECTED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=1&p=%s&page_size=%s";
    // 用户电台 API (猫耳)
    private final String USER_RADIO_ME_API = "https://www.missevan.com/dramaapi/getuserdramas?user_id=%s&s=&order=0&page=%s&page_size=%s";
    // 用户收藏电台 API (猫耳)
    private final String USER_SUB_RADIO_ME_API = "https://www.missevan.com/dramaapi/getusersubscriptions?user_id=%s&page=%s&page_size=%s";
    // 用户关注 API (猫耳)
    private final String USER_FOLLOWS_ME_API = "https://www.missevan.com/person/getuserattention?type=0&user_id=%s&p=%s&page_size=%s";
    // 用户粉丝 API (猫耳)
    private final String USER_FANS_ME_API = "https://www.missevan.com/person/getuserattention?type=1&user_id=%s&p=%s&page_size=%s";

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        String id = userInfo.getId();
        // 创建的音单
        Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            int t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_ME_API, id, page, limit))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("info");
            JSONArray playlistArray = data.getJSONArray("Datas");
            if (JsonUtil.notEmpty(playlistArray)) {
                t = data.getJSONObject("pagination").getIntValue("count");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    Integer trackCount = playlistJson.getIntValue("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }
            }

            return new CommonResult<>(r, t);
        };
        // 收藏的音单
        Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            int t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_ME_API, id, page, limit))
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("info");
            JSONArray playlistArray = data.getJSONArray("Datas");
            if (JsonUtil.notEmpty(playlistArray)) {
                t = data.getJSONObject("pagination").getIntValue("count");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    Integer trackCount = playlistJson.getIntValue("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }
            }

            return new CommonResult<>(r, t);
        };

        MultiCommonResultCallableExecutor<NetPlaylistInfo> executor = new MultiCommonResultCallableExecutor<>();
        executor.submit(getCreatedPlaylists);
        executor.submit(getCollectedPlaylists);
        return executor.getResult();
    }

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

            String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_ME_API, id, page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
//                    String category = radioJson.getString("type_name");
                String dj = userInfo.getName();
                Long playCount = radioJson.getLong("view_count");
                String coverImgThumbUrl = radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
//                    radioInfo.setCategory(category);
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
            int t = 0;

            String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_ME_API, id, page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            JSONArray radioArray = data.getJSONArray("Datas");
            if (JsonUtil.notEmpty(radioArray)) {
                t = data.getJSONObject("pagination").getIntValue("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
//                    String category = radioJson.getString("type");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
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
        String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_ME_API, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("info");
        JSONArray userArray = data.getJSONArray("Datas");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("username");
            String gender = "保密";
//                String sign = userJson.getString("userintro");
            String avatarThumbUrl = userJson.getString("boardiconurl2");
            Integer fan = userJson.getIntValue("fansnum");
            Integer programCount = userJson.getIntValue("soundnumchecked");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.ME);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setFan(fan);
            userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

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
        String userInfoBody = HttpRequest.get(String.format(USER_FANS_ME_API, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("info");
        JSONArray userArray = data.getJSONArray("Datas");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = 0, len = userArray.size(); i < len; i++) {
            JSONObject userJson = userArray.getJSONObject(i);

            String userId = userJson.getString("id");
            String userName = userJson.getString("username");
            String gender = "保密";
//                String sign = userJson.getString("userintro");
            String avatarThumbUrl = userJson.getString("boardiconurl2");
            Integer fan = userJson.getIntValue("fansnum");
            Integer programCount = userJson.getIntValue("soundnumchecked");

            NetUserInfo userInfo = new NetUserInfo();
            userInfo.setSource(NetMusicSource.ME);
            userInfo.setId(userId);
            userInfo.setName(userName);
            userInfo.setGender(gender);
            userInfo.setAvatarThumbUrl(avatarThumbUrl);
            userInfo.setFan(fan);
            userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                userInfo.setAvatarThumb(avatarThumb);
            });

            res.add(userInfo);
        }

        return new CommonResult<>(res, t);
    }
}
