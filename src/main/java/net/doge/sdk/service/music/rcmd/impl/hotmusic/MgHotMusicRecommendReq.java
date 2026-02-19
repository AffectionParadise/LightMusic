package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.rank.info.RankInfoReq;

public class MgHotMusicRecommendReq {
    private static MgHotMusicRecommendReq instance;

    private MgHotMusicRecommendReq() {
    }

    public static MgHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new MgHotMusicRecommendReq();
        return instance;
    }

    // 尖叫热歌榜 API (咪咕)
//    private final String HOT_MUSIC_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=27186466";

    /**
     * 尖叫热歌榜
     */
    public CommonResult<NetMusicInfo> getHotMusic(int page, int limit) {
        return RankInfoReq.getInstance().getMusicInfoInRank(String.valueOf(27186466), NetMusicSource.MG, page, limit);

//            List<NetMusicInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String rankInfoBody = HttpRequest.get(HOT_MUSIC_MG_API)
//                    .executeAsync()
//                    .body();
//            JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
//            JSONObject data = rankInfoJson.getJSONObject("columnInfo");
//            t = data.getIntValue("contentsCount");
//            JSONArray songArray = data.getJSONArray("contents");
//            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
//                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");
//
//                String songId = songJson.getString("copyrightId");
//                // 过滤掉不是歌曲的 objectInfo
//                if (StringUtil.isEmpty(songId)) continue;
//                String name = songJson.getString("songName");
//                String artist = songJson.getString("singer");
//                String artistId = songJson.getString("singerId");
//                String albumName = songJson.getString("album");
//                String albumId = songJson.getString("albumId");
//                Double duration = TimeUtil.toSeconds(songJson.getString("length"));
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.MG);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
//                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//
//                r.add(musicInfo);
//            }
//            return new CommonResult<>(r, t);
    }
}
