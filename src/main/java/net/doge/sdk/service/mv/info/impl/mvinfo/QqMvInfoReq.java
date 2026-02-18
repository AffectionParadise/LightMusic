package net.doge.sdk.service.mv.info.impl.mvinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

import java.awt.image.BufferedImage;

public class QqMvInfoReq {
    private static QqMvInfoReq instance;

    private QqMvInfoReq() {
    }

    public static QqMvInfoReq getInstance() {
        if (instance == null) instance = new QqMvInfoReq();
        return instance;
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public void fillMvDetail(NetMvInfo mvInfo) {
        String mvId = mvInfo.getId();
        String mvBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":4747474},\"mvinfo\":{\"module\":\"video.VideoDataServer\"," +
                        "\"method\":\"get_video_info_batch\",\"param\":{\"vidlist\":[\"%s\"],\"required\":[\"vid\",\"type\",\"sid\"," +
                        "\"cover_pic\",\"duration\",\"singers\",\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\"," +
                        "\"isfav\",\"gmid\"]}},\"other\":{\"module\":\"video.VideoLogicServer\",\"method\":\"rec_video_byvid\"," +
                        "\"param\":{\"vid\":\"%s\",\"required\":[\"vid\",\"type\",\"sid\",\"cover_pic\",\"duration\",\"singers\"," +
                        "\"video_switch\",\"msg\",\"name\",\"desc\",\"playcnt\",\"pubdate\",\"isfav\",\"gmid\",\"uploader_headurl\"," +
                        "\"uploader_nick\",\"uploader_encuin\",\"uploader_uin\",\"uploader_hasfollow\",\"uploader_follower_num\"],\"support\":1}}}", mvId, mvId))
                .executeAsStr();
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
}
