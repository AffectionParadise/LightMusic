package net.doge.sdk.playlist.rcmd;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.NetMusicSource;
import net.doge.constants.Tags;
import net.doge.models.entities.NetPlaylistInfo;
import net.doge.models.server.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.utils.ListUtil;
import net.doge.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class HighQualityPlaylistReq {
    // 精品歌单 API
    private final String HIGH_QUALITY_PLAYLIST_API
            = SdkCommon.prefix + "/top/playlist/highquality?cat=%s&limit=100";
    // 网友精选碟(最热) API
    private final String HOT_PICKED_PLAYLIST_API
            = SdkCommon.prefix + "/top/playlist?cat=%s&limit=%s&offset=%s";
    // 网友精选碟(最新) API
    private final String NEW_PICKED_PLAYLIST_API
            = SdkCommon.prefix + "/top/playlist?order=new&cat=%s&limit=%s&offset=%s";
    // 推荐分类歌单(最热) API (酷狗)
    private final String CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=6&c=%s&p=%s";
    // 推荐分类歌单(热藏) API (酷狗)
    private final String HOT_COLLECTED_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=3&c=%s&p=%s";
    // 推荐分类歌单(飙升) API (酷狗)
    private final String UP_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=8&c=%s&p=%s";
    // 热门歌单 API (酷狗)
    private final String HOT_PLAYLIST_KG_API
            = "http://mobilecdnbj.kugou.com/api/v5/special/recommend?recommend_expire=0&sign=52186982747e1404d426fa3f2a1e8ee4&plat=0&uid=0&version=9108&page=1&area_code=1&appid=1005&mid=286974383886022203545511837994020015101&_t=1545746286";
    // 分类歌单 API (QQ)
    private final String CAT_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";
    // 热门歌单 API (酷我)
    private final String HOT_PLAYLIST_KW_API
            = "https://www.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot&httpsStatus=1";
    // 默认歌单(热门) API (酷我)
    private final String DEFAULT_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot";
    // 分类歌单 API (酷我)
    private final String CAT_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?loginUid=0&loginSid=0&appUid=76039576&id=%s&pn=%s&rn=%s";
    // 分类歌单 API 2 (酷我)
    private final String CAT_PLAYLIST_KW_API_2
            = "http://mobileinterfaces.kuwo.cn/er.s?type=get_pc_qz_data&f=web&id=%s&prod=pc";
    // 推荐歌单 API(最热) (咪咕)
    private final String REC_HOT_PLAYLIST_MG_API
            = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=1";
    // 分类歌单 API (咪咕)
    private final String CAT_PLAYLIST_MG_API
            = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-listbytag?tagId=%s&pageNumber=%s&templateVersion=1";
    // 分类歌单 API (千千)
    private final String CAT_PLAYLIST_QI_API
            = "https://music.91q.com/v1/tracklist/list?appid=16073360&pageNo=%s&pageSize=%s&subCateId=%s&timestamp=%s";
    // 分类歌单 API (猫耳)
    private final String CAT_PLAYLIST_ME_API
            = "https://www.missevan.com/explore/tagalbum?order=0&tid=%s&p=%s&pagesize=%s";
    // 探索歌单 API (猫耳)
    private final String EXP_PLAYLIST_ME_API
            = "https://www.missevan.com/explore/getAlbumFromTag/%s";
    
    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public CommonResult<NetPlaylistInfo> getHighQualityPlaylists(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.playlistTag.get(tag);

        // 网易云
        // 精品歌单(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getHighQualityPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[0])) {
                String playlistInfoBody = HttpRequest.get(String.format(HIGH_QUALITY_PLAYLIST_API, s[0]))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.optJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = ct != null ? ct.getString("nickname") : "";
                    String creatorId = ct != null ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
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
            return new CommonResult<>(res, t);
        };
        // 网友精选碟(最热)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getHotPickedPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_PICKED_PLAYLIST_API, s[1], limit, (page - 1) * limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getInt("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.optJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = ct != null ? ct.getString("nickname") : "";
                    String creatorId = ct != null ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
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
            return new CommonResult<>(res, t);
        };
        // 网友精选碟(最新)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPickedPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_PICKED_PLAYLIST_API, s[1], limit, (page - 1) * limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getInt("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.optJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = ct != null ? ct.getString("nickname") : "";
                    String creatorId = ct != null ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(name);
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
            return new CommonResult<>(res, t);
        };

        // 酷狗
        // 推荐歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = StringUtil.parseNumber(playlistJson.getString("total_play_count"));
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(热藏)
        Callable<CommonResult<NetPlaylistInfo>> getHotCollectedTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_COLLECTED_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = StringUtil.parseNumber(playlistJson.getString("total_play_count"));
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(飙升)
        Callable<CommonResult<NetPlaylistInfo>> getUpTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(UP_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = StringUtil.parseNumber(playlistJson.getString("total_play_count"));
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 热门歌单(这个接口不分页，分开处理)
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(HOT_PLAYLIST_KG_API))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.getInt("songcount");
                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
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

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 分类推荐歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[3])) {
                String cat = s[3];
                boolean isAll = "10000000".equals(cat);
                String url;
                if (isAll) {
                    url = CAT_PLAYLIST_QQ_API + StringUtil.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_playlist_by_tag\"," +
                                    "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":5,\"cur_page\":%s}," +
                                    "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
                } else {
                    url = CAT_PLAYLIST_QQ_API + StringUtil.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_category_content\"," +
                                    "\"param\":{" +
                                    "\"titleid\":%s," +
                                    "\"caller\":\"0\"," +
                                    "\"category_id\":%s," +
                                    "\"size\":%s," +
                                    "\"page\":%s," +
                                    "\"use_page\":1}," +
                                    "\"module\":\"playlist.PlayListCategoryServer\"}}", cat, cat, limit, page - 1));
                }
                String playlistInfoBody = HttpRequest.get(url)
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                if (isAll) {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray playlistArray = data.getJSONArray("v_playlist");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator_info").getString("nick");
                        Long playCount = playlistJson.getLong("access_num");
                        String coverImgThumbUrl = playlistJson.getString("cover_url_small");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                } else {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data").getJSONObject("content");
                    t = data.getInt("total_cnt");
                    JSONArray playlistArray = data.getJSONArray("v_item");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("basic");

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator").getString("nick");
                        Long playCount = playlistJson.getLong("play_cnt");
                        String coverImgThumbUrl = playlistJson.getJSONObject("cover").getString("small_url");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        // 热门歌单
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(HOT_PLAYLIST_KW_API, page, limit))
                    .setFollowRedirects(true)
                    .execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
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

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 默认歌单(热门)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getDefaultPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(DEFAULT_PLAYLIST_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
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

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[4])) {
                String[] sp = s[4].split(" ");
                // 根据 digest 信息请求不同的分类歌单接口
                if ("43".equals(sp[1])) {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API_2, sp[0])).execute();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONArray playlistArray = JSONArray.fromObject(playlistInfoBody);
                        for (int i = 0, l = playlistArray.size(); i < l; i++) {
                            JSONArray list = playlistArray.getJSONObject(i).getJSONArray("list");
                            for (int j = 0, k = list.size(); j < k; j++, t++) {
                                if (t >= (page - 1) * limit && t < page * limit) {
                                    JSONObject playlistJson = list.getJSONObject(j);

                                    String playlistId = playlistJson.getString("id");
                                    String playlistName = playlistJson.getString("name");
                                    String coverImgThumbUrl = playlistJson.getString("img");

                                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                                    playlistInfo.setSource(NetMusicSource.KW);
                                    playlistInfo.setId(playlistId);
                                    playlistInfo.setName(playlistName);
                                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                                    GlobalExecutors.imageExecutor.execute(() -> {
                                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                                        playlistInfo.setCoverImgThumb(coverImgThumb);
                                    });

                                    res.add(playlistInfo);
                                }
                            }
                        }
                    }
                } else {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API, sp[0], page, limit)).execute();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                        JSONObject data = playlistInfoJson.getJSONObject("data");
                        t = data.getInt("total");
                        JSONArray playlistArray = data.getJSONArray("data");
                        for (int i = 0, len = playlistArray.size(); i < len; i++) {
                            JSONObject playlistJson = playlistArray.getJSONObject(i);

                            String playlistId = playlistJson.getString("id");
                            String playlistName = playlistJson.getString("name");
                            String creator = playlistJson.getString("uname");
                            Long playCount = playlistJson.getLong("listencnt");
                            Integer trackCount = playlistJson.getInt("total");
                            String coverImgThumbUrl = playlistJson.getString("img");

                            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                            playlistInfo.setSource(NetMusicSource.KW);
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

                            res.add(playlistInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 推荐歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getRecHotPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(REC_HOT_PLAYLIST_MG_API, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data").getJSONArray("contentItemList").getJSONObject(0);
            t = 1000;
            JSONArray playlistArray = data.getJSONArray("itemList");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = ReUtil.get("id=(\\d+)", playlistJson.getString("actionUrl"), 1);
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("subTitle");
                String fs = playlistJson.getJSONArray("barList").getJSONObject(0).getString("title");
                Long playCount = StringUtil.parseNumber(fs);
                String coverImgThumbUrl = playlistJson.getString("imageUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(每页固定 10 条)
//        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsMg = () -> {
//            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
//                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
//            t = data.getInt("countSize");
//            JSONArray playlistArray = data.getJSONArray("playlist");
//            for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                String playlistId = playlistJson.getString("playListId");
//                String playlistName = playlistJson.getString("playListName");
//                String creator = playlistJson.getString("createName");
//                Long playCount = playlistJson.getLong("playCount");
//                Integer trackCount = playlistJson.getInt("contentCount");
//                String coverImgThumbUrl = playlistJson.getString("image");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.MG);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(playlistName);
//                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
//                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    playlistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(playlistInfo);
//            }
//            return new CommonResult<>(res, t);
//        };
        // 分类歌单(每页 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[5])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_MG_API, s[5], page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data").getJSONObject("contentItemList");
                t = 300;
                JSONArray playlistArray = data.getJSONArray("itemList");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getJSONObject("logEvent").getString("contentId");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("subTitle");
                    String fs = playlistJson.getString("playNum");
                    Long playCount = StringUtil.parseNumber(fs);
                    String coverImgThumbUrl = playlistJson.getString("imageUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQi = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[6])) {
                String playlistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(CAT_PLAYLIST_QI_API, page, limit, s[6].trim(), System.currentTimeMillis())))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("result");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("pic");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QI);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[7])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_ME_API, s[7].trim(), page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getJSONObject("pagination").getInt("count");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("albums");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Integer trackCount = playlistJson.getInt("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 探索歌单
        Callable<CommonResult<NetPlaylistInfo>> getExpPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[8])) {
                String playlistInfoBody = HttpRequest.get(String.format(EXP_PLAYLIST_ME_API, s[8].trim(), page, limit))
                        .execute()
                        .body();
                JSONArray playlistArray = JSONArray.fromObject(playlistInfoBody);
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Long playCount = playlistJson.getLong("view_count");
                    Integer trackCount = playlistJson.getInt("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getHighQualityPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotPickedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewPickedPlaylists));
        }

        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getTagPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotCollectedTagPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getUpTagPlaylistsKg));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotPlaylistsKg));
        }

        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsQq));
        }

        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotPlaylistsKw));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getDefaultPlaylistsKw));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsKw));
        }

        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecHotPlaylistsMg));
//            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsMg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsMg));
        }

        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsQi));
        }

        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getExpPlaylistsMe));
        }

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
        playlistInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(playlistInfos, total.get());
    }
}
