package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

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
import net.doge.util.core.text.LangUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgRecommendPlaylistReq {
    private static KgRecommendPlaylistReq instance;

    private KgRecommendPlaylistReq() {
    }

    public static KgRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new KgRecommendPlaylistReq();
        return instance;
    }

    // 推荐歌单 API (每页固定 30 条)(酷狗)
    private final String RECOMMEND_PLAYLIST_KG_API = "http://m.kugou.com/plist/index";
    // 推荐分类歌单(推荐) API (酷狗)
    private final String RECOMMEND_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial";
    // 推荐分类歌单(最新) API (酷狗)
    private final String NEW_CAT_PLAYLIST_KG_API = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial";
    // Top 分类歌单 API (酷狗)
    private final String TOP_PLAYLIST_KG_API = "/specialrec.service/special_recommend";
    // 音效歌单 API (酷狗)
    private final String SOUND_EFFECT_PLAYLIST_KG_API = "/pubsongs/v1/get_sound_effect_list";

    /**
     * 每页固定 30 条的推荐歌单
     */
    public CommonResult<NetPlaylistInfo> getRecommendPlaylists(int page) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        Map<String, Object> params = new HashMap<>();
        params.put("json", true);
        params.put("page", page);
        String playlistInfoBody = HttpRequest.get(RECOMMEND_PLAYLIST_KG_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("plist").getJSONObject("list");
        t = data.getIntValue("total");
        JSONArray playlistArray = data.getJSONArray("info");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("global_specialid");
            String playlistName = playlistJson.getString("specialname");
            String creator = playlistJson.getString("username");
            Long playCount = playlistJson.getLong("playcount");
            Integer trackCount = playlistJson.getIntValue("songcount", -1);
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

    /**
     * 推荐歌单(推荐)
     */
    public CommonResult<NetPlaylistInfo> getRecommendTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        String param = s[TagModule.RECOMMEND_CAT_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("is_ajax", 1);
            params.put("cdn", "cdn");
            params.put("t", 5);
            params.put("c", param.trim());
            params.put("p", page);
            String playlistInfoBody = HttpRequest.get(RECOMMEND_CAT_PLAYLIST_KG_API)
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
     * 推荐歌单(最新)
     */
    public CommonResult<NetPlaylistInfo> getNewTagPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        String param = s[TagModule.RECOMMEND_CAT_PLAYLIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("is_ajax", 1);
            params.put("cdn", "cdn");
            params.put("t", 7);
            params.put("c", param.trim());
            params.put("p", page);
            String playlistInfoBody = HttpRequest.get(NEW_CAT_PLAYLIST_KG_API)
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
     * Top 歌单
     */
    public CommonResult<NetPlaylistInfo> getTopPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(TOP_PLAYLIST_KG_API);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        String dat = String.format("{\"appid\":%s,\"mid\":\"%s\",\"clientver\":%s," +
                        "\"platform\":\"android\",\"clienttime\":\"%s\",\"userid\":%s,\"module_id\":4,\"page\":1,\"pagesize\":30," +
                        "\"key\":\"%s\",\"special_recommend\":{\"withtag\":1,\"withsong\":1,\"sort\":1,\"ugc\":1," +
                        "\"is_selected\":0,\"withrecommend\":1,\"area_code\":1,\"categoryid\":\"0\"}}",
                KgReqBuilder.appid, KgReqBuilder.mid, KgReqBuilder.clientver, ct, KgReqBuilder.userid, KgReqBuilder.signParamsKey(ct));
        String playlistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        JSONArray playlistArray = data.getJSONArray("special_list");
        t = playlistArray.size();
        for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("global_collection_id");
            String playlistName = playlistJson.getString("specialname");
            String creator = playlistJson.getString("nickname");
            Long playCount = LangUtil.parseNumber(playlistJson.getString("play_count"));
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
        return new CommonResult<>(r, t);
    }

    /**
     * 音效歌单
     */
    public CommonResult<NetPlaylistInfo> getSoundEffectPlaylists(int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t;

        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(SOUND_EFFECT_PLAYLIST_KG_API);
        String dat = String.format("{\"page\":%s,\"pagesize\":%s}", page, limit);
        String playlistInfoBody = SdkCommon.kgRequest(null, dat, options)
                .executeAsStr();
        JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
        JSONObject data = playlistInfoJson.getJSONObject("data");
        JSONArray playlistArray = data.getJSONArray("info");
        t = data.getIntValue("total");
        for (int i = 0, len = playlistArray.size(); i < len; i++) {
            JSONObject playlistJson = playlistArray.getJSONObject(i);

            String playlistId = playlistJson.getString("global_collection_id");
            String playlistName = playlistJson.getString("name");
            String creator = playlistJson.getString("list_create_username");
            String creatorId = playlistJson.getString("list_create_userid");
            Long playCount = playlistJson.getLong("heat");
            Integer trackCount = playlistJson.getIntValue("count");
            String coverImgThumbUrl = playlistJson.getString("pic").replace("/{size}", "");

            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
            playlistInfo.setSource(NetResourceSource.KG);
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

            r.add(playlistInfo);
        }
        return new CommonResult<>(r, t);
    }
}
