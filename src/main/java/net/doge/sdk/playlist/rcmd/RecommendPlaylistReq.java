package net.doge.sdk.playlist.rcmd;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.sdk.common.Tags;
import net.doge.model.entity.NetPlaylistInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.ListUtil;
import net.doge.util.StringUtil;
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

public class RecommendPlaylistReq {
    // 推荐歌单 API
    private final String RECOMMEND_PLAYLIST_API = SdkCommon.prefix + "/personalized?limit=100";
    // 发现歌单 API
    private final String DISCOVER_PLAYLIST_API = "https://music.163.com/discover/playlist/?order=hot&offset=%s&limit=%s";
    // 曲风歌单 API
    private final String STYLE_PLAYLIST_API = SdkCommon.prefix + "/style/playlist?tagId=%s&cursor=%s&size=%s";
    // 推荐歌单 API (每页固定 30 条)(酷狗)
    private final String RECOMMEND_PLAYLIST_KG_API
            = "http://m.kugou.com/plist/index?json=true&page=%s";
    // 推荐分类歌单(推荐) API (酷狗)
    private final String RECOMMEND_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=5&c=%s&p=%s";
    // 推荐分类歌单(最新) API (酷狗)
    private final String NEW_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=7&c=%s&p=%s";
    // 每日推荐歌单 API (QQ)
    private final String DAILY_RECOMMEND_PLAYLIST_QQ_API
            = SdkCommon.prefixQQ33 + "/recommend/playlist/u";
    // 推荐歌单 API (QQ)
//    private final String RECOMMEND_PLAYLIST_QQ_API
//            = SdkCommon.prefixQQ33 + "/recommend/playlist?id=%s&pageNo=1&pageSize=120";
    // 推荐歌单(最新) API (QQ)
    private final String NEW_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";
    private final String RECOMMEND_PLAYLIST_KW_API = "https://kuwo.cn/api/www/rcm/index/playlist?loginUid=0&httpsStatus=1";
    // 推荐歌单(最新) API (酷我)
    private final String NEW_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?pn=%s&rn=%s&order=new";
    // 推荐歌单 API(最新) (咪咕)
    private final String REC_NEW_PLAYLIST_MG_API
            = "https://app.c.nf.migu.cn/MIGUM2.0/v2.0/content/getMusicData.do?start=%s&count=%s&templateVersion=5&type=2";
    // 推荐歌单(最热) API (咪咕)
    private final String RECOMMEND_PLAYLIST_MG_API
            = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127315&startIndex=%s";
    // 最新歌单 API (咪咕)
    private final String NEW_PLAYLIST_MG_API
            = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127272&startIndex=%s";
    // 推荐歌单 API (千千)
    private final String REC_PLAYLIST_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 推荐歌单 API (猫耳)
    private final String REC_PLAYLIST_ME_API = "https://www.missevan.com/site/homepage";
    // 分类歌单(最新) API (猫耳)
    private final String NEW_PLAYLIST_ME_API = "https://www.missevan.com/explore/tagalbum?order=1&tid=%s&p=%s&pagesize=%s";
    // 分类歌单(最新) API (5sing)
    private final String NEW_PLAYLIST_FS_API = "http://5sing.kugou.com/gd/gdList?tagName=%s&page=%s&type=1";
    // 推荐歌单 API (哔哩哔哩)
    private final String NEW_PLAYLIST_BI_API = "https://www.bilibili.com/audio/music-service-c/web/menu/hit?pn=%s&ps=%s";

