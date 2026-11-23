package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LzMusicUrlReq {
    private static LzMusicUrlReq instance;

    private LzMusicUrlReq() {
    }

    public static LzMusicUrlReq getInstance() {
        if (instance == null) instance = new LzMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (李志)
    private final String SONG_URL_LZ_API = "https://www.lizhinb.com/gequ/";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo, boolean forDownload) {
        String id = musicInfo.getId();
        String albumId = musicInfo.getAlbumId();
        String albumSongBody = HttpRequest.get(SONG_URL_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(albumSongBody);
        Elements article = doc.select(String.format(".oa-album-card[data-album-id=\"%s\"]", albumId));
        String albumSongJsonStr = article.attr("data-tracks");
        JSONArray songArray = JSONArray.parseArray(albumSongJsonStr);
        JSONObject songJson = JsonUtil.findObject(songArray, "track_id", id);
        int quality = forDownload ? AudioQuality.downQuality : AudioQuality.playQuality;
        return songJson.getString(quality == AudioQuality.STANDARD ? "audio_url" : "hq_audio_url");
    }
}
