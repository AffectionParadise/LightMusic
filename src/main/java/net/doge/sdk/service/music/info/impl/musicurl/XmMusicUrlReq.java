package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.http.HttpRequest;

public class XmMusicUrlReq {
    private static XmMusicUrlReq instance;

    private XmMusicUrlReq() {
    }

    public static XmMusicUrlReq getInstance() {
        if (instance == null) instance = new XmMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (喜马拉雅)
    private final String SONG_URL_XM_API = "https://www.ximalaya.com/revision/play/v1/audio?id=%s&ptype=1";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String playUrlBody = HttpRequest.get(String.format(SONG_URL_XM_API, id))
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(playUrlBody);
        return urlJson.getJSONObject("data").getString("src");
    }
}
