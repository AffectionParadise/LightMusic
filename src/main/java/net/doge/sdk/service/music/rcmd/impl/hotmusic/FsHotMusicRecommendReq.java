package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class FsHotMusicRecommendReq {
    private static FsHotMusicRecommendReq instance;

    private FsHotMusicRecommendReq() {
    }

    public static FsHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new FsHotMusicRecommendReq();
        return instance;
    }

    // 传播最快(原唱) API (5sing)
    private final String SPREAD_YC_MUSIC_FS_API = "http://5sing.kugou.com/yc/spread/more_%s.shtml";
    // 分享最多(原唱) API (5sing)
    private final String SHARE_YC_MUSIC_FS_API = "http://5sing.kugou.com/yc/share/more_%s.shtml";
    // 传播最快(翻唱) API (5sing)
    private final String SPREAD_FC_MUSIC_FS_API = "http://5sing.kugou.com/fc/spread/more_%s.shtml";
    // 分享最多(翻唱) API (5sing)
    private final String SHARE_FC_MUSIC_FS_API = "http://5sing.kugou.com/fc/share/more_%s.shtml";
    // 热门伴奏(伴奏) API (5sing)
    private final String HOT_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/rmsong/more_%s.shtml";
    // 下载排行(伴奏) API (5sing)
    private final String RANK_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/xzsong/more_%s.shtml";

    /**
     * 传播最快(原唱)
     */
    public CommonResult<NetMusicInfo> getSpreadYcSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(SPREAD_YC_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".lists dl dd.l_info");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(em.text()) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements a = song.select("h3 a");
                Elements pa = song.select("p.m_z a");

                String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分享最多(原唱)
     */
    public CommonResult<NetMusicInfo> getShareYcSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(SHARE_YC_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".lists dl dd.l_info");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(em.text()) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements a = song.select("h3 a");
                Elements pa = song.select("p.m_z a");

                String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 传播最快(翻唱)
     */
    public CommonResult<NetMusicInfo> getSpreadFcSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(SPREAD_FC_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".lists dl dd.l_info");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(em.text()) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements a = song.select("h3 a");
                Elements pa = song.select("p.m_z a");

                String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 分享最多(翻唱)
     */
    public CommonResult<NetMusicInfo> getShareFcSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(SHARE_FC_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".lists dl dd.l_info");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(em.text()) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements a = song.select("h3 a");
                Elements pa = song.select("p.m_z a");

                String songId = RegexUtil.getGroup1("/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = RegexUtil.getGroup1("音乐人：(.*)", pa.text());
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 热门伴奏(伴奏)
     */
    public CommonResult<NetMusicInfo> getHotBzSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(HOT_BZ_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select("tr");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(RegexUtil.getGroup1("\\d+/(\\d+)", em.text())) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements td = song.select("td");
                // 排除表头
                if (td.isEmpty()) continue;

                Elements a = song.select(".aleft a");
                Elements pa = td.get(2).select("a");

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = pa.text();
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 下载排行(伴奏)
     */
    public CommonResult<NetMusicInfo> getRankBzSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        String musicInfoBody = HttpRequest.get(String.format(RANK_BZ_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select("tr");
        if (!songs.isEmpty()) {
            Elements em = doc.select(".page_num em");
            t = Integer.parseInt(RegexUtil.getGroup1("\\d+/(\\d+)", em.text())) * limit;
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);
                Elements td = song.select("td");
                // 排除表头
                if (td.isEmpty()) continue;

                Elements a = song.select(".aleft a");
                Elements pa = td.get(2).select("a");

                String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?/.*?).html", a.attr("href")).replaceFirst("/", "_");
                String songName = a.text();
                String artist = pa.text();
                String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", pa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.FS);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
