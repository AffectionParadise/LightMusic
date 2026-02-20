package net.doge.sdk.service.music.rcmd.impl.newmusic;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class QqNewMusicReq {
    private static QqNewMusicReq instance;

    private QqNewMusicReq() {
    }

    public static QqNewMusicReq getInstance() {
        if (instance == null) instance = new QqNewMusicReq();
        return instance;
    }

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_QQ];
        if (StringUtil.notEmpty(param)) {
            String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .jsonBody(String.format("{\"comm\":{\"ct\":24},\"new_song\":{\"module\":\"newsong.NewSongServer\"," +
                            "\"method\":\"get_new_song_info\",\"param\":{\"type\":%s}}}", param))
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONArray songArray = musicInfoJson.getJSONObject("new_song").getJSONObject("data").getJSONArray("songlist");
            t = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");
                JSONObject fileJson = songJson.getJSONObject("file");

                String songId = songJson.getString("mid");
                String songName = songJson.getString("title");
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
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setQualityType(qualityType);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
