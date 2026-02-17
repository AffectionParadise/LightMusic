package net.doge.sdk.service.music.rcmd.impl.newmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.json.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class MgNewMusicReq {
    private static MgNewMusicReq instance;

    private MgNewMusicReq() {
    }

    public static MgNewMusicReq getInstance() {
        if (instance == null) instance = new MgNewMusicReq();
        return instance;
    }

    // 推荐新歌 API (咪咕)
    private final String RECOMMEND_NEW_SONG_MG_API = "http://m.music.migu.cn/migu/remoting/cms_list_tag?nid=23853978&pageNo=%s&pageSize=%s";

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_MG_API, page - 1, limit))
                .header(Header.REFERER, "https://m.music.migu.cn/")
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject data = musicInfoJson.getJSONObject("result");
        t = data.getIntValue("totalCount");
        JSONArray songArray = data.getJSONArray("results");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i).getJSONObject("songData");

            String songId = songJson.getString("songId");
            String songName = songJson.getString("songName");
            String artist = SdkUtil.joinString(songJson.getJSONArray("singerName"));
            JSONArray singerIdArray = songJson.getJSONArray("singerId");
            String artistId = JsonUtil.isEmpty(singerIdArray) ? "" : singerIdArray.getString(0);
            int qualityType;
            if (songJson.getIntValue("has24Bitqq") == 1) qualityType = AudioQuality.HR;
            else if (songJson.getIntValue("hasSQqq") == 1) qualityType = AudioQuality.SQ;
            else if (songJson.getIntValue("hasHQqq") == 1) qualityType = AudioQuality.HQ;
            else qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.MG);
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setQualityType(qualityType);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
