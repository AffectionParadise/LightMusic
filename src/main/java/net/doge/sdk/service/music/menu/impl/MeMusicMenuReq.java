package net.doge.sdk.service.music.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeMusicMenuReq {
    private static MeMusicMenuReq instance;

    private MeMusicMenuReq() {
    }

    public static MeMusicMenuReq getInstance() {
        if (instance == null) instance = new MeMusicMenuReq();
        return instance;
    }

    // 相似歌曲 API (猫耳)
    private final String SIMILAR_SONG_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";
    // 歌曲推荐电台 API (猫耳)
    private final String SONG_REC_RADIO_ME_API = "https://www.missevan.com/sound/getsoundlike?sound_id=%s&type=15";

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        List<NetMusicInfo> res = new LinkedList<>();
        int t;

        String id = netMusicInfo.getId();
        String musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_ME_API, id))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray songArray = musicInfoJson.getJSONObject("info").getJSONArray("sounds");
        t = songArray.size();
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String songName = songJson.getString("soundstr");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.ME);
            musicInfo.setId(songId);
            musicInfo.setName(songName);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取推荐电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getRecRadios(NetMusicInfo musicInfo) {
        List<NetRadioInfo> res = new LinkedList<>();
        int t;

        String id = musicInfo.getId();
        String radioInfoBody = HttpRequest.get(String.format(SONG_REC_RADIO_ME_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info");
        JSONArray radioArray = data.getJSONArray("dramas");
        t = radioArray.size();
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String coverImgThumbUrl = radioJson.getString("front_cover");
            Long playCount = radioJson.getLong("view_count");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.ME);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setPlayCount(playCount);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            res.add(radioInfo);
        }

        return new CommonResult<>(res, t);
    }
}
