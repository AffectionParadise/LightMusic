package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KgReqBuilder;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.text.LangUtil;

import java.awt.image.BufferedImage;
import java.util.*;

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
    private final String CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial";
    // 推荐分类歌单(热藏) API (酷狗)
    private final String HOT_COLLECTED_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial";
    // 推荐分类歌单(飙升) API (酷狗)
    private final String UP_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial";
    // 热门歌单 API (酷狗)
    private final String HOT_PLAYLIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/special/recommend";
    // 编辑精选歌单 API (酷狗)
//    private final String IP_PLAYLIST_KG_API = "/ocean/v6/pubsongs/list_info_for_ip";

    /**
     * Top 歌单
     */
    public CommonResult<NetPlaylistInfo> getTopPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        String param = s[TagModule.TOP_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            String cid = param.trim();
            Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(TOP_PLAYLIST_KG_API);
            String ct = String.valueOf(System.currentTimeMillis() / 1000);
            String dat = String.format("{\"appid\":%s,\"mid\":\"%s\",\"clientver\":%s," +
                            "\"platform\":\"android\",\"clienttime\":\"%s\",\"userid\":%s,\"module_id\":4,\"page\":1,\"pagesize\":30," +
                            "\"key\":\"%s\",\"special_recommend\":{\"withtag\":1,\"withsong\":1,\"sort\":1,\"ugc\":1," +
                            "\"is_selected\":0,\"withrecommend\":1,\"area_code\":1,\"categoryid\":\"%s\"}}",
                    KgReqBuilder.appid, KgReqBuilder.mid, KgReqBuilder.clientver, ct, KgReqBuilder.userid, KgReqBuilder.signParamsKey(ct), StringUtil.isEmpty(cid) ? "0" : cid);
            String playlistInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("special_list");
            if (JsonUtil.notEmpty(playlistArray)) {
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("global_collection_id");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("play_count");
                    String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetResourceSource.KG);
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
    }

    /**
     * 推荐歌单(最热)
     */
    public CommonResult<NetPlaylistInfo> getTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        String param = s[TagModule.TOP_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("is_ajax", 1);
            params.put("cdn", "cdn");
            params.put("t", 6);
            params.put("c", param.trim());
            params.put("p", page);
            String playlistInfoBody = HttpRequest.get(CAT_PLAYLIST_KG_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = limit * 100;
            JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("global_collection_id");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.KG);
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

        String param = s[TagModule.TOP_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("is_ajax", 1);
            params.put("cdn", "cdn");
            params.put("t", 3);
            params.put("c", param.trim());
            params.put("p", page);
            String playlistInfoBody = HttpRequest.get(HOT_COLLECTED_CAT_PLAYLIST_KG_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = limit * 100;
            JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("global_collection_id");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.KG);
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

        String param = s[TagModule.TOP_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("is_ajax", 1);
            params.put("cdn", "cdn");
            params.put("t", 8);
            params.put("c", param.trim());
            params.put("p", page);
            String playlistInfoBody = HttpRequest.get(UP_CAT_PLAYLIST_KG_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            t = limit * 100;
            JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("global_collection_id");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = LangUtil.parseNumber(playlistJson.getString("total_play_count"));
                String coverImgThumbUrl = playlistJson.getString("img");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.KG);
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

        Map<String, Object> params = new TreeMap<>();
        params.put("recommend_expire", 0);
        params.put("sign", "52186982747e1404d426fa3f2a1e8ee4");
        params.put("plat", 0);
        params.put("uid", 0);
        params.put("version", 9108);
        params.put("page", 1);
        params.put("area_code", 1);
        params.put("appid", 1005);
        params.put("mid", "286974383886022203545511837994020015101");
        params.put("_t", "1545746286");
        String playlistInfoBody = HttpRequest.get(HOT_PLAYLIST_KG_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        JSONArray playlistArray = data.getJSONArray("list");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("global_specialid");
            String playlistName = playlistJson.getString("specialname");
            String creator = playlistJson.getString("nickname");
            Long playCount = playlistJson.getLong("playcount");
            Integer trackCount = playlistJson.getIntValue("songcount");
            String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.KG);
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

//    String param = s[TagType.IP_PLAYLIST_KG];
//        if (StringUtil.notEmpty(param)) {
//                Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_PLAYLIST_KG_API);
//                Map<String, Object> params = new TreeMap<>();
//                params.put("ip", param);
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
