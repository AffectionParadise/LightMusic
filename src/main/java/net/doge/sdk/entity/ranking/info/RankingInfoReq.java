package net.doge.sdk.entity.ranking.info;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetRankingInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.entity.playlist.info.PlaylistInfoReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.StringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class RankingInfoReq {
    // 榜单信息 API (酷狗)
    private final String RANKING_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=%s&page=%s&pagesize=%s";
    // 榜单信息 API (QQ)
    private final String RANKING_DETAIL_QQ_API = SdkCommon.prefixQQ33 + "/top?id=%s&pageSize=%s";
    // 榜单信息 API (酷我)
    private final String RANKING_DETAIL_KW_API = "http://www.kuwo.cn/api/www/bang/bang/musicList?bangId=%s&pn=%s&rn=%s&httpsStatus=1";
    // 榜单信息 API (咪咕)
    private final String RANKING_DETAIL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=%s";
    // 榜单信息 API (千千)
    private final String RANKING_DETAIL_QI_API = "https://music.91q.com/v1/bd/list?appid=16073360&bdid=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // 榜单信息 API (猫耳)
    private final String RANKING_DETAIL_ME_API = "https://www.missevan.com/sound/soundalllist?albumid=%s";

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public void preloadRankingInfo(NetRankingInfo rankingInfo) {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImgThumb(SdkUtil.extractCover(rankingInfo.getCoverImgUrl())));
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankingInfo(NetRankingInfo rankingInfo) {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;

        int source = rankingInfo.getSource();
        String id = rankingInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_QQ_API, id, 1))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");

            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // QQ 需要额外补全榜单描述
            rankingInfo.setDescription(data.getJSONObject("info").getString("desc").replace("<br>", "\n"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, id))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");

            if (!rankingInfo.hasPlayCount())
                rankingInfo.setPlayCount(data.getJSONObject("opNumItem").getLong("playNum"));
            if (!rankingInfo.hasUpdateTime()) rankingInfo.setUpdateTime(data.getString("columnUpdateTime"));
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // 咪咕需要额外补全榜单描述
            rankingInfo.setDescription(data.getString("columnDes"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, id))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("info").getJSONObject("album");

            String description = StringUtil.removeHTMLLabel(data.getString("intro"));

            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            rankingInfo.setDescription(description);
        }
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String rankingId, int source, int limit, int page) {
        int total = 0;
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        // 网易云(榜单就是歌单，接口分页)
        if (source == NetMusicSource.NET_CLOUD) {
            return new PlaylistInfoReq().getMusicInfoInPlaylist(rankingId, source, limit, page);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_KG_API, rankingId, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String name = songJson.getString("songname");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.KG);
                JSONArray artistArray = songJson.getJSONArray("authors");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("author_id") : "";
//                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                JSONArray mvdata = songJson.getJSONArray("mvdata");
                String mvId = mvdata == null ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
                musicInfo.setArtistId(artistId);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                musicInfos.add(musicInfo);
            }
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_QQ_API, rankingId, 1000))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("name");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.QQ);
                JSONArray singerArray = songJson.getJSONArray("singer");
                String artistId = singerArray.isEmpty() ? "" : singerArray.getJSONObject(0).getString("mid");
                String albumName = songJson.getJSONObject("album").getString("name");
                String albumId = songJson.getJSONObject("album").getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                musicInfos.add(musicInfo);
            }
        }

        // 酷我(接口分页)
        else if (source == NetMusicSource.KW) {
            int lim = Math.min(30, limit);
            String rankingInfoBody = SdkCommon.kwRequest(String.format(RANKING_DETAIL_KW_API, rankingId, page, lim))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            int to = data.getIntValue("num");
            total = (to % lim == 0 ? to / lim : to / lim + 1) * limit;
            JSONArray songArray = data.getJSONArray("musicList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                String name = songJson.getString("name");
                String artist = songJson.getString("artist").replace("&", "、");
                String artistId = songJson.getString("artistid");
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumid");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;

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

                musicInfos.add(musicInfo);
            }
        }

        // 咪咕(程序分页)
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, rankingId))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
            total = data.getIntValue("contentsCount");
            JSONArray songArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");

                String songId = songJson.getString("copyrightId");
                // 过滤掉不是歌曲的 objectInfo
                if (StringUtil.isEmpty(songId)) continue;
                String name = songJson.getString("songName");
                String artist = songJson.getString("singer");
                String artistId = songJson.getString("singerId");
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumId");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);

                musicInfos.add(musicInfo);
            }
        }

        // 千千(程序分页)
        else if (source == NetMusicSource.QI) {
            String rankingInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(RANKING_DETAIL_QI_API, rankingId, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtists(songJson, NetMusicSource.QI);
                JSONArray artistArray = songJson.getJSONArray("artist");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                musicInfos.add(musicInfo);
            }
        }

        // 猫耳(程序分页)
        else if (source == NetMusicSource.ME) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, rankingId))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("info");
            JSONArray songArray = data.getJSONArray("sounds");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(page * limit, songArray.size()); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("soundstr");
                String artist = songJson.getString("username");
                String artistId = songJson.getString("user_id");
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);

                musicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(musicInfos, total);
    }
}
