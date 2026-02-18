package net.doge.sdk.service.mv.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.MvInfoType;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NcMvMenuReq {
    private static NcMvMenuReq instance;

    private NcMvMenuReq() {
    }

    public static NcMvMenuReq getInstance() {
        if (instance == null) instance = new NcMvMenuReq();
        return instance;
    }

    // 相似 MV API (网易云)
    private final String SIMILAR_MV_NC_API = "https://music.163.com/weapi/discovery/simiMV";
    // 视频相关视频 API (网易云)
    private final String RELATED_VIDEO_NC_API = "https://music.163.com/weapi/cloudvideo/v1/allvideo/rcmd";
    // 歌曲相关视频 API (网易云)
    private final String RELATED_MLOG_NC_API = "https://interface.music.163.com/eapi/mlog/rcmd/feed/list";
    // mlog id 转视频 id API (网易云)
    private final String MLOG_TO_VIDEO_NC_API = "https://music.163.com/weapi/mlog/video/convert/id";

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo musicInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int t;

        String id = musicInfo.getId();
        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
        String mvInfoBody = SdkCommon.ncRequest(Method.POST, RELATED_MLOG_NC_API,
                        String.format("{\"id\":\"0\",\"type\":2,\"rcmdType\":20,\"limit\":500,\"extInfo\":\"{'songId':'%s'}\"}", id), options)
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("feeds");
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            JSONObject resource = mvArray.getJSONObject(i).getJSONObject("resource");
            JSONObject mlogBaseData = resource.getJSONObject("mlogBaseData");

            String mvId = mlogBaseData.getString("id");
            String mvName = mlogBaseData.getString("originalTitle");
            if (StringUtil.isEmpty(mvName)) mvName = mlogBaseData.getString("text");
            mvName = mvName.trim();
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

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        List<NetMvInfo> res = new LinkedList<>();
        int t;

        String id = netMvInfo.getId();
        boolean isVideo = netMvInfo.isVideo();
        boolean isMlog = netMvInfo.isMlog();
        // Mlog 需要先获取视频 id，并转为视频类型
        if (isMlog) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String body = SdkCommon.ncRequest(Method.POST, MLOG_TO_VIDEO_NC_API, String.format("{\"mlogId\":\"%s\"}", id), options)
                    .executeAsStr();
            id = JSONObject.parseObject(body).getString("data");
            netMvInfo.setId(id);
            netMvInfo.setType(MvInfoType.VIDEO);
        }
        // 视频
        if (isVideo) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, RELATED_VIDEO_NC_API,
                            String.format("{\"id\":\"%s\",\"type\":%s}", id, RegexUtil.test("^\\d+$", id) ? 0 : 1), options)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
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
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String mvInfoBody = SdkCommon.ncRequest(Method.POST, SIMILAR_MV_NC_API, String.format("{\"mvid\":\"%s\"}", id), options)
                    .executeAsStr();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = SdkUtil.parseArtist(mvJson);
                String creatorId = SdkUtil.parseArtistId(mvJson);
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

        return new CommonResult<>(res, t);
    }
}
