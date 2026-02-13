package net.doge.sdk.service.ranking.info;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.playlist.info.PlaylistInfoReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.util.core.DurationUtil;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.StringUtil;

import java.util.LinkedList;
import java.util.List;

public class RankingInfoReq {
    private static RankingInfoReq instance;

    private RankingInfoReq() {
    }

    public static RankingInfoReq getInstance() {
        if (instance == null) instance = new RankingInfoReq();
        return instance;
    }

    // 榜单信息 API (酷狗)
    private final String RANKING_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=%s&page=%s&pagesize=%s";
    // 榜单信息 API (酷我)
//    private final String RANKING_DETAIL_KW_API = "https://kuwo.cn/api/www/bang/bang/musicList?bangId=%s&pn=%s&rn=%s&httpsStatus=1";
    private final String RANKING_DETAIL_KW_API = "http://kbangserver.kuwo.cn/ksong.s?from=pc&fmt=json&id=%s&pn=%s&rn=%s&type=bang&data=content&show_copyright_off=0&pcmp4=1&isbang=1";
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

        GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImgThumb(SdkUtil.extractCover(rankingInfo.getCoverImgUrl())));
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
        if (source == NetMusicSource.NC) {
            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                            "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", id, 0, 1))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("detail").getJSONObject("data").getJSONObject("data");

            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // QQ 需要额外补全榜单描述
            rankingInfo.setDescription(data.getString("intro").replace("<br>", "\n"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, id))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");

            if (!rankingInfo.hasPlayCount())
                rankingInfo.setPlayCount(data.getJSONObject("opNumItem").getLong("playNum"));
            if (!rankingInfo.hasUpdateTime()) rankingInfo.setUpdateTime(data.getString("columnUpdateTime"));
            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // 咪咕需要额外补全榜单描述
            rankingInfo.setDescription(data.getString("columnDes"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("info").getJSONObject("album");

            String description = HtmlUtil.removeHtmlLabel(data.getString("intro"));

            GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
            rankingInfo.setDescription(description);
        }
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String id, int source, int page, int limit) {
        int total = 0;
        List<NetMusicInfo> res = new LinkedList<>();

        // 网易云(榜单就是歌单，接口分页)
        if (source == NetMusicSource.NC) {
            return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(id, source, page, limit);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_KG_API, id, page, limit))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String name = songJson.getString("songname");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("remark");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                JSONArray mvdata = songJson.getJSONArray("mvdata");
                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");
                int qualityType = AudioQuality.UNKNOWN;
                if (songJson.getLong("filesize_high") != 0) qualityType = AudioQuality.HR;
                else if (songJson.getLong("sqfilesize") != 0) qualityType = AudioQuality.SQ;
                else if (songJson.getLong("320filesize") != 0) qualityType = AudioQuality.HQ;
                else if (songJson.getLong("filesize") != 0) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
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
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\"," +
                            "\"param\":{\"topId\":%s,\"offset\":%s,\"num\":%s}},\"comm\":{\"ct\":24,\"cv\":0}}", id, 0, 1000))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("detail").getJSONObject("data");
            total = data.getJSONObject("data").getIntValue("totalNum");
            JSONArray songArray = data.getJSONArray("songInfoList");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");
                JSONObject fileJson = songJson.getJSONObject("file");

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
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
        }

        // 酷我(接口分页)
        else if (source == NetMusicSource.KW) {
//            int lim = Math.min(30, limit);
//            String rankingInfoBody = SdkCommon.kwRequest(String.format(RANKING_DETAIL_KW_API, id, page, lim))
//                    .executeAsync()
//                    .body();
//            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
//            JSONObject data = rankingInfoJson.getJSONObject("data");
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

            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_KW_API, id, page - 1, limit))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(rankingInfoBody);
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
        }

        // 咪咕(程序分页)
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, id))
                    .executeAsStr();
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
                Double duration = DurationUtil.toSeconds(songJson.getString("length"));
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray newRateFormats = songJson.getJSONArray("newRateFormats");
                for (int k = newRateFormats.size() - 1; k >= 0; k--) {
                    String formatType = newRateFormats.getJSONObject(k).getString("formatType");
                    if ("ZQ".equals(formatType)) qualityType = AudioQuality.HR;
                    else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                    else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                    else if ("PQ".equals(formatType)) qualityType = AudioQuality.LQ;
                    if (qualityType != AudioQuality.UNKNOWN) break;
                }

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setQualityType(qualityType);

                res.add(musicInfo);
            }
        }

        // 千千(程序分页)
        else if (source == NetMusicSource.QI) {
            String rankingInfoBody = SdkCommon.qiRequest(String.format(RANKING_DETAIL_QI_API, id, page, limit, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");
                int qualityType = AudioQuality.UNKNOWN;
                String allRate = songJson.getJSONArray("allRate").toString();
                if (allRate.contains("3000")) qualityType = AudioQuality.SQ;
                else if (allRate.contains("320")) qualityType = AudioQuality.HQ;
                else if (allRate.contains("128")) qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setQualityType(qualityType);

                res.add(musicInfo);
            }
        }

        // 猫耳(程序分页)
        else if (source == NetMusicSource.ME) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, id))
                    .executeAsStr();
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

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, total);
    }
}
