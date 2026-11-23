package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class MeMusicUrlReq {
    private static MeMusicUrlReq instance;

    private MeMusicUrlReq() {
    }

    public static MeMusicUrlReq getInstance() {
        if (instance == null) instance = new MeMusicUrlReq();
        return instance;
    }

    // 歌曲信息 API (猫耳)
    private final String SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo, boolean forDownload) {
        String id = musicInfo.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("soundid", id);
        String songBody = HttpRequest.get(SONG_DETAIL_ME_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
        int quality = forDownload ? AudioQuality.downQuality : AudioQuality.playQuality;
        return data.getString(quality == AudioQuality.STANDARD ? "soundurl_128" : "soundurl");
    }
}
