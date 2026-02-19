package net.doge.sdk.service.rank.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;

public class KwRankInfoReq {
    private static KwRankInfoReq instance;

    private KwRankInfoReq() {
    }

    public static KwRankInfoReq getInstance() {
        if (instance == null) instance = new KwRankInfoReq();
        return instance;
    }

    // 榜单信息 API (酷我)
//    private final String RANK_DETAIL_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String RANK_DETAIL_KW_API = "http://kbangserver.kuwo.cn/ksong.s?from=pc&fmt=json&id=%s&pn=%s&rn=%s&type=bang&data=content&show_copyright_off=0&pcmp4=1&isbang=1";

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        //            int lim = Math.min(30, limit);
//            String rankInfoBody = SdkCommon.kwRequest(String.format(RANK_DETAIL_KW_API, id, page, lim))
//                    .executeAsync()
//                    .body();
//            JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
//            JSONObject data = rankInfoJson.getJSONObject("data");
//            int to = data.getIntValue("num");
//            total = PageUtil.totalPage(to, lim) * limit;
//            JSONArray songArray = data.getJSONArray("musicList");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i);
//
//                String songId = songJson.getString("rid");
//                String name = songJson.getString("name");
//                String artist = songJson.getString("artist").replace("&", "、");
//                String artistId = songJson.getString("artistid");
//                String albumName = songJson.getString("album");
//                String albumId = songJson.getString("albumid");
//                Double duration = songJson.getDouble("duration");
//                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.KW);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//
//                res.add(musicInfo);
//            }

        String rankInfoBody = HttpRequest.get(String.format(RANK_DETAIL_KW_API, id, page - 1, limit))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(rankInfoBody);
        total = data.getIntValue("num");
        JSONArray songArray = data.getJSONArray("musiclist");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("name");
            String artist = songJson.getString("artist").replace("&", "、");
            String artistId = songJson.getString("artistid");
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumid");
            Double duration = songJson.getDouble("song_duration");
            String mvId = songJson.getIntValue("mp4sig1") == 0 ? "" : songId;
            String formats = songJson.getString("formats");
            int qualityType = AudioQuality.UNKNOWN;
            if (formats.contains("HIRFLAC")) qualityType = AudioQuality.HR;
            else if (formats.contains("ALFLAC")) qualityType = AudioQuality.SQ;
            else if (formats.contains("MP3H")) qualityType = AudioQuality.HQ;
            else if (formats.contains("MP3128")) qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.KW);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
