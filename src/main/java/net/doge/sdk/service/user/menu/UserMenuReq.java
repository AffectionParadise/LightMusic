package net.doge.sdk.service.user.menu;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.RadioType;
import net.doge.entity.service.*;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Header;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.collection.ListUtil;
import net.doge.util.core.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class UserMenuReq {
    private static UserMenuReq instance;

    private UserMenuReq() {
    }

    public static UserMenuReq getInstance() {
        if (instance == null) instance = new UserMenuReq();
        return instance;
    }
    
    // 用户歌单 API
    private final String USER_PLAYLIST_API = "https://music.163.com/api/user/playlist";
    // 用户创建歌单 API (QQ)
    private final String USER_CREATED_PLAYLIST_QQ_API = "https://c.y.qq.com/rsc/fcgi-bin/fcg_user_created_diss?" +
            "hostUin=0&hostuin=%s&sin=0&size=200&g_tk=5381&loginUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";
    // 用户收藏歌单 API (QQ)
    private final String USER_COLLECTED_PLAYLIST_QQ_API = "https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg?ct=20&cid=205360956&userid=%s&reqtype=3&sin=%s&ein=%s";
    // 用户收藏专辑 API (QQ)
    private final String USER_COLLECTED_ALBUM_QQ_API = "https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg?ct=20&cid=205360956&userid=%s&reqtype=2&sin=%s&ein=%s";
    // 用户创建音单 API (猫耳)
    private final String USER_CREATED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=0&p=%s&page_size=%s";
    // 用户收藏音单 API (猫耳)
    private final String USER_COLLECTED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=1&p=%s&page_size=%s";

    // 用户专辑 API (堆糖)
    private final String USER_ALBUM_DT_API = "https://www.duitang.com/napi/album/list/by_user/?user_id=%s&start=%s&limit=%s";

    // 用户电台 API
    private final String USER_RADIO_API = "https://music.163.com/weapi/djradio/get/byuser";
    // 用户电台 API (喜马拉雅)
    private final String USER_RADIO_XM_API = "https://www.ximalaya.com/revision/user/pub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户收藏电台 API (喜马拉雅)
    private final String USER_SUB_RADIO_XM_API = "https://www.ximalaya.com/revision/user/sub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户电台 API (猫耳)
    private final String USER_RADIO_ME_API = "https://www.missevan.com/dramaapi/getuserdramas?user_id=%s&s=&order=0&page=%s&page_size=%s";
    // 用户收藏电台 API (猫耳)
    private final String USER_SUB_RADIO_ME_API = "https://www.missevan.com/dramaapi/getusersubscriptions?user_id=%s&page=%s&page_size=%s";
    // 用户专辑 API (豆瓣)
    private final String USER_ALBUM_DB_API = "https://music.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户电台 API (豆瓣)
    private final String USER_RADIO_DB_API = "https://movie.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户图书电台 API (豆瓣)
    private final String USER_BOOK_RADIO_DB_API = "https://book.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";

    // 用户视频 API (好看)
    private final String USER_VIDEO_HK_API = "https://haokan.baidu.com/web/author/listall?app_id=%s&rn=20&ctime=%s";
    // 用户小视频 API (好看)
    private final String USER_SMALL_VIDEO_HK_API = "https://haokan.baidu.com/web/author/listall?app_id=%s&rn=20&video_type=haokan|tabhubVideo";
    // 用户视频 API (哔哩哔哩)
    private final String USER_VIDEO_BI_API = "https://api.bilibili.com/x/space/wbi/arc/search?order=%s&mid=%s&pn=%s&ps=%s";

    // 用户关注 API
    private final String USER_FOLLOWS_API = "https://music.163.com/weapi/user/getfollows/%s";
    // 用户关注 API (喜马拉雅)
    private final String USER_FOLLOWS_XM_API = "https://www.ximalaya.com/revision/user/following?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户关注 API (猫耳)
    private final String USER_FOLLOWS_ME_API = "https://www.missevan.com/person/getuserattention?type=0&user_id=%s&p=%s&page_size=%s";
    // 用户关注 API (5sing)
    private final String USER_FOLLOWS_FS_API = "http://5sing.kugou.com/%s/friend/%s.html";
    // 用户关注 API (堆糖)
    private final String USER_FOLLOWS_DT_API = "https://www.duitang.com/napi/friendship/follows/?user_id=%s&start=%s&limit=%s";
    // 用户关注 API (哔哩哔哩)
    private final String USER_FOLLOWS_BI_API = "https://api.bilibili.com/x/relation/followings?vmid=%s&pn=%s&ps=%s";

    // 用户粉丝 API
    private final String USER_FANS_API = "https://music.163.com/eapi/user/getfolloweds/%s";
    // 用户粉丝 API (喜马拉雅)
    private final String USER_FANS_XM_API = "https://www.ximalaya.com/revision/user/fans?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户粉丝 API (猫耳)
    private final String USER_FANS_ME_API = "https://www.missevan.com/person/getuserattention?type=1&user_id=%s&p=%s&page_size=%s";
    // 用户粉丝 API (5sing)
    private final String USER_FANS_FS_API = "http://5sing.kugou.com/%s/fans/%s.html";
    // 用户粉丝 API (堆糖)
    private final String USER_FANS_DT_API = "https://www.duitang.com/napi/friendship/fans/?user_id=%s&start=%s&limit=%s";
    // 用户粉丝 API (哔哩哔哩)
    private final String USER_FANS_BI_API = "https://api.bilibili.com/x/relation/followers?vmid=%s&pn=%s&ps=%s";

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo commentInfo, int page, int limit) {
        int source = commentInfo.getSource();
        String id = UrlUtil.encodeAll(commentInfo.getUserId());
        String name = commentInfo.getUsername();

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(source);
        userInfo.setId(id);
        userInfo.setName(name);

        return getUserPlaylists(userInfo, page, limit);
    }

    /**
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo commentInfo, int page, int limit) {
        int source = commentInfo.getSource();
        String id = UrlUtil.encodeAll(commentInfo.getUserId());

        NetUserInfo userInfo = new NetUserInfo();
        userInfo.setSource(source);
        userInfo.setId(id);

        return getUserAlbums(userInfo, page, limit);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        String id = userInfo.getId();

        List<NetPlaylistInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String playlistInfoBody = SdkCommon.ncRequest(Method.POST, USER_PLAYLIST_API,
                            String.format("{\"uid\":\"%s\",\"offset\":%s,\"limit\":%s,\"includeVideo\":true}", id, (page - 1) * limit, limit), options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
            total.set(playlistInfoJson.getBooleanValue("more") ? page * limit + 1 : page * limit);
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
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            // 创建的歌单
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                List<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_QQ_API, id))
                        .header(Header.REFERER, "https://y.qq.com/portal/profile.html")
                        .executeAsStr();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                if (JsonUtil.notEmpty(data)) {
                    JSONArray playlistArray = data.getJSONArray("disslist");
                    t = playlistArray.size();
                    for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("tid");
                        if ("0".equals(playlistId)) continue;
                        String playlistName = playlistJson.getString("diss_name");
                        String creator = userInfo.getName();
                        Long playCount = playlistJson.getLong("listen_num");
                        Integer trackCount = playlistJson.getIntValue("song_cnt");
                        String coverImgThumbUrl = playlistJson.getString("diss_cover");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
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
            // 收藏的歌单
            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                List<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_QQ_API, id, (page - 1) * limit, page * limit))
                        .executeAsStr();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("cdlist");
                if (JsonUtil.notEmpty(playlistArray)) {
                    t = data.getIntValue("totaldiss");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("dissid");
                        if ("0".equals(playlistId)) continue;
                        String playlistName = playlistJson.getString("dissname");
                        String creator = playlistJson.getString("nickname");
                        Long playCount = playlistJson.getLong("listennum");
                        Integer trackCount = playlistJson.getIntValue("songnum");
                        String coverImgThumbUrl = playlistJson.getString("logo");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
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

            List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCollectedPlaylists));

            List<List<NetPlaylistInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetPlaylistInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            // 创建的音单
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                List<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

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
                Integer t = 0;

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

            List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCollectedPlaylists));

            List<List<NetPlaylistInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetPlaylistInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        return new CommonResult<>(res, total.get());
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        String id = userInfo.getId();

        List<NetAlbumInfo> res = new LinkedList<>();
        Integer total = 0;

        // QQ
        if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(USER_COLLECTED_ALBUM_QQ_API, id, (page - 1) * limit, page * limit - 1))
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("totalalbum");
            JSONArray albumArray = data.getJSONArray("albumlist");
            if (JsonUtil.notEmpty(albumArray)) {
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albummid");
                    String albumName = albumJson.getString("albumname");
                    String artist = SdkUtil.parseArtist(albumJson);
                    String artistId = SdkUtil.parseArtistId(albumJson);
                    String publishTime = TimeUtil.msToDate(albumJson.getLong("pubtime") * 1000);
                    Integer songNum = albumJson.getIntValue("songnum");
                    String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.QQ);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setArtistId(artistId);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(albumInfo);
                }
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 15;
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, id, (page - 1) * rn))
                    .executeAsStr();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select(".item");
            String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
            total = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            total += total / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select(".pic img").first();

                String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                String radioName = a.text();
                String coverImgThumbUrl = img.attr("src");
                String[] sp = intro.text().split(" / ");
                String artist = sp[0];
                String pubTime = sp[1];

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(radioId);
                albumInfo.setName(radioName);
                albumInfo.setArtist(artist);
                albumInfo.setPublishTime(pubTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DT_API, id, (page - 1) * limit, limit))
                    .executeAsStr();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray albumArray = data.getJSONArray("object_list");
            total = page * limit;
            if (data.getIntValue("more") == 1) total++;
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);
                JSONObject user = albumJson.getJSONObject("user");

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = user.getString("username");
                String artistId = user.getString("id");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
                Integer songNum = albumJson.getIntValue("count");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        int source = userInfo.getSource();
        String id = userInfo.getId();

        List<NetRadioInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String radioInfoBody = SdkCommon.ncRequest(Method.POST, USER_RADIO_API, String.format("{\"userId\":\"%s\"}", id), options)
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            total.set(radioInfoJson.getIntValue("count"));
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
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            Callable<CommonResult<NetRadioInfo>> getCreatedRadios = () -> {
                List<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

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
                    radioInfo.setSource(NetMusicSource.XM);
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
                Integer t = 0;

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
                    radioInfo.setSource(NetMusicSource.XM);
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

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSubRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            Callable<CommonResult<NetRadioInfo>> getCreatedRadios = () -> {
                List<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

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
                Integer t = 0;

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

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSubRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            Callable<CommonResult<NetRadioInfo>> getRadios = () -> {
                List<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_DB_API, id, (page - 1) * rn))
                        .executeAsStr();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select(".item");
                String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
                t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select("li.title a").first();
                    Element intro = radio.select("li.intro").first();
                    Element img = radio.select(".pic img").first();

                    String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                    String radioName = a.text();
                    String dj = StringUtil.shorten(intro.text(), 100);
                    String coverImgThumbUrl = img.attr("src");
                    String category = "电影";

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(radioInfo);
                }
                return new CommonResult<>(r, t);
            };

            // 图书电台
            Callable<CommonResult<NetRadioInfo>> getBookRadios = () -> {
                List<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_BOOK_RADIO_DB_API, id, (page - 1) * rn))
                        .executeAsStr();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select("li.subject-item");
                String ts = RegexUtil.getGroup1("\\((\\d+)\\)", doc.select("#db-usr-profile .info h1").text());
                t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select(".info a").first();
                    Element pub = radio.select(".pub").first();
                    Element img = radio.select(".pic img").first();

                    String radioId = RegexUtil.getGroup1("subject/(\\d+)/", a.attr("href"));
                    String radioName = a.text();
                    String dj = pub.text().trim();
                    String coverImgThumbUrl = img.attr("src");
                    String category = "书籍";

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setType(RadioType.BOOK);
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(radioInfo);
                }
                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getBookRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        return new CommonResult<>(res, total.get());
    }

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int sortType, int page, int limit, String cursor) {
        int source = userInfo.getSource();
        String id = userInfo.getId();

        List<NetMvInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();
        AtomicReference<String> cur = new AtomicReference<>();

        String[] orders = {"pubdate", "click", "stow"};

        // 好看
        if (source == NetMusicSource.HK) {
            // 普通视频
            Callable<CommonResult<NetMvInfo>> getNormalVideos = () -> {
                List<NetMvInfo> r = new LinkedList<>();
                Integer t = 0;

                String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_HK_API, id, cursor))
                        .cookie(SdkCommon.HK_COOKIE)
                        .executeAsStr();
                JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
                String cs = data.getString("ctime");
                t = data.getIntValue("has_more") == 0 ? page * limit : (page + 1) * limit;
                JSONArray mvArray = data.getJSONArray("results");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = userInfo.getName();
                    String creatorId = userInfo.getId();
                    String coverImgUrl = mvJson.getString("poster");
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                    String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.HK);
                    mvInfo.setId(mvId);
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

                return new CommonResult<>(r, t, cs);
            };
            // 小视频
            Callable<CommonResult<NetMvInfo>> getSmallVideos = () -> {
                List<NetMvInfo> r = new LinkedList<>();
                Integer t = 0;

                String mvInfoBody = HttpRequest.get(String.format(USER_SMALL_VIDEO_HK_API, id))
                        .cookie(SdkCommon.HK_COOKIE)
                        .executeAsStr();
                JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
                String cs = data.getString("ctime");
                t = data.getBoolean("has_more") ? (page + 1) * limit : page * limit;
                JSONArray mvArray = data.getJSONArray("results");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = userInfo.getName();
                    String creatorId = userInfo.getId();
                    String coverImgUrl = mvJson.getString("poster");
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = DurationUtil.toSeconds(mvJson.getString("duration"));
                    String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.HK);
                    mvInfo.setId(mvId);
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

                return new CommonResult<>(r, t, cs);
            };
            List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getNormalVideos));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSmallVideos));

            List<List<NetMvInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetMvInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                    // 用普通视频的 cursor
                    if (ListUtil.indexOf(taskList, task) == 0) cur.set(result.cursor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtil.joinAll(rl));
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_BI_API, orders[sortType], id, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data");
            if (JsonUtil.notEmpty(data)) {
                total.set(data.getJSONObject("page").getIntValue("count"));
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
                    mvInfo.setBvid(bvId);
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
        }

        return new CommonResult<>(res, total.get(), cur.get());
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFollows(NetUserInfo netUserInfo, int page, int limit) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_FOLLOWS_API, id),
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
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
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
                userInfo.setSource(NetMusicSource.XM);
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
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
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
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_FS_API, id, page))
                    .executeAsStr();
            Document doc = Jsoup.parse(userInfoBody);

            // 判断主页 ui 类型
            int pageType = 1;
            if (!doc.select(".mainnav").isEmpty()) pageType = 2;
            else if (!doc.select(".right h1").isEmpty()) pageType = 3;
            else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
            else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
            else if (!doc.select(".user_name").isEmpty()) pageType = 6;

            switch (pageType) {
                case 1:
                    Elements pageElem = doc.select(".page_number em");
                    String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                    t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 42 + 1) * limit + 1 : limit;
                    break;
                case 2:
                    pageElem = doc.select(".page_list span");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 3:
                    pageElem = doc.select("a.page_t_next");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 4:
                case 6:
                    pageElem = doc.select(".msg_page_list a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 2).attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 5:
                    pageElem = doc.select(".page span");
                    pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
            }

            // 主页 ui 不同
            Elements userArray;
            if (pageType == 1) userArray = doc.select(".follow_list li a");
            else if (pageType == 2) userArray = doc.select(".mid.friend_list li a");
            else if (pageType == 3) userArray = doc.select(".fans_list li a");
            else if (pageType == 4) userArray = doc.select("li dl.c_wap");
            else if (pageType == 5) userArray = doc.select(".single_care li");
            else userArray = doc.select("dl.c_ft");
            if (pageType == 4 || pageType == 6) {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Element a = user.select(pageType == 6 ? "h2.c_ft a" : "h2.c_wap a").first();
                    Elements img = user.select("dt.lt img");
                    Elements la = user.select("label a");

                    String userId = RegexUtil.getGroup1("/(\\d+)", a.attr("href"));
                    String userName = a.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
                    Integer follow = Integer.parseInt(la.first().text());
                    Integer fan = Integer.parseInt(la.last().text());

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            } else if (pageType == 5) {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Elements a = user.select("a");
                    Elements img = user.select("img");

                    String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", a.attr("href"));
                    String userName = a.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            } else {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Elements img = user.select("img");

                    String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", user.attr("href"));
                    String userName = user.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_DT_API, id, (page - 1) * limit, limit))
                    .executeAsStr();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray userArray = data.getJSONArray("object_list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                Integer follow = userJson.getIntValue("followCount");
                Integer fan = userJson.getIntValue("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
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
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFans(NetUserInfo netUserInfo, int page, int limit) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/user/getfolloweds");
            String userInfoBody = SdkCommon.ncRequest(Method.POST, String.format(USER_FANS_API, id),
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
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
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
                userInfo.setSource(NetMusicSource.XM);
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
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
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
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String userInfoBody = HttpRequest.get(String.format(USER_FANS_FS_API, id, page))
                    .executeAsStr();
            Document doc = Jsoup.parse(userInfoBody);

            // 判断主页 ui 类型
            int pageType = 1;
            if (!doc.select(".mainnav").isEmpty()) pageType = 2;
            else if (!doc.select(".right h1").isEmpty()) pageType = 3;
            else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
            else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
            else if (!doc.select(".user_name").isEmpty()) pageType = 6;

            switch (pageType) {
                case 1:
                    Elements pageElem = doc.select(".page_number em");
                    String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                    t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 42 + 1) * limit + 1 : limit;
                    break;
                case 2:
                    pageElem = doc.select(".page_list span");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 3:
                    pageElem = doc.select("a.page_t_next");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 4:
                case 6:
                    pageElem = doc.select(".msg_page_list a");
                    pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 2).attr("href"));
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
                case 5:
                    pageElem = doc.select(".page span");
                    pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                    t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                    break;
            }

            // 主页 ui 不同
            Elements userArray;
            if (pageType == 1) userArray = doc.select(".follow_list li a");
            else if (pageType == 2) userArray = doc.select(".mid.friend_list li a");
            else if (pageType == 3) userArray = doc.select(".fans_list li a");
            else if (pageType == 4) userArray = doc.select("li dl.c_wap");
            else if (pageType == 5) userArray = doc.select(".single_care li");
            else userArray = doc.select("dl.c_ft");
            if (pageType == 4 || pageType == 6) {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Element a = user.select(pageType == 6 ? "h2.c_ft a" : "h2.c_wap a").first();
                    Elements img = user.select("dt.lt img");
                    Elements la = user.select("label a");

                    String userId = RegexUtil.getGroup1("/(\\d+)", a.attr("href"));
                    String userName = a.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
                    Integer follow = Integer.parseInt(la.first().text());
                    Integer fan = Integer.parseInt(la.last().text());

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            } else if (pageType == 5) {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Elements a = user.select("a");
                    Elements img = user.select("img");

                    String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", a.attr("href"));
                    String userName = a.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            } else {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Element user = userArray.get(i);
                    Elements img = user.select("img");

                    String userId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", user.attr("href"));
                    String userName = user.attr("title");
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_FANS_DT_API, id, (page - 1) * limit, limit))
                    .executeAsStr();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray userArray = data.getJSONArray("object_list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                Integer follow = userJson.getIntValue("followCount");
                Integer fan = userJson.getIntValue("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
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
        }

        return new CommonResult<>(res, t);
    }
}
