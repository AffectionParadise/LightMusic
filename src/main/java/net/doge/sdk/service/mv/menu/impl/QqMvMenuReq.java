package net.doge.sdk.service.mv.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqMvMenuReq {
    private static QqMvMenuReq instance;

    private QqMvMenuReq() {
    }

    public static QqMvMenuReq getInstance() {
        if (instance == null) instance = new QqMvMenuReq();
        return instance;
    }

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo musicInfo, int page, int limit) {
        List<NetMvInfo> res = new LinkedList<>();
        int t;

        String id = musicInfo.getId();
        // 先根据 mid 获取 id
        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", id))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");

        String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"g_tk\":5381,\"format\":\"json\",\"inCharset\":\"utf-8\",\"outCharset\":\"utf-8\"," +
                        "\"notice\":0,\"platform\":\"h5\",\"needNewCode\":1},\"video\":{\"module\":\"MvService.MvInfoProServer\"," +
                        "\"method\":\"GetSongRelatedMv\",\"param\":{\"songid\":%s,\"songtype\":1,\"lastmvid\":0,\"num\":10}}}", id))
                .executeAsStr();
        JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
        JSONArray mvArray = mvInfoJson.getJSONObject("video").getJSONObject("data").getJSONArray("list");
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            JSONObject mvJson = mvArray.getJSONObject(i);

            String mvId = mvJson.getString("vid");
            String mvName = mvJson.getString("title").trim();
            String artistName = SdkUtil.parseArtist(mvJson);
            String creatorId = SdkUtil.parseArtistId(mvJson);
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
        String mvInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":4747474},\"mvinfo\":{\"module\":\"video.VideoDataServer\"," +
                        "\"method\":\"get_video_info_batch\",\"param\":{\"vidlist\":[\"%s\"],\"required\":[\"vid\",\"type\",\"sid\"," +
                        "\"cover_pic\",\"duration\",\"singers\",\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\"," +
                        "\"isfav\",\"gmid\"]}},\"other\":{\"module\":\"video.VideoLogicServer\",\"method\":\"rec_video_byvid\"," +
                        "\"param\":{\"vid\":\"%s\",\"required\":[\"vid\",\"type\",\"sid\",\"cover_pic\",\"duration\",\"singers\"," +
                        "\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\",\"isfav\",\"gmid\",\"uploader_headurl\"," +
                        "\"uploader_nick\",\"uploader_encuin\",\"uploader_uin\",\"uploader_hasfollow\",\"uploader_follower_num\"],\"support\":1}}}", id, id))
                .executeAsStr();
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

        return new CommonResult<>(res, t);
    }
}
