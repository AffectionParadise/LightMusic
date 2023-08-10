package net.doge.sdk.entity.mv.info;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.Format;
import net.doge.constant.system.VideoQuality;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.Map;

public class MvInfoReq {
    // MV 信息 API
    private final String MV_DETAIL_API = "https://music.163.com/api/v1/mv/detail";
    // MV 信息 API (酷狗)
    private final String MV_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/mv/detail?area_code=1&plat=0&mvhash=%s";
    // MV 信息 API (酷我)
    private final String MV_DETAIL_KW_API = "http://www.kuwo.cn/api/www/music/musicInfo?mid=%s&httpsStatus=1";

    /**
     * 根据 MV id 预加载 MV 信息
     */
    public void preloadMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.execute(() -> mvInfo.setCoverImgThumb(SdkUtil.extractMvCover(mvInfo.getCoverImgUrl())));
    }

    /**
     * 根据 MV id 补全 MV 信息(只包含 url)
     */
    public void fillMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated() && mvInfo.isQualityMatch()) return;

        String url = new MvUrlReq().fetchMvUrl(mvInfo);
        mvInfo.setUrl(url);

        if (url.contains(".mp4")) mvInfo.setFormat(Format.MP4);
        else if (url.contains(".flv")) mvInfo.setFormat(Format.FLV);

        // 更新画质
        mvInfo.setQuality(VideoQuality.quality);
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        int source = mvInfo.getSource();
        String mvId = mvInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String mvBody = SdkCommon.ncRequest(Method.POST, MV_DETAIL_API, String.format("{\"id\":\"%s\"}", mvId), options)
                    .executeAsync()
                    .body();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data");
            String name = data.getString("name").trim();
            String artist = SdkUtil.parseArtist(data);
            String creatorId = SdkUtil.parseArtistId(data);
            Long playCount = data.getLong("playCount");
            Double duration = data.getDouble("duration") / 1000;
            String pubTime = data.getString("publishTime");
            String coverImgUrl = data.getString("cover");

            mvInfo.setName(name);
            mvInfo.setArtist(artist);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String mvBody = HttpRequest.get(String.format(MV_DETAIL_KG_API, mvId))
                    .executeAsync()
                    .body();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data").getJSONObject("info");
            String[] s = data.getString("filename").split(" - ");
            String name = s[1];
            String artist = s[0];
            String creatorId = SdkUtil.parseArtistId(mvJson);
            Long playCount = data.getLong("history_heat");
            Double duration = data.getDouble("mv_timelength") / 1000;
            String pubTime = data.getString("update");
            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");

            mvInfo.setName(name);
            mvInfo.setArtist(artist);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"ct\":24,\"cv\":4747474},\"mvinfo\":{\"module\":\"video.VideoDataServer\"," +
                            "\"method\":\"get_video_info_batch\",\"param\":{\"vidlist\":[\"%s\"],\"required\":[\"vid\",\"type\",\"sid\"," +
                            "\"cover_pic\",\"duration\",\"singers\",\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\"," +
                            "\"isfav\",\"gmid\"]}},\"other\":{\"module\":\"video.VideoLogicServer\",\"method\":\"rec_video_byvid\"," +
                            "\"param\":{\"vid\":\"%s\",\"required\":[\"vid\",\"type\",\"sid\",\"cover_pic\",\"duration\",\"singers\"," +
                            "\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\",\"isfav\",\"gmid\",\"uploader_headurl\"," +
                            "\"uploader_nick\",\"uploader_encuin\",\"uploader_uin\",\"uploader_hasfollow\",\"uploader_follower_num\"],\"support\":1}}}", mvId, mvId))
                    .executeAsync()
                    .body();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONObject data = mvJson.getJSONObject("mvinfo").getJSONObject("data").getJSONObject(mvId);

            String name = data.getString("name");
            String artist = SdkUtil.parseArtist(data);
            String creatorId = SdkUtil.parseArtistId(data);
            Long playCount = data.getLong("playcnt");
            Double duration = data.getDouble("duration");
            String pubTime = TimeUtil.msToDate(data.getLong("pubdate") * 1000);
            String coverImgUrl = data.getString("cover_pic");

            mvInfo.setName(name);
            mvInfo.setArtist(artist);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String mvBody = SdkCommon.kwRequest(String.format(MV_DETAIL_KW_API, mvId))
                    .executeAsync()
                    .body();
            JSONObject mvJson = JSONObject.parseObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data");

            String name = data.getString("name");
            String artist = data.getString("artist").replace("&", "、");
            String creatorId = data.getString("artistid");
            Long playCount = data.getLong("mvPlayCnt");
            Double duration = data.getDouble("duration");
            String pubTime = data.getString("releaseDate");
            String coverImgUrl = data.getString("pic");

            mvInfo.setName(name);
            mvInfo.setArtist(artist);
            mvInfo.setCreatorId(creatorId);
            mvInfo.setPlayCount(playCount);
            mvInfo.setDuration(duration);
            mvInfo.setPubTime(pubTime);
            mvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });
        }
    }
}
