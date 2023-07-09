package net.doge.sdk.entity.playlist.menu;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetPlaylistInfo;
import net.doge.model.entity.NetUserInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class PlaylistMenuReq {
    // 歌单相似歌单 API
    private final String SIMILAR_PLAYLIST_API = SdkCommon.prefix + "/related/playlist?id=%s";
    // 歌单收藏者 API
    private final String PLAYLIST_SUBSCRIBERS_API = SdkCommon.prefix + "/playlist/subscribers?id=%s&offset=%s&limit=%s";

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo netPlaylistInfo) {
        int source = netPlaylistInfo.getSource();
        String id = netPlaylistInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(SIMILAR_PLAYLIST_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
//                Long playCount = playlistJson.getLong("playCount");
//                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌单收藏者
     *
     * @return
     */
    public CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo netPlaylistInfo, int limit, int page) {
        int source = netPlaylistInfo.getSource();
        String id = netPlaylistInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(PLAYLIST_SUBSCRIBERS_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("subscribers");
            t = userInfoJson.getIntValue("total");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
//                Integer follow = userJson.getIntValue("follows");
//                Integer followed = userJson.getIntValue("followeds");
//                Integer playlistCount = userJson.getIntValue("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
//                userInfo.setFollow(follow);
//                userInfo.setFollowed(followed);
//                userInfo.setPlaylistCount(playlistCount);
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
