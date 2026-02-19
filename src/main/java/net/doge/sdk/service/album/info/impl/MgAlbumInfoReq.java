package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.media.DurationUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MgAlbumInfoReq {
    private static MgAlbumInfoReq instance;

    private MgAlbumInfoReq() {
    }

    public static MgAlbumInfoReq getInstance() {
        if (instance == null) instance = new MgAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (咪咕)
//    private final String ALBUM_DETAIL_MG_API = "http://music.migu.cn/v3/music/album/%s?page=%s";
    private final String ALBUM_DETAIL_MG_API = "https://app.c.nf.migu.cn/v1.0/content/resourceinfo.do?needSimple=01&resourceType=5&resourceId=%s";
    // 专辑歌曲 API (咪咕)
    private final String ALBUM_SONGS_MG_API = "https://app.c.nf.migu.cn/v1.0/content/resourceinfo.do?needSimple=01&resourceType=5&resourceId=%s";

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id) {
        List<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        //                String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id, 1))
//                        .executeAsStr();
//                Document doc = Jsoup.parse(albumInfoBody);
//                Elements as = doc.select(".singer-name > a");
//
//                // 部分歌曲无专辑，跳出
//                if (!as.isEmpty()) {
//                    String name = doc.select(".content .title").text();
//                    StringJoiner sj = new StringJoiner("、");
//                    as.forEach(a -> sj.add(a.text()));
//                    String artist = sj.toString();
//                    String artistId = RegexUtil.getGroup1("/v3/music/artist/(\\d+)", as.first().attr("href"));
//                    String publishTime = doc.select(".pub-date").first().ownText();
//                    String coverImgThumbUrl = "https:" + doc.select(".mad-album-info .thumb-img").attr("src");
//                    Integer songNum = doc.select(".row.J_CopySong").size();
//
//                    NetAlbumInfo albumInfo = new NetAlbumInfo();
//                    albumInfo.setSource(NetMusicSource.MG);
//                    albumInfo.setId(id);
//                    albumInfo.setName(name);
//                    albumInfo.setArtist(artist);
//                    albumInfo.setArtistId(artistId);
//                    albumInfo.setPublishTime(publishTime);
//                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    albumInfo.setSongNum(songNum);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                        albumInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(albumInfo);
//                }

        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(albumInfoBody).getJSONArray("resource").getJSONObject(0);

        String name = data.getString("title");
        String artist = data.getString("singer");
        String artistId = data.getString("singerId");
        String publishTime = data.getString("publishTime");
        JSONArray imgItems = data.getJSONArray("imgItem");
        String coverImgThumbUrl = JsonUtil.isEmpty(imgItems) ? null : SdkUtil.findFeatureObj(imgItems, "imgSizeType", "03").getString("img");
        Integer songNum = data.getIntValue("totalCount");

        NetAlbumInfo albumInfo = new NetAlbumInfo();
        albumInfo.setSource(NetMusicSource.MG);
        albumInfo.setId(id);
        albumInfo.setName(name);
        albumInfo.setArtist(artist);
        albumInfo.setArtistId(artistId);
        albumInfo.setPublishTime(publishTime);
        albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        albumInfo.setSongNum(songNum);
        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            albumInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(albumInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        //            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id, 1))
//                    .executeAsStr();
//            Document doc = Jsoup.parse(albumInfoBody);
//
//            String coverImgUrl = "https:" + doc.select(".mad-album-info .thumb-img").attr("src");
//            String description = doc.select("#J_IntroInline").text();
//
//            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
//            GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
//            albumInfo.setDescription(description);

        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(albumInfoBody).getJSONArray("resource").getJSONObject(0);

        JSONArray imgItems = data.getJSONArray("imgItem");
        String coverImgUrl = JsonUtil.isEmpty(imgItems) ? null : SdkUtil.findFeatureObj(imgItems, "imgSizeType", "03").getString("img");
        String description = data.getString("summary");

        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        albumInfo.setDescription(description);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = albumInfo.getId();
//            String musicInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id, page))
//                    .executeAsStr();
//            Document doc = Jsoup.parse(musicInfoBody);
//            Elements pageElem = doc.select(".page *");
//            total = !pageElem.isEmpty() ? Integer.parseInt(pageElem.get(pageElem.size() - 2).text()) * limit : limit;
//            Elements songArray = doc.select(".row.J_CopySong");
//            for (int i = 0, len = songArray.size(); i < len; i++) {
//                Element song = songArray.get(i);
//                Elements a = song.select("a.song-name-txt");
//                Elements aa = song.select(".J_SongSingers a");
//                Elements sd = song.select(".song-duration span");
//                Elements fa = song.select("a.flag.flag-mv");
//
//                String songId = RegexUtil.getGroup1("/v3/music/song/(.*)", a.attr("href"));
//                String name = a.text();
//                StringJoiner sj = new StringJoiner("、");
//                aa.forEach(aElem -> sj.add(aElem.text()));
//                String artist = sj.toString();
//                String artistId = aa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/music/artist/(\\d+)", aa.get(0).attr("href"));
//                Double duration = DurationUtil.toSeconds(sd.text());
//                // 歌曲对应的专辑可能不是本专辑
////                String albumName = albumInfo.getName();
////                String albumId = id;
//                String mvId = fa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/video/mv/(.*)", fa.attr("href"));
//                int qualityType;
//                if (!song.select("i.flag.flag-bit24").isEmpty()) qualityType = AudioQuality.HR;
//                else if (!song.select("i.flag.flag-sq").isEmpty() || !song.select("i.flag.flag-d3").isEmpty())
//                    qualityType = AudioQuality.SQ;
//                else if (!song.select("i.flag.flag-hq").isEmpty()) qualityType = AudioQuality.HQ;
//                else qualityType = AudioQuality.LQ;
//
//                NetMusicInfo musicInfo = new NetMusicInfo();
//                musicInfo.setSource(NetMusicSource.MG);
//                musicInfo.setId(songId);
//                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
////                musicInfo.setAlbumName(albumName);
////                musicInfo.setAlbumId(albumId);
//                musicInfo.setDuration(duration);
//                musicInfo.setMvId(mvId);
//                musicInfo.setQualityType(qualityType);
//
//                res.add(musicInfo);
//            }

        String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_MG_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject data = albumInfoJson.getJSONArray("resource").getJSONObject(0);
        JSONArray songArray = data.getJSONArray("songItems");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("copyrightId");
            String name = songJson.getString("songName");
            String artist = SdkUtil.parseArtist(songJson);
            String artistId = SdkUtil.parseArtistId(songJson);
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("albumId");
            Double duration = DurationUtil.toSeconds(songJson.getString("length"));
            String mvId = songJson.getString("mvId");
            int qualityType = AudioQuality.UNKNOWN;
            JSONArray newRateFormats = songJson.getJSONArray("newRateFormats");
            for (int k = newRateFormats.size() - 1; k >= 0; k--) {
                String formatType = newRateFormats.getJSONObject(k).getString("formatType");
                if ("ZQ".equals(formatType)) qualityType = AudioQuality.HR;
                else if ("SQ".equals(formatType)) qualityType = AudioQuality.SQ;
                else if ("HQ".equals(formatType)) qualityType = AudioQuality.HQ;
                else if ("PQ".equals(formatType)) qualityType = AudioQuality.MQ;
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
            musicInfo.setMvId(mvId);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
