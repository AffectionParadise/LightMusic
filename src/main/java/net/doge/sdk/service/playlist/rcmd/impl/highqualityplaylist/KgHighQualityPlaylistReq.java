package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.LangUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgHighQualityPlaylistReq {
    private static KgHighQualityPlaylistReq instance;

    private KgHighQualityPlaylistReq() {
    }

    public static KgHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new KgHighQualityPlaylistReq();
        return instance;
    }

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
//    private final String IP_PLAYLIST_KG_API = "/ocean/v6/pubsongs/list_info_for_ip";

    /**
     * Top 歌单
     */
    public CommonResult<NetPlaylistInfo> getTopPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

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
                    .executeAsStr();
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
    }

    /**
     * 推荐歌单(最热)
     */
    public CommonResult<NetPlaylistInfo> getTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_KG_API, s[2].trim(), page))
                    .executeAsStr();
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
    }

    /**
     * 推荐歌单(热藏)
     */
    public CommonResult<NetPlaylistInfo> getHotCollectedTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            String playlistInfoBody = HttpRequest.get(String.format(HOT_COLLECTED_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                    .executeAsStr();
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
    }

    /**
     * 推荐歌单(飙升)
     */
    public CommonResult<NetPlaylistInfo> getUpTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[2])) {
            String playlistInfoBody = HttpRequest.get(String.format(UP_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                    .executeAsStr();
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
    }

    /**
     * 热门歌单(这个接口不分页，分开处理)
     */
    public CommonResult<NetPlaylistInfo> getHotPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        String playlistInfoBody = HttpRequest.get(HOT_PLAYLIST_KG_API)
                .executeAsStr();
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
    }

//    /**
//     * 编辑精选歌单(该歌单数据的 id 与 specialid 不同，暂不考虑)
//     */
//    public CommonResult<NetPlaylistInfo> getIpPlaylists(String tag, int page, int limit) {
//        List<NetPlaylistInfo> r = new LinkedList<>();
//        int t = 0;
//        String[] s = Tags.hotPlaylistTag.get(tag);

//        if (StringUtil.notEmpty(s[3])) {
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
//    }
}
