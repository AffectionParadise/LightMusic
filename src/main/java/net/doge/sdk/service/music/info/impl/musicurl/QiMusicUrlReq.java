package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.StringUtil;

public class QiMusicUrlReq {
    private static QiMusicUrlReq instance;

    private QiMusicUrlReq() {
    }

    public static QiMusicUrlReq getInstance() {
        if (instance == null) instance = new QiMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (千千)
    private final String SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String playUrlBody = SdkCommon.qiRequest(String.format(SONG_URL_QI_API, id, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(playUrlBody).getJSONObject("data");
        // 排除试听部分，直接换源
        if (data.getIntValue("isVip") == 0) {
            String url = data.getString("path");
            if (StringUtil.isEmpty(url)) url = data.getJSONObject("trail_audio_info").getString("path");
            return url;
        }
        return "";
    }
}
