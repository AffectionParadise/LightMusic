package net.doge.sdk.entity.mv.menu;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class MvMenuReq {
    // 相似 MV API
    private final String SIMILAR_MV_API = SdkCommon.PREFIX + "/simi/mv?mvid=%s";
    // 视频相关视频 API
    private final String RELATED_VIDEO_API = SdkCommon.PREFIX + "/related/allvideo?id=%s";
    // 歌曲相关视频 API
    private final String RELATED_MLOG_API = SdkCommon.PREFIX + "/mlog/music/rcmd?songid=%s&limit=500";
    // mlog id 转视频 id API
    private final String MLOG_TO_VIDEO_API = SdkCommon.PREFIX + "/mlog/to/video?id=%s";

    // 相似视频 API (好看)
    private final String SIMILAR_VIDEO_HK_API = "https://haokan.baidu.com/videoui/api/videorec?title=%s&vid=%s&act=pcRec&pd=pc";
    // 相似视频 API (哔哩哔哩)
    private final String SIMILAR_VIDEO_BI_API = "https://api.bilibili.com/x/web-interface/archive/related?bvid=%s";
    // 视频分集 API (哔哩哔哩)
    private final String VIDEO_EPISODES_BI_API = "https://api.bilibili.com/x/player/pagelist?bvid=%s";

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo netMusicInfo, int limit, int page) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云(程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String mvInfoBody = HttpRequest.get(String.format(RELATED_MLOG_API, id))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("feeds");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject resource = mvArray.getJSONObject(i).getJSONObject("resource");
                JSONObject mlogBaseData = resource.getJSONObject("mlogBaseData");

                String mvId = mlogBaseData.getString("id");
                String mvName = mlogBaseData.getString("originalTitle").trim();
                if (StringUtil.isEmpty(mvName)) mvName = mlogBaseData.getString("text");
                String artistName = resource.getJSONObject("userProfile").getString("nickname");
                String creatorId = resource.getJSONObject("userProfile").getString("userId");
                String coverImgUrl = mlogBaseData.getString("coverUrl");
                Long playCount = resource.getJSONObject("mlogExtVO").getLong("playCount");
                Double duration = mlogBaseData.getDouble("duration") / 1000;
                String pubTime = TimeUtil.msToDate(mlogBaseData.getLong("pubTime"));

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setType(MvInfoType.MLOG);
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
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            // 先根据 mid 获取 id
            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

            String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                            "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"video\":{\"module\":\"MvService.MvInfoProServer\"," +
                            "\"method\":\"GetSongRelatedMv\",\"param\":{\"songid\":%s,\"songtype\":1,\"lastmvid\":0,\"num\":10}}}", id))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONObject("video").getJSONObject("data").getJSONArray("list");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title").trim();
                String artistName = SdkUtil.parseArtist(mvJson, NetMusicSource.QQ);
                JSONArray singerArray = mvJson.getJSONArray("singers");
                String creatorId = JsonUtil.isEmpty(singerArray) ? "" : singerArray.getJSONObject(0).getString("mid");
                String coverImgUrl = mvJson.getString("picurl");
                Long playCount = mvJson.getLong("playcnt");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        String id = netMvInfo.getId();
        String bvid = netMvInfo.getBvid();
        String name = StringUtil.urlEncode(netMvInfo.getName());
        boolean isVideo = netMvInfo.isVideo();
        boolean isMlog = netMvInfo.isMlog();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 视频
            if (isVideo || isMlog) {
                // Mlog 需要先获取视频 id，并转为视频类型
                if (isMlog) {
                    String body = HttpRequest.get(String.format(MLOG_TO_VIDEO_API, id))
                            .execute()
                            .body();
                    id = JSONObject.parseObject(body).getString("data");
                    netMvInfo.setId(id);
                    netMvInfo.setType(MvInfoType.VIDEO);
                }

                String mvInfoBody = HttpRequest.get(String.format(RELATED_VIDEO_API, id))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title").trim();
                    String artistName = SdkUtil.parseCreator(mvJson);
                    String creatorId = mvJson.getJSONArray("creator").getJSONObject(0).getString("userId");
                    String coverImgUrl = mvJson.getString("coverUrl");
                    Long playCount = mvJson.getLong("playTime");
                    Double duration = mvJson.getDouble("durationms") / 1000;

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setType(MvInfoType.VIDEO);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            // MV
            else {
                String mvInfoBody = HttpRequest.get(String.format(SIMILAR_MV_API, id))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtist(mvJson, NetMusicSource.NET_CLOUD);
                    String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
                    String coverImgUrl = mvJson.getString("cover");
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getDouble("duration") / 1000;

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"ct\":24,\"cv\":4747474},\"mvinfo\":{\"module\":\"video.VideoDataServer\"," +
                            "\"method\":\"get_video_info_batch\",\"param\":{\"vidlist\":[\"%s\"],\"required\":[\"vid\",\"type\",\"sid\"," +
                            "\"cover_pic\",\"duration\",\"singers\",\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\"," +
                            "\"isfav\",\"gmid\"]}},\"other\":{\"module\":\"video.VideoLogicServer\",\"method\":\"rec_video_byvid\"," +
                            "\"param\":{\"vid\":\"%s\",\"required\":[\"vid\",\"type\",\"sid\",\"cover_pic\",\"duration\",\"singers\"," +
                            "\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\",\"isfav\",\"gmid\",\"uploader_headurl\"," +
                            "\"uploader_nick\",\"uploader_encuin\",\"uploader_uin\",\"uploader_hasfollow\",\"uploader_follower_num\"],\"support\":1}}}", id, id))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("other").getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("list");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("name");
                String artistName = mvJson.getString("uploader_nick");
                String creatorId = mvJson.getString("uploader_uin");
                String coverImgUrl = mvJson.getString("cover_pic");
                Long playCount = mvJson.getLong("playcnt");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
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
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String mvInfoBody = HttpRequest.get(String.format(SIMILAR_VIDEO_HK_API, name, id))
                    .header(Header.REFERER, String.format("https://haokan.baidu.com/v?vid=%s", id))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(mvInfoBody).getJSONObject("data").getJSONObject("response");
            JSONArray mvArray = data.getJSONArray("videos");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("source_name");
                String creatorId = mvJson.getString("mthid");
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
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String mvInfoBody = HttpRequest.get(String.format(SIMILAR_VIDEO_BI_API, bvid))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONArray mvArray = JSONObject.parseObject(mvInfoBody).getJSONArray("data");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("cid");
                String bvId = mvJson.getString("bvid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getJSONObject("owner").getString("name");
                String creatorId = mvJson.getJSONObject("owner").getString("mid");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getJSONObject("stat").getLong("view");
                Double duration = mvJson.getDouble("duration");
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
                mvInfo.setId(mvId);
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

        return new CommonResult<>(res, t);
    }

    /**
     * 获取视频分集
     *
     * @return
     */
    public CommonResult<NetMvInfo> getVideoEpisodes(NetMvInfo netMvInfo, int page, int limit) {
        int source = netMvInfo.getSource();
        String bvid = netMvInfo.getBvid();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        Integer t = 0;

        // 哔哩哔哩
        if (source == NetMusicSource.BI) {
            String mvInfoBody = HttpRequest.get(String.format(VIDEO_EPISODES_BI_API, bvid))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONArray mvArray = JSONObject.parseObject(mvInfoBody).getJSONArray("data");
            if (JsonUtil.notEmpty(mvArray)) {
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(page * limit, mvArray.size()); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("cid");
                    String mvName = mvJson.getString("part");
                    String artistName = netMvInfo.getArtist();
                    String creatorId = netMvInfo.getCreatorId();
                    String coverImgUrl = netMvInfo.getCoverImgUrl();
                    Long playCount = netMvInfo.getPlayCount();
                    Double duration = mvJson.getDouble("duration");
                    String pubTime = netMvInfo.getPubTime();

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.BI);
                    mvInfo.setId(mvId);
                    mvInfo.setBvid(bvid);
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

        return new CommonResult<>(res, t);
    }
}
