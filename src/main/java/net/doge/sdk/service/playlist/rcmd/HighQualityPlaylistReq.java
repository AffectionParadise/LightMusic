package net.doge.sdk.service.playlist.rcmd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.*;
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

public class HighQualityPlaylistReq {
    private static HighQualityPlaylistReq instance;

    private HighQualityPlaylistReq() {
    }

    public static HighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new HighQualityPlaylistReq();
        return instance;
    }

    // 精品歌单 API
    private final String HIGH_QUALITY_PLAYLIST_API = "https://music.163.com/api/playlist/highquality/list";
    // 网友精选碟(最热/最新) API
    private final String PICKED_PLAYLIST_API = "https://music.163.com/weapi/playlist/list";
    // Top 分类歌单 API (酷狗)
    private final String TOP_PLAYLIST_KG_API = "/specialrec.service/special_recommend";
    // 推荐分类歌单(最热) API (酷狗)
    private final String CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=6&c=%s&p=%s";
    // 推荐分类歌单(热藏) API (酷狗)
    private final String HOT_COLLECTED_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=3&c=%s&p=%s";
    // 推荐分类歌单(飙升) API (酷狗)
    private final String UP_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=8&c=%s&p=%s";
    // 热门歌单 API (酷狗)
    private final String HOT_PLAYLIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/special/recommend?recommend_expire=0&sign=52186982747e1404d426fa3f2a1e8ee4" +
            "&plat=0&uid=0&version=9108&page=1&area_code=1&appid=1005&mid=286974383886022203545511837994020015101&_t=1545746286";
    // 编辑精选歌单 API (酷狗)
    private final String IP_PLAYLIST_KG_API = "/ocean/v6/pubsongs/list_info_for_ip";
    // 分类歌单 API (QQ)
    private final String CAT_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";
    // 热门歌单 API (酷我)
    private final String HOT_PLAYLIST_KW_API = "https://kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot&httpsStatus=1";
    // 默认歌单(热门) API (酷我)
    private final String DEFAULT_PLAYLIST_KW_API = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=hot";
    // 分类歌单 API (酷我)
    private final String CAT_PLAYLIST_KW_API = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?loginUid=0&loginSid=0&appUid=76039576&id=%s&pn=%s&rn=%s";
    // 分类歌单 API 2 (酷我)
    private final String CAT_PLAYLIST_KW_API_2 = "http://mobileinterfaces.kuwo.cn/er.s?type=get_pc_qz_data&f=web&id=%s&prod=pc";
    // 推荐歌单 API(最热) (咪咕)
    private final String REC_HOT_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=1";
    // 分类歌单 API (咪咕)
    private final String CAT_PLAYLIST_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-listbytag?tagId=%s&pageNumber=%s&templateVersion=1";
    // 分类歌单 API (千千)
    private final String CAT_PLAYLIST_QI_API = "https://music.91q.com/v1/tracklist/list?appid=16073360&pageNo=%s&pageSize=%s&subCateId=%s&timestamp=%s";
    // 分类歌单 API (猫耳)
    private final String CAT_PLAYLIST_ME_API = "https://www.missevan.com/explore/tagalbum?order=0&tid=%s&p=%s&pagesize=%s";
    // 探索歌单 API (猫耳)
    private final String EXP_PLAYLIST_ME_API = "https://www.missevan.com/explore/getAlbumFromTag/%s";
    // 分类歌单(最热) API (5sing)
    private final String HOT_PLAYLIST_FS_API = "http://5sing.kugou.com/gd/gdList?tagName=%s&page=%s&type=0";

    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public CommonResult<NetPlaylistInfo> getHighQualityPlaylists(int src, String tag, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> res = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.hotPlaylistTag.get(tag);

        // 网易云
        // 精品歌单(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getHighQualityPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String playlistInfoBody = SdkCommon.ncRequest(Method.POST, HIGH_QUALITY_PLAYLIST_API,
                                String.format("{\"cat\":\"%s\",\"lasttime\":0,\"limit\":%s,\"total\":true}", s[0], limit), options)
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.getJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                    String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getIntValue("trackCount");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 网友精选碟(最热)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getHotPickedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_API,
                                String.format("{\"cat\":\"%s\",\"order\":\"hot\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[1], (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = playlistInfoJson.getIntValue("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.getJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                    String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getIntValue("trackCount");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 网友精选碟(最新)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPickedPlaylists = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
                String playlistInfoBody = SdkCommon.ncRequest(Method.POST, PICKED_PLAYLIST_API,
                                String.format("{\"cat\":\"%s\",\"order\":\"new\",\"offset\":%s,\"limit\":%s,\"total\":true}", s[1], (page - 1) * limit, limit), options)
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = playlistInfoJson.getIntValue("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);
                    JSONObject ct = playlistJson.getJSONObject("creator");

                    String playlistId = playlistJson.getString("id");
                    String name = playlistJson.getString("name");
                    String creator = JsonUtil.notEmpty(ct) ? ct.getString("nickname") : "";
                    String creatorId = JsonUtil.notEmpty(ct) ? ct.getString("userId") : "";
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getIntValue("trackCount");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷狗
        // Top 歌单
        Callable<CommonResult<NetPlaylistInfo>> getTopPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String cid = s[2].trim();
                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(TOP_PLAYLIST_KG_API);
                String ct = String.valueOf(System.currentTimeMillis() / 1000);
                String dat = String.format("{\"appid\":%s,\"mid\":\"%s\",\"clientver\":%s," +
                                "\"platform\":\"android\",\"clienttime\":\"%s\",\"userid\":%s,\"module_id\":4,\"page\":1,\"pagesize\":30," +
                                "\"key\":\"%s\",\"special_recommend\":{\"withtag\":1,\"withsong\":1,\"sort\":1,\"ugc\":1," +
                                "\"is_selected\":0,\"withrecommend\":1,\"area_code\":1,\"categoryid\":\"%s\"}}",
                        KugouReqBuilder.appid, KugouReqBuilder.mid, KugouReqBuilder.clientver, ct, KugouReqBuilder.userid, KugouReqBuilder.signParamsKey(ct), StringUtil.isEmpty(cid) ? "0" : cid);
                String playlistInfoBody = SdkCommon.kgRequest(null, dat, options)
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("special_list");
                if (JsonUtil.notEmpty(playlistArray)) {
                    t = playlistArray.size();
                    for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("specialid");
                        String playlistName = playlistJson.getString("specialname");
                        String creator = playlistJson.getString("nickname");
                        Long playCount = playlistJson.getLong("play_count");
                        Integer trackCount = playlistJson.getIntValue("songcount");
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

                        r.add(playlistInfo);
                    }
                }
            }
            return new CommonResult<>(r, t);
        };
        // 推荐歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getTagPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 推荐歌单(热藏)
        Callable<CommonResult<NetPlaylistInfo>> getHotCollectedTagPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_COLLECTED_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 推荐歌单(飙升)
        Callable<CommonResult<NetPlaylistInfo>> getUpTagPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(UP_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = limit * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 热门歌单(这个接口不分页，分开处理)
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsKg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(HOT_PLAYLIST_KG_API)
                    .executeAsync()
                    .body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.getIntValue("songcount");
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

                r.add(playlistInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 编辑精选歌单
        // 该歌单数据的 id 与 specialid 不同，暂不考虑
//        Callable<CommonResult<NetPlaylistInfo>> getIpPlaylistsKg = () -> {
//            List<NetPlaylistInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            if (StringUtil.notEmpty(s[3])) {
//                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_PLAYLIST_KG_API);
//                Map<String, Object> params = new TreeMap<>();
//                params.put("ip", s[3]);
//                params.put("page", page);
//                params.put("pagesize", limit);
//                String playlistInfoBody = SdkCommon.kgRequest(params, null, options)
//                        .executeAsync()
//                        .body();
//                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//                JSONObject data = playlistInfoJson.getJSONObject("data");
//                JSONArray playlistArray = data.getJSONArray("info");
//                t = page * limit + 1;
//                for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("list_create_listid");
//                    String playlistName = playlistJson.getString("name");
//                    String creator = playlistJson.getString("list_create_username");
//                    String creatorId = playlistJson.getString("list_create_userid");
//                    Long playCount = playlistJson.getLong("heat");
//                    Integer trackCount = playlistJson.getIntValue("count");
//                    String coverImgThumbUrl = playlistJson.getString("pic").replace("/{size}", "");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.KG);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCreatorId(creatorId);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    r.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
//        };

        // QQ
        // 分类推荐歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQq = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                String cat = s[4];
                boolean isAll = "10000000".equals(cat);
                String url;
                if (isAll) {
                    url = CAT_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_playlist_by_tag\"," +
                                    "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":5,\"cur_page\":%s}," +
                                    "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
                } else {
                    url = CAT_PLAYLIST_QQ_API + UrlUtil.encodeAll(String.format(
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
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                if (isAll) {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data");
                    t = data.getIntValue("total");
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

                        r.add(playlistInfo);
                    }
                } else {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data").getJSONObject("content");
                    t = data.getIntValue("total_cnt");
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

                        r.add(playlistInfo);
                    }
                }
            }
            return new CommonResult<>(r, t);
        };

        // 酷我
        // 热门歌单
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsKw = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(HOT_PLAYLIST_KW_API, page, limit))
                    .setFollowRedirects(true)
                    .executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getIntValue("total");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 默认歌单(热门)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getDefaultPlaylistsKw = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(DEFAULT_PLAYLIST_KW_API, page, limit)).executeAsync();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getIntValue("total");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 分类歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsKw = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                String[] sp = s[5].split(" ");
                // 根据 digest 信息请求不同的分类歌单接口
                if ("43".equals(sp[1])) {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API_2, sp[0])).executeAsync();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONArray playlistArray = JSONArray.parseArray(playlistInfoBody);
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

                                    r.add(playlistInfo);
                                }
                            }
                        }
                    }
                } else {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API, sp[0], page, limit)).executeAsync();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                        JSONObject data = playlistInfoJson.getJSONObject("data");
                        t = data.getIntValue("total");
                        JSONArray playlistArray = data.getJSONArray("data");
                        for (int i = 0, len = playlistArray.size(); i < len; i++) {
                            JSONObject playlistJson = playlistArray.getJSONObject(i);

                            String playlistId = playlistJson.getString("id");
                            String playlistName = playlistJson.getString("name");
                            String creator = playlistJson.getString("uname");
                            Long playCount = playlistJson.getLong("listencnt");
                            Integer trackCount = playlistJson.getIntValue("total");
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

                            r.add(playlistInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(r, t);
        };

        // 咪咕
        // 推荐歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getRecHotPlaylistsMg = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(REC_HOT_PLAYLIST_MG_API, page, limit))
                    .executeAsync()
                    .body();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data").getJSONArray("contentItemList").getJSONObject(0);
            t = 1000;
            JSONArray playlistArray = data.getJSONArray("itemList");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = RegexUtil.getGroup1("id=(\\d+)", playlistJson.getString("actionUrl"));
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("subTitle");
                String fs = playlistJson.getJSONArray("barList").getJSONObject(0).getString("title");
                Long playCount = LangUtil.parseNumber(fs);
                String coverImgThumbUrl = playlistJson.getString("imageUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
            return new CommonResult<>(r, t);
        };
        // 推荐歌单(每页固定 10 条)
//        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsMg = () -> {
//            List<NetPlaylistInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
//                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
//                    .header(Header.REFERER, "https://m.music.migu.cn/")
//                    .executeAsync()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
//            t = data.getIntValue("countSize");
//            JSONArray playlistArray = data.getJSONArray("playlist");
//            for (int i = 0, len = playlistArray.size(); i < len; i++) {
//                JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                String playlistId = playlistJson.getString("playListId");
//                String playlistName = playlistJson.getString("playListName");
//                String creator = playlistJson.getString("createName");
//                Long playCount = playlistJson.getLong("playCount");
//                Integer trackCount = playlistJson.getIntValue("contentCount");
//                String coverImgThumbUrl = playlistJson.getString("image");
//
//                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                playlistInfo.setSource(NetMusicSource.MG);
//                playlistInfo.setId(playlistId);
//                playlistInfo.setName(playlistName);
//                playlistInfo.setCreator(creator);
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
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_MG_API, s[6], page))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data").getJSONObject("contentItemList");
                t = 300;
                JSONArray playlistArray = data.getJSONArray("itemList");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getJSONObject("logEvent").getString("contentId");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("subTitle");
                    String fs = playlistJson.getString("playNum");
                    Long playCount = LangUtil.parseNumber(fs);
                    String coverImgThumbUrl = playlistJson.getString("imageUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 千千
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQi = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                String playlistInfoBody = SdkCommon.qiRequest(String.format(CAT_PLAYLIST_QI_API, page, limit, s[7].trim(), System.currentTimeMillis()))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray playlistArray = data.getJSONArray("result");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    Integer trackCount = playlistJson.getIntValue("trackCount");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsMe = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[8])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_ME_API, s[8].trim(), page, limit))
                        .executeAsync()
                        .body();
                JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
                t = playlistInfoJson.getJSONObject("pagination").getIntValue("count");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("albums");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Integer trackCount = playlistJson.getIntValue("music_count");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };
        // 探索歌单
        Callable<CommonResult<NetPlaylistInfo>> getExpPlaylistsMe = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[9])) {
                String playlistInfoBody = HttpRequest.get(String.format(EXP_PLAYLIST_ME_API, s[9].trim()))
                        .executeAsync()
                        .body();
                JSONArray playlistArray = JSONArray.parseArray(playlistInfoBody);
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Long playCount = playlistJson.getLong("view_count");
                    Integer trackCount = playlistJson.getIntValue("music_count");
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

                    r.add(playlistInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 5sing
        // 分类歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsFs = () -> {
            List<NetPlaylistInfo> r = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[10])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_PLAYLIST_FS_API, s[10].trim(), page))
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(playlistInfoBody);
                Elements as = doc.select("span.pagecon a");
                if (as.isEmpty()) t = limit;
                else t = Integer.parseInt(as.last().text()) * limit;
                Elements playlistArray = doc.select("li.item dl");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    Element elem = playlistArray.get(i);
                    Elements a = elem.select(".jx_name.ellipsis a");
                    Elements author = elem.select(".author a");
                    Elements img = elem.select(".imgbox img");
                    Elements lc = elem.select(".lcount");

                    String playlistId = RegexUtil.getGroup1("dj/(.*?)\\.html", a.attr("href"));
                    String playlistName = a.text();
                    String creator = author.text();
                    String creatorId = RegexUtil.getGroup1("/(\\d+)/dj", a.attr("href"));
                    Long playCount = Long.parseLong(lc.text());
                    String coverImgThumbUrl = img.attr("src");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.FS);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
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

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getHighQualityPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotPickedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewPickedPlaylists));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getTopPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getTagPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotCollectedTagPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getUpTagPlaylistsKg));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotPlaylistsKg));
//            taskList.add(GlobalExecutors.requestExecutor.submit(getIpPlaylistsKg));
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
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotPlaylistsFs));
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
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get());
    }
}
