package net.doge.sdk.service.music.rcmd.impl.newmusic;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.rank.info.RankInfoReq;

public class KwNewMusicReq {
    private static KwNewMusicReq instance;

    private KwNewMusicReq() {
    }

    public static KwNewMusicReq getInstance() {
        if (instance == null) instance = new KwNewMusicReq();
        return instance;
    }

    // 新歌榜 API (酷我)
    //    private final String NEW_SONG_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=16&pn=%s&rn=%s&httpsStatus=1";

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(int page, int limit) {
        return RankInfoReq.getInstance().getMusicInfoInRank(String.valueOf(16), NetResourceSource.KW, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = SdkCommon.kwRequest(String.format(NEW_SONG_KW_API, page, limit)).executeAsync();
//            if (resp.isSuccessful()) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                t = data.getIntValue("num");
//                JSONArray songArray = data.getJSONArray("musicList");
//                t = Math.max(t, songArray.size());
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
