package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.ranking.info.RankingInfoReq;

public class KwHotMusicRecommendReq {
    private static KwHotMusicRecommendReq instance;

    private KwHotMusicRecommendReq() {
    }

    public static KwHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new KwHotMusicRecommendReq();
        return instance;
    }

    // 飙升榜 API (酷我)
//    private final String UP_MUSIC_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=93&pn=%s&rn=%s&httpsStatus=1";
    // 热歌榜 API (酷我)
//    private final String HOT_MUSIC_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 飙升榜
     */
    public CommonResult<NetMusicInfo> getUpMusic(int page, int limit) {
        return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(93), NetMusicSource.KW, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(UP_MUSIC_KW_API, page, limit)).executeAsync();
//            if (resp.isSuccessful()) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String id = songJson.getString("rid");
//                    String name = songJson.getString("name");
//                    String artist = songJson.getString("artist").replace("&", "、");
//                    String artistId = songJson.getString("artistid");
//                    String albumName = songJson.getString("album");
//                    String albumId = songJson.getString("albumid");
//                    Double duration = songJson.getDouble("duration");
//                    String mvId = songJson.getIntValue("hasmv") == 0 ? "" : id;
//                    String formats = songJson.getString("formats");
//                    int qualityType = AudioQuality.UNKNOWN;
//                    if (formats.contains("HIRFLAC")) qualityType = AudioQuality.HR;
//                    else if (formats.contains("ALFLAC")) qualityType = AudioQuality.SQ;
//                    else if (formats.contains("MP3H")) qualityType = AudioQuality.HQ;
//                    else if (formats.contains("MP3128")) qualityType = AudioQuality.LQ;
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.KW);
//                    musicInfo.setId(id);
//                    musicInfo.setName(name);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setDuration(duration);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
    }

    /**
     * 热歌榜
     */
    public CommonResult<NetMusicInfo> getHotMusic(int page, int limit) {
        return RankingInfoReq.getInstance().getMusicInfoInRanking(String.valueOf(16), NetMusicSource.KW, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(HOT_MUSIC_KW_API, page, limit)).executeAsync();
//            if (resp.isSuccessful()) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                for (int i = 0, len = songArray.size(); i < len; i++) {
//                    JSONObject songJson = songArray.getJSONObject(i);
//
//                    String id = songJson.getString("rid");
//                    String name = songJson.getString("name");
//                    String artist = songJson.getString("artist").replace("&", "、");
//                    String artistId = songJson.getString("artistid");
//                    String albumName = songJson.getString("album");
//                    String albumId = songJson.getString("albumid");
//                    Double duration = songJson.getDouble("duration");
//                    String mvId = songJson.getIntValue("hasmv") == 0 ? "" : id;
//
//                    NetMusicInfo musicInfo = new NetMusicInfo();
//                    musicInfo.setSource(NetMusicSource.KW);
//                    musicInfo.setId(id);
//                    musicInfo.setName(name);
//                    musicInfo.setArtist(artist);
//                    musicInfo.setArtistId(artistId);
//                    musicInfo.setAlbumName(albumName);
//                    musicInfo.setAlbumId(albumId);
//                    musicInfo.setDuration(duration);
//                    musicInfo.setMvId(mvId);
//
//                    r.add(musicInfo);
//                }
//            }
//            return new CommonResult<>(r, t);
    }
}
