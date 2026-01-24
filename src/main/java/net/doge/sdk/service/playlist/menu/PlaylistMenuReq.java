package net.doge.sdk.service.playlist.menu;

import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlaylistMenuReq {
    private static PlaylistMenuReq instance;

    private PlaylistMenuReq() {
    }

    public static PlaylistMenuReq getInstance() {
        if (instance == null) instance = new PlaylistMenuReq();
        return instance;
    }
    
    // 歌单相似歌单 API
    private final String SIMILAR_PLAYLIST_API = "https://music.163.com/playlist?id=%s";
    // 歌单收藏者 API
    private final String PLAYLIST_SUBSCRIBERS_API = "https://music.163.com/weapi/playlist/subscribers";

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo netPlaylistInfo) {
        int source = netPlaylistInfo.getSource();
        String id = netPlaylistInfo.getId();

        List<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapiPC();
            String playlistInfoBody = SdkCommon.ncRequest(Method.GET, String.format(SIMILAR_PLAYLIST_API, id), "{}", options)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(playlistInfoBody);
            Elements playlistArray = doc.select(".m-rctlist.f-cb li");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                Element playlist = playlistArray.get(i);
                Elements a = playlist.select(".f-thide a");
                Elements ca = playlist.select("a.nm.nm.f-thide.s-fc3");
                Elements img = playlist.select(".cver.u-cover.u-cover-3 img");

                String playlistId = RegexUtil.getGroup1("/playlist\\?id=(\\d+)", a.attr("href"));
                String playlistName = a.text();
                String creator = ca.text();
                String creatorId = RegexUtil.getGroup1("/user/home\\?id=(\\d+)", ca.attr("href"));
                String coverImgThumbUrl = img.attr("src").replaceFirst("param=\\d+y\\d+", "param=500y500");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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
    public CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo playlistInfo, int page, int limit) {
        int source = playlistInfo.getSource();
        String id = playlistInfo.getId();

        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String userInfoBody = SdkCommon.ncRequest(Method.POST, PLAYLIST_SUBSCRIBERS_API,
                            String.format("{\"id\":\"%s\",\"offset\":%s,\"limit\":%s}", id, (page - 1) * limit, limit), options)
                    .executeAsync()
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
        }

        return new CommonResult<>(res, t);
    }
}