    /**
     * 获取推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.recPlaylistTag.get(tag);

        // 网易云(程序分页)
        // 发现歌单
        Callable<CommonResult<NetPlaylistInfo>> getDiscoverPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(35, limit);
            String playlistInfoBody = HttpRequest.get(String.format(DISCOVER_PLAYLIST_API, (page - 1) * lim, lim))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(playlistInfoBody);
            Elements playlists = doc.select("ul.m-cvrlst.f-cb li");
            Elements a = doc.select(".u-page a");
            t = Integer.parseInt(a.get(a.size() - 2).text()) * limit;
            for (int i = 0, len = playlists.size(); i < len; i++) {
                Element playlist = playlists.get(i);
                Element pa = playlist.select("p.dec a.tit.f-thide.s-fc0").first();
                Element nb = playlist.select(".bottom span.nb").first();
                Element fc = playlist.select("a.nm.nm-icn.f-thide.s-fc3").first();
                Element img = playlist.select(".u-cover.u-cover-1 img").first();

                String playlistId = ReUtil.get("id=(\\d+)", pa.attr("href"), 1);
                String playlistName = pa.text();
                String creator = fc.text();
                String creatorId = ReUtil.get("id=(\\d+)", fc.attr("href"), 1);
                Long playCount = StringUtil.parseNumber(nb.text());
                String coverImgThumbUrl = img.attr("src");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
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
        // 个人推荐
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(RECOMMEND_PLAYLIST_API)
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("result");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("picUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
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
        // 曲风歌单
        Callable<CommonResult<NetPlaylistInfo>> getStylePlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[0])) {
                String playlistInfoBody = HttpRequest.get(String.format(STYLE_PLAYLIST_API, s[0], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("playlist");
                t = data.getJSONObject("page").getInt("total");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("userName");
                    String creatorId = playlistJson.getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("songCount");
                    String coverImgThumbUrl = playlistJson.getString("cover");

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
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        // 每页固定 30 条的推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_KG_API, page))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("plist").getJSONObject("list");
            t = data.getInt("total");
            JSONArray playlistArray = data.getJSONArray("info");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("username");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.optInt("songcount", -1);
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
        // 推荐歌单(推荐)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_CAT_PLAYLIST_KG_API, s[1].trim(), page))
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
        // 推荐歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getNewTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_CAT_PLAYLIST_KG_API, s[1].trim(), page))
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

        // QQ
        // 每日推荐(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsQqDaily = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(DAILY_RECOMMEND_PLAYLIST_QQ_API)
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("content_id");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("username");
                Long playCount = playlistJson.getLong("listen_num");
                String coverImgThumbUrl = playlistJson.getString("cover");

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
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(程序分页)
//        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsQq = () -> {
//            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            if (StringUtils.isNotEmpty(s[0])) {
//                String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_QQ_API, s[0]))
//                        .execute()
//                        .body();
//                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
//                t = playlistArray.size();
//                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("tid");
//                    String playlistName = playlistJson.getString("title");
//                    String creator = playlistJson.getJSONObject("creator_info").getString("nick");
//                    Long playCount = playlistJson.getLong("access_num");
//                    Integer trackCount = playlistJson.getJSONArray("song_ids").size();
//                    String coverImgThumbUrl = playlistJson.getString("cover_url_big");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.QQ);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(res, t);
//        };
        // 分类推荐歌单(最新)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[2])) {
                String cat = s[2];
                boolean isAll = "10000000".equals(cat);
                String url;
                if (isAll) {
                    url = NEW_PLAYLIST_QQ_API + StringUtil.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_playlist_by_tag\"," +
                                    "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":2,\"cur_page\":%s}," +
                                    "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
                } else {
                    url = NEW_PLAYLIST_QQ_API + StringUtil.encode(String.format(
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
        // 推荐歌单(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(RECOMMEND_PLAYLIST_KW_API).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
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
        // 推荐歌单(最新)(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(NEW_PLAYLIST_KW_API, page, limit)).execute();
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

        // 咪咕(接口分页)
        // 推荐歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getRecNewPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(REC_NEW_PLAYLIST_MG_API, page, limit))
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
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
            t = data.getInt("countSize");
            JSONArray playlistArray = data.getJSONArray("playlist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playListId");
                String playlistName = playlistJson.getString("playListName");
                String creator = playlistJson.getString("createName");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("contentCount");
                String coverImgThumbUrl = playlistJson.getString("image");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
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
        // 最新歌单(每页固定 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_MG_API, (page - 1) * 10))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
            t = data.getInt("countSize");
            JSONArray playlistArray = data.getJSONArray("playlist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playListId");
                String playlistName = playlistJson.getString("playListName");
                String creator = playlistJson.getString("createName");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("contentCount");
                String coverImgThumbUrl = playlistJson.getString("image");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
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

        // 千千
        // 推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecPlaylistsQi = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(REC_PLAYLIST_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONArray("data").getJSONObject(4);
            t = data.getInt("module_nums");
            JSONArray playlistArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
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
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(REC_PLAYLIST_ME_API))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject info = playlistInfoJson.getJSONObject("info");
            JSONArray playlistArray = info.getJSONArray("albums");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("username");
                String creatorId = playlistJson.getString("user_id");
                Integer trackCount = playlistJson.getInt("music_count");
                Long playCount = playlistJson.getLong("view_count");
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
            return new CommonResult<>(res, t);
        };
        // 分类歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[3])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_ME_API, s[3].trim(), page, limit))
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

        // 5sing
        // 分类歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsFs = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.isNotEmpty(s[4])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_FS_API, s[4].trim(), page))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(playlistInfoBody);
                Elements as = doc.select("span.pagecon a");
                if (!as.isEmpty()) {
                    t = Integer.parseInt(as.last().text()) * limit;
                } else t = limit;
                Elements playlistArray = doc.select("li.item dl");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    Element elem = playlistArray.get(i);
                    Elements a = elem.select(".jx_name.ellipsis a");
                    Elements author = elem.select(".author a");
                    Elements img = elem.select(".imgbox img");
                    Elements lc = elem.select(".lcount");

                    String playlistId = ReUtil.get("dj/(.*?)\\.html", a.attr("href"), 1);
                    String playlistName = a.text();
                    String creator = author.text();
                    String creatorId = ReUtil.get("/(\\d+)/dj", a.attr("href"), 1);
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

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 哔哩哔哩
        // 推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecPlaylistsBi = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_BI_API, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getInt("totalSize");
            JSONArray playlistArray = data.getJSONArray("data");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);
                JSONObject statistic = playlistJson.getJSONObject("statistic");

                String playlistId = statistic.getString("sid");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("uname");
                String creatorId = playlistJson.getString("uid");
                Integer trackCount = playlistJson.getInt("snum");
                Long playCount = statistic.getLong("play");
                String coverImgThumbUrl = playlistJson.getString("cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.BI);
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
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getDiscoverPlaylists));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylists));
            if (!dt) taskList.add(GlobalExecutors.requestExecutor.submit(getStylePlaylists));
        }
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendTagPlaylistsKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewTagPlaylistsKg));
        }
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsQqDaily));
//        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsQq));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsQq));
        }
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsKw));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsKw));
        }
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecNewPlaylistsMg));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsMg));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsMg));
        }
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecPlaylistsQi));
        }
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecPlaylistsMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsMe));
        }
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsFs));
        }
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecPlaylistsBi));
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
