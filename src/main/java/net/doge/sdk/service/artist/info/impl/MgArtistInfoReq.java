package net.doge.sdk.service.artist.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class MgArtistInfoReq {
    private static MgArtistInfoReq instance;

    private MgArtistInfoReq() {
    }

    public static MgArtistInfoReq getInstance() {
        if (instance == null) instance = new MgArtistInfoReq();
        return instance;
    }

    // 歌手信息 API (咪咕)
    private final String ARTIST_DETAIL_MG_API = "http://music.migu.cn/v3/music/artist/%s";
    // 歌手歌曲 API (咪咕)
    private final String ARTIST_SONGS_MG_API = "http://music.migu.cn/v3/music/artist/%s/song?page=%s";

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);

        String name = doc.select(".artist-info .artist-name a").text();
        String txt = doc.select(".artist-section-title").text();
        String songNumText = RegexUtil.getGroup1("全部(\\d+)首", txt);
        Integer songNum = StringUtil.isEmpty(songNumText) ? 0 : Integer.parseInt(songNumText);
        String albumNumText = RegexUtil.getGroup1("全部(\\d+)张", txt);
        Integer albumNum = StringUtil.isEmpty(albumNumText) ? 0 : Integer.parseInt(albumNumText);
        String mvNumText = RegexUtil.getGroup1("全部(\\d+)支", txt);
        Integer mvNum = StringUtil.isEmpty(mvNumText) ? 0 : Integer.parseInt(mvNumText);
        String coverImgThumbUrl = "https:" + doc.select(".artist-info .artist-avatar img").attr("src");

        NetArtistInfo artistInfo = new NetArtistInfo();
        artistInfo.setSource(NetMusicSource.MG);
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
        String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);

        String txt = doc.select(".artist-section-title").text();
        String coverImgUrl = "https:" + doc.select(".artist-info .artist-avatar img").attr("src");
        String description = doc.select("#J_ArtistIntro .content").text();

        if (!artistInfo.hasSongNum()) {
            String songNumText = RegexUtil.getGroup1("全部(\\d+)首", txt);
            Integer songNum = StringUtil.isEmpty(songNumText) ? 0 : Integer.parseInt(songNumText);
            artistInfo.setSongNum(songNum);
        }
        if (!artistInfo.hasAlbumNum()) {
            String albumNumText = RegexUtil.getGroup1("全部(\\d+)张", txt);
            Integer albumNum = StringUtil.isEmpty(albumNumText) ? 0 : Integer.parseInt(albumNumText);
            artistInfo.setAlbumNum(albumNum);
        }
        if (!artistInfo.hasMvNum()) {
            String mvNumText = RegexUtil.getGroup1("全部(\\d+)支", txt);
            Integer mvNum = StringUtil.isEmpty(mvNumText) ? 0 : Integer.parseInt(mvNumText);
            artistInfo.setMvNum(mvNum);
        }
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
        String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_MG_API, id, page))
                .executeAsStr();
        Document doc = Jsoup.parse(artistInfoBody);
        Elements pageElem = doc.select(".views-pagination .pagination-item");
        total = !pageElem.isEmpty() ? Integer.parseInt(pageElem.get(pageElem.size() - 1).text()) * limit : limit;
        Elements songArray = doc.select(".row.J_CopySong");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            Element song = songArray.get(i);
            Elements a = song.select("a.song-name-txt");
            Elements aa = song.select(".J_SongSingers a");
            Elements ba = song.select(".song-belongs a");
            Elements fa = song.select("a.flag.flag-mv");

            String songId = RegexUtil.getGroup1("/v3/music/song/(.*)", a.attr("href"));
            String name = a.text();
            StringJoiner sj = new StringJoiner("、");
            aa.forEach(aElem -> sj.add(aElem.text()));
            String artist = sj.toString();
            String artistId = aa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/music/artist/(\\d+)", aa.get(0).attr("href"));
            String albumName = ba.text();
            String albumId = RegexUtil.getGroup1("/v3/music/album/(\\d+)", ba.attr("href"));
            String mvId = fa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/video/mv/(.*)", fa.attr("href"));
            int qualityType;
            if (!song.select("i.flag.flag-bit24").isEmpty()) qualityType = AudioQuality.HR;
            else if (!song.select("i.flag.flag-sq").isEmpty() || !song.select("i.flag.flag-d3").isEmpty())
                qualityType = AudioQuality.SQ;
            else if (!song.select("i.flag.flag-hq").isEmpty()) qualityType = AudioQuality.HQ;
            else qualityType = AudioQuality.LQ;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.MG);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setMvId(mvId);
            musicInfo.setQualityType(qualityType);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
