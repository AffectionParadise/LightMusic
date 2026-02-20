package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqArtistInfoReq {
    private static QqArtistInfoReq instance;

    private QqArtistInfoReq() {
    }

    public static QqArtistInfoReq getInstance() {
        if (instance == null) instance = new QqArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (QQ)
//    private final String ARTIST_DETAIL_QQ_API = "https://y.qq.com/n/ryqq/singer/%s";
    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        //                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, id))
//                        .executeAsync()
//                        .body();
//                Document doc = Jsoup.parse(artistInfoBody);
//
//                Elements sn = doc.select(".data_statistic__number");
//
//                String name = doc.select("h1.data__name_txt").text();
//                Integer songNum = !sn.isEmpty() ? Integer.parseInt(sn.get(0).text()) : 0;
//                Integer albumNum = sn.size() > 1 ? Integer.parseInt(sn.get(1).text()) : 0;
//                Integer mvNum = sn.size() > 2 ? Integer.parseInt(sn.get(2).text()) : 0;
//                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, id);
//
//                NetArtistInfo artistInfo = new NetArtistInfo();
//                artistInfo.setSource(NetMusicSource.QQ);
//                artistInfo.setId(id);
//                artistInfo.setName(name);
//                artistInfo.setSongNum(songNum);
//                artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
//                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    artistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(artistInfo);

        String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"singer\":{\"method\":\"GetSingerDetail\",\"param\":{\"singer_mids\":[\"%s\"],\"ex_singer\":1," +
                        "\"wiki_singer\":1,\"group_singer\":0,\"pic\":1,\"photos\":0},\"module\":\"music.musichallSinger.SingerInfoInter\"}," +
                        "\"album\":{\"method\":\"GetAlbumList\",\"param\":{\"singerMid\":\"%s\",\"order\":0,\"begin\":0,\"num\":1," +
                        "\"songNumTag\":0,\"singerID\":0},\"module\":\"music.musichallAlbum.AlbumListServer\"}," +
                        "\"mv\":{\"method\":\"GetSingerMvList\",\"param\":{\"singermid\":\"%s\",\"count\":1,\"start\":0,\"order\":1}," +
                        "\"module\":\"MvService.MvInfoProServer\"},\"song\":{\"method\":\"GetSingerSongList\",\"param\":{\"singerMid\":\"%s\"," +
                        "\"order\":1,\"begin\":0,\"num\":1},\"module\":\"musichall.song_list_server\"}}", id, id, id, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject singerJson = artistInfoJson.getJSONObject("singer").getJSONObject("data").getJSONArray("singer_list").getJSONObject(0);
        JSONObject basicInfo = singerJson.getJSONObject("basic_info");
        JSONObject songJson = artistInfoJson.getJSONObject("song").getJSONObject("data");
        JSONObject albumJson = artistInfoJson.getJSONObject("album").getJSONObject("data");
        JSONObject mvJson = artistInfoJson.getJSONObject("mv").getJSONObject("data");

        String name = basicInfo.getString("name");
        Integer songNum = songJson.getIntValue("totalNum");
        Integer albumNum = albumJson.getIntValue("total");
        Integer mvNum = mvJson.getIntValue("total");
        String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, id);

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetResourceSource.QQ);
        artistInfo.setId(id);
        artistInfo.setName(name);
        artistInfo.setSongNum(songNum);
        artistInfo.setAlbumNum(albumNum);
        artistInfo.setMvNum(mvNum);
        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            artistInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(artistInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        //            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, id))
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(artistInfoBody);
//
//            Elements sn = doc.select(".data_statistic__number");
//            Elements ps = doc.select("#popup_data_detail .popup_data_detail__cont p");
//
//            String coverImgUrl = String.format(ARTIST_IMG_QQ_API, id);
//            StringJoiner sj = new StringJoiner("\n");
//            ps.forEach(p -> sj.add(p.text()));
//            String description = sj.toString();
//
//            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(!sn.isEmpty() ? Integer.parseInt(sn.get(0).text()) : 0);
//            if (!artistInfo.hasAlbumNum())
//                artistInfo.setAlbumNum(sn.size() > 1 ? Integer.parseInt(sn.get(1).text()) : 0);
//            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(sn.size() > 2 ? Integer.parseInt(sn.get(2).text()) : 0);
//            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
//            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
//            artistInfo.setDescription(description);

        String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"singer\":{\"method\":\"GetSingerDetail\",\"param\":{\"singer_mids\":[\"%s\"],\"ex_singer\":1," +
                        "\"wiki_singer\":1,\"group_singer\":0,\"pic\":1,\"photos\":0},\"module\":\"music.musichallSinger.SingerInfoInter\"}," +
                        "\"album\":{\"method\":\"GetAlbumList\",\"param\":{\"singerMid\":\"%s\",\"order\":0,\"begin\":0,\"num\":1," +
                        "\"songNumTag\":0,\"singerID\":0},\"module\":\"music.musichallAlbum.AlbumListServer\"}," +
                        "\"mv\":{\"method\":\"GetSingerMvList\",\"param\":{\"singermid\":\"%s\",\"count\":1,\"start\":0,\"order\":1}," +
                        "\"module\":\"MvService.MvInfoProServer\"},\"song\":{\"method\":\"GetSingerSongList\",\"param\":{\"singerMid\":\"%s\"," +
                        "\"order\":1,\"begin\":0,\"num\":1},\"module\":\"musichall.song_list_server\"}}", id, id, id, id))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject singerJson = artistInfoJson.getJSONObject("singer").getJSONObject("data").getJSONArray("singer_list").getJSONObject(0);
        JSONObject exInfo = singerJson.getJSONObject("ex_info");
        JSONObject songJson = artistInfoJson.getJSONObject("song").getJSONObject("data");
        JSONObject albumJson = artistInfoJson.getJSONObject("album").getJSONObject("data");
        JSONObject mvJson = artistInfoJson.getJSONObject("mv").getJSONObject("data");

        Integer songNum = songJson.getIntValue("totalNum");
        Integer albumNum = albumJson.getIntValue("total");
        Integer mvNum = mvJson.getIntValue("total");
        String description = exInfo.getString("desc");
        String coverImgUrl = String.format(ARTIST_IMG_QQ_API, id);

        if (!artistInfo.hasSongNum()) artistInfo.setSongNum(songNum);
        if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(albumNum);
        if (!artistInfo.hasMvNum()) artistInfo.setMvNum(mvNum);
        if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        artistInfo.setDescription(description);
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format("{\"comm\":{\"ct\":24,\"cv\":0},\"singer\":{\"method\":\"get_singer_detail_info\",\"param\":" +
                        "{\"sort\":5,\"singermid\":\"%s\",\"sin\":%s,\"num\":%s},\"module\":\"music.web_singer_info_svr\"}}", id, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
        JSONObject data = artistInfoJson.getJSONObject("singer").getJSONObject("data");
        total = data.getIntValue("total_song");
        JSONArray songArray = data.getJSONArray("songlist");
        for (int i = 0, len = songArray.size(); i < len; i++) {
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
            musicInfo.setSource(NetResourceSource.QQ);
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
