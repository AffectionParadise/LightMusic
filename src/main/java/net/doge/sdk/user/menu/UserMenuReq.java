package net.doge.sdk.user.menu;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.constant.model.RadioType;
import net.doge.model.entity.*;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.ListUtil;
import net.doge.util.StringUtil;
import net.doge.util.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class UserMenuReq {
    // 用户歌单 API
    private final String USER_PLAYLIST_API = SdkCommon.prefix + "/user/playlist?uid=%s&limit=1000";
    // 用户创建歌单 API (QQ)
    private final String USER_CREATED_PLAYLIST_QQ_API = SdkCommon.prefixQQ33 + "/user/songlist?id=%s";
    // 用户收藏歌单 API (QQ)
    private final String USER_COLLECTED_PLAYLIST_QQ_API = SdkCommon.prefixQQ33 + "/user/collect/songlist?id=%s&pageNo=%s&pageSize=%s";
    // 用户收藏专辑 API (QQ)
    private final String USER_COLLECTED_ALBUM_QQ_API = SdkCommon.prefixQQ33 + "/user/collect/album?id=%s&pageNo=%s&pageSize=%s";
    // 用户创建音单 API (猫耳)
    private final String USER_CREATED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=0&p=%s&page_size=%s";
    // 用户收藏音单 API (猫耳)
    private final String USER_COLLECTED_PLAYLIST_ME_API = "https://www.missevan.com/person/getuseralbum?user_id=%s&type=1&p=%s&page_size=%s";

    // 用户专辑 API (堆糖)
    private final String USER_ALBUM_DT_API = "https://www.duitang.com/napi/album/list/by_user/?user_id=%s&start=%s&limit=%s";

    // 用户电台 API
    private final String USER_RADIO_API = SdkCommon.prefix + "/user/audio?uid=%s";
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
    private final String USER_FOLLOWS_API = SdkCommon.prefix + "/user/follows?uid=%s&limit=1000";
    // 用户关注 API (喜马拉雅)
    private final String USER_FOLLOWS_XM_API = "https://www.ximalaya.com/revision/user/following?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户关注 API (猫耳)
    private final String USER_FOLLOWS_ME_API = "https://www.missevan.com/person/getuserattention?type=0&user_id=%s&p=%s&page_size=%s";
    // 用户关注 API (堆糖)
    private final String USER_FOLLOWS_DT_API = "https://www.duitang.com/napi/friendship/follows/?user_id=%s&start=%s&limit=%s";
    // 用户关注 API (哔哩哔哩)
    private final String USER_FOLLOWS_BI_API = "https://api.bilibili.com/x/relation/followings?vmid=%s&pn=%s&ps=%s";
    // 用户粉丝 API
    private final String USER_FOLLOWEDS_API = SdkCommon.prefix + "/user/followeds?uid=%s&offset=%s&limit=%s";
    // 用户粉丝 API (喜马拉雅)
    private final String USER_FOLLOWEDS_XM_API = "https://www.ximalaya.com/revision/user/fans?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户粉丝 API (猫耳)
    private final String USER_FOLLOWEDS_ME_API = "https://www.missevan.com/person/getuserattention?type=1&user_id=%s&p=%s&page_size=%s";
    // 用户粉丝 API (堆糖)
    private final String USER_FOLLOWEDS_DT_API = "https://www.duitang.com/napi/friendship/fans/?user_id=%s&start=%s&limit=%s";
    // 用户粉丝 API (哔哩哔哩)
    private final String USER_FOLLOWEDS_BI_API = "https://api.bilibili.com/x/relation/followers?vmid=%s&pn=%s&ps=%s";

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo netCommentInfo, int limit, int page) {
        int source = netCommentInfo.getSource();
        String uid = StringUtil.encode(netCommentInfo.getUserId());

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(USER_PLAYLIST_API, uid))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
            total.set(playlistArray.size());
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
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
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_QQ_API, uid))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("diss_name");
                    String creator = netCommentInfo.getUsername();
                    Long playCount = playlistJson.getLong("listen_num");
                    Integer trackCount = playlistJson.getInt("song_cnt");
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

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_QQ_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("list");
                t = data.getInt("total");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("dissid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("dissname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("listennum");
                    Integer trackCount = playlistJson.getInt("songnum");
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
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("info");
                JSONArray playlistArray = data.optJSONArray("Datas");
                if (playlistArray != null) {
                    t = data.getJSONObject("pagination").getInt("count");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("id");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getString("username");
                        Integer trackCount = playlistJson.getInt("music_count");
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

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("info");
                JSONArray playlistArray = data.optJSONArray("Datas");
                if (playlistArray != null) {
                    t = data.getJSONObject("pagination").getInt("count");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("id");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getString("username");
                        Integer trackCount = playlistJson.getInt("music_count");
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
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo netCommentInfo, int limit, int page) {
        int source = netCommentInfo.getSource();
        String uid = StringUtil.encode(netCommentInfo.getUserId());

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer total = 0;

        // QQ
        if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(USER_COLLECTED_ALBUM_QQ_API, uid, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albummid");
                String albumName = albumJson.getString("albumname");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QQ);
                String artistId = albumJson.getJSONArray("singer").getJSONObject(0).getString("mid");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("pubtime") * 1000);
                Integer songNum = albumJson.getInt("songnum");
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

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 15;
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, uid, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("div.item");
            String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
            total = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            total += total / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select("div.pic img").first();

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
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

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(USER_PLAYLIST_API, uid))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
            total.set(playlistArray.size());
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
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
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_QQ_API, uid))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("diss_name");
                    String creator = netUserInfo.getName();
                    Long playCount = playlistJson.getLong("listen_num");
                    Integer trackCount = playlistJson.getInt("song_cnt");
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

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_QQ_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("list");
                t = data.getInt("total");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("dissid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("dissname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("listennum");
                    Integer trackCount = playlistJson.getInt("songnum");
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
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("info");
                JSONArray playlistArray = data.optJSONArray("Datas");
                if (playlistArray != null) {
                    t = data.getJSONObject("pagination").getInt("count");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("id");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getString("username");
                        Integer trackCount = playlistJson.getInt("music_count");
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

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("info");
                JSONArray playlistArray = data.optJSONArray("Datas");
                if (playlistArray != null) {
                    t = data.getJSONObject("pagination").getInt("count");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("id");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getString("username");
                        Integer trackCount = playlistJson.getInt("music_count");
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
    public CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 0;

        // QQ
        if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(USER_COLLECTED_ALBUM_QQ_API, uid, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albummid");
                String albumName = albumJson.getString("albumname");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QQ);
                String artistId = albumJson.getJSONArray("singer").getJSONObject(0).getString("mid");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("pubtime") * 1000);
                Integer songNum = albumJson.getInt("songnum");
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

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 15;
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, uid, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("div.item");
            String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select("div.pic img").first();

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
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
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DT_API, uid, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray albumArray = data.getJSONArray("object_list");
            t = page * limit;
            if (data.getInt("more") == 1) t++;
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = albumJson.getJSONObject("user").getString("username");
                String artistId = albumJson.getJSONObject("user").getString("id");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
                Integer songNum = albumJson.getInt("count");

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

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getUserRadios(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_API, uid))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            total.set(radioInfoJson.getInt("count"));
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.getString("category");
                if (!category.isEmpty()) category += "、" + radioJson.getString("secondCategory");
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_XM_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                JSONArray radioArray = data.getJSONArray("albumList");
                t = data.getInt("totalCount");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getString("anchorNickName");
                    String djId = radioJson.getString("anchorUid");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_XM_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                JSONArray radioArray = data.getJSONArray("albumsInfo");
                t = data.getInt("totalCount");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getJSONObject("anchor").getString("anchorNickName");
                    String djId = radioJson.getJSONObject("anchor").getString("anchorUid");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getInt("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
//                    String category = radioJson.getString("type_name");
                    String dj = netUserInfo.getName();
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.optJSONArray("Datas");
                if (radioArray != null) {
                    t = data.getJSONObject("pagination").getInt("count");
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_DB_API, uid, (page - 1) * rn))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select("div.item");
                String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
                t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select("li.title a").first();
                    Element intro = radio.select("li.intro").first();
                    Element img = radio.select("div.pic img").first();

                    String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
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
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_BOOK_RADIO_DB_API, uid, (page - 1) * rn))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select("li.subject-item");
                String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
                t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select("div.info a").first();
                    Element pub = radio.select("div.pub").first();
                    Element img = radio.select("div.pic img").first();

                    String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
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
    public CommonResult<NetMvInfo> getUserVideos(NetUserInfo netUserInfo, int sortType, int page, int limit, String cursor) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();
        AtomicReference<String> cur = new AtomicReference<>();

        String[] orders = {"pubdate", "click", "stow"};

        // 好看
        if (source == NetMusicSource.HK) {
            // 普通视频
            Callable<CommonResult<NetMvInfo>> getNormalVideos = () -> {
                LinkedList<NetMvInfo> r = new LinkedList<>();
                Integer t = 0;

                String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_HK_API, uid, cursor))
                        .header(Header.COOKIE, SdkCommon.HK_COOKIE)
                        .execute()
                        .body();
                JSONObject data = JSONObject.fromObject(mvInfoBody).getJSONObject("data");
                String cs = data.getString("ctime");
                t = data.getInt("has_more") == 0 ? page * limit : (page + 1) * limit;
                JSONArray mvArray = data.getJSONArray("results");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = netUserInfo.getName();
                    String creatorId = netUserInfo.getId();
                    String coverImgUrl = mvJson.getString("poster");
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
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
                LinkedList<NetMvInfo> r = new LinkedList<>();
                Integer t = 0;

                String mvInfoBody = HttpRequest.get(String.format(USER_SMALL_VIDEO_HK_API, uid))
                        .header(Header.COOKIE, SdkCommon.HK_COOKIE)
                        .execute()
                        .body();
                JSONObject data = JSONObject.fromObject(mvInfoBody).getJSONObject("data");
                String cs = data.getString("ctime");
                t = data.getBoolean("has_more") ? (page + 1) * limit : page * limit;
                JSONArray mvArray = data.getJSONArray("results");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i).getJSONObject("content");

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = netUserInfo.getName();
                    String creatorId = netUserInfo.getId();
                    String coverImgUrl = mvJson.getString("poster");
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
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
                    if (ListUtil.search(taskList, task) == 0) cur.set(result.cursor);
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
            String mvInfoBody = HttpRequest.get(String.format(USER_VIDEO_BI_API, orders[sortType], uid, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvInfoBody).getJSONObject("data");
            total.set(data.getJSONObject("page").getInt("count"));
            JSONArray mvArray = data.getJSONObject("list").getJSONArray("vlist");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("play");
                Double duration = TimeUtil.toSeconds(mvJson.getString("length"));
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

        return new CommonResult<>(res, total.get(), cur.get());
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserFollows(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_API, id))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("follow");
            t = userArray.size();
            for (int i = (page - 1) * limit, len = Math.min(userArray.size(), page * limit); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getInt("follows");
                Integer followed = userJson.getInt("followeds");
                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_XM_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("followingsPageInfo");
            t = data.getInt("totalCount");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("uid");
                String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getInt("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "https:" + userJson.getString("coverPath");
                Integer follow = userJson.getInt("followingCount");
                Integer followed = userJson.getInt("followerCount");
                Integer radioCount = userJson.getInt("albumCount");
                Integer programCount = userJson.getInt("trackCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
//                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_ME_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
//                String sign = userJson.getString("userintro");
                String avatarThumbUrl = userJson.getString("boardiconurl2");
                Integer followed = userJson.getInt("fansnum");
                Integer programCount = userJson.getInt("soundnumchecked");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_DT_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray userArray = data.getJSONArray("object_list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                Integer follow = userJson.getInt("followCount");
                Integer followed = userJson.getInt("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_BI_API, id, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getInt("total");
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

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
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
    public CommonResult<NetUserInfo> getUserFolloweds(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("followeds");
            t = userInfoJson.getInt("size");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getInt("follows");
                Integer followed = userJson.getInt("followeds");
                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_XM_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("fansPageInfo");
            t = data.getInt("totalCount");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("uid");
                String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getInt("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "https:" + userJson.getString("coverPath");
                Integer follow = userJson.getInt("followingCount");
                Integer followed = userJson.getInt("followerCount");
                Integer radioCount = userJson.getInt("albumCount");
                Integer programCount = userJson.getInt("trackCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
//                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_ME_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
//                String sign = userJson.getString("userintro");
                String avatarThumbUrl = userJson.getString("boardiconurl2");
                Integer followed = userJson.getInt("fansnum");
                Integer programCount = userJson.getInt("soundnumchecked");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_DT_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray userArray = data.getJSONArray("object_list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                Integer follow = userJson.getInt("followCount");
                Integer followed = userJson.getInt("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_BI_API, id, page, limit))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getInt("total");
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

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
