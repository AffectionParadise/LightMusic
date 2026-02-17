package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class QqHotMusicRecommendReq {
    private static QqHotMusicRecommendReq instance;

    private QqHotMusicRecommendReq() {
    }

    public static QqHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new QqHotMusicRecommendReq();
        return instance;
    }

    /**
     * 流行指数榜
     */
    public CommonResult<NetMusicInfo> getPopularMusic(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                        "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", 4, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
        t = data.getJSONObject("data").getIntValue("totalNum");
        JSONArray songArray = data.getJSONArray("songInfoList");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("album");
            JSONObject fileJson = songJson.getJSONObject("file");

            String id = songJson.getString("mid");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("title");
            String albumId = albumJson.getString("mid");
            Double duration = songJson.getDouble("interval");
            String mvId = songJson.getJSONObject("mv").getString("vid");
            int qualityType = AudioQuality.UNKNOWN;
            if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
            else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
            else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
            else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.QQ);
            musicInfo.setId(id);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 热歌榜
     */
    public CommonResult<NetMusicInfo> getHotMusic(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                        "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", 26, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("detail").getJSONObject("data");
        t = data.getJSONObject("data").getIntValue("totalNum");
        JSONArray songArray = data.getJSONArray("songInfoList");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            JSONObject albumJson = songJson.getJSONObject("album");
            JSONObject fileJson = songJson.getJSONObject("file");

            String id = songJson.getString("mid");
            String name = songJson.getString("title");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = albumJson.getString("title");
            String albumId = albumJson.getString("mid");
            Double duration = songJson.getDouble("interval");
            String mvId = songJson.getJSONObject("mv").getString("vid");
            int qualityType = AudioQuality.UNKNOWN;
            if (fileJson.getLong("size_hires") != 0) qualityType = AudioQuality.HR;
            else if (fileJson.getLong("size_flac") != 0) qualityType = AudioQuality.SQ;
            else if (fileJson.getLong("size_320mp3") != 0) qualityType = AudioQuality.HQ;
            else if (fileJson.getLong("size_128mp3") != 0) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.QQ);
            musicInfo.setId(id);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
