package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.http.HttpRequest;

public class MeMusicUrlReq {
    private static MeMusicUrlReq instance;

    private MeMusicUrlReq() {
    }

    public static MeMusicUrlReq getInstance() {
        if (instance == null) instance = new MeMusicUrlReq();
        return instance;
    }

    // 歌曲信息 API (猫耳)
    private final String SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
        return data.getString(AudioQuality.quality == AudioQuality.STANDARD ? "soundurl_128" : "soundurl");
    }
}
