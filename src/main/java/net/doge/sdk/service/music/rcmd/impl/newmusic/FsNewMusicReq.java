package net.doge.sdk.service.music.rcmd.impl.newmusic;

import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class FsNewMusicReq {
    private static FsNewMusicReq instance;

    private FsNewMusicReq() {
    }

    public static FsNewMusicReq getInstance() {
        if (instance == null) instance = new FsNewMusicReq();
        return instance;
    }

    // 最新上传(原唱) API (5sing)
    private final String LATEST_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=-1&s=%s&l=%s&p=%s";
    // 网站推荐(原唱) API (5sing)
    private final String WEBSITE_REC_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=1&s=%s&l=%s&p=%s";
    // 候选推荐(原唱) API (5sing)
    private final String CANDI_REC_YC_MUSIC_FS_API = "https://5sing.kugou.com/yc/list?t=2&s=%s&l=%s&p=%s";
    // 最新上传(翻唱) API (5sing)
    private final String LATEST_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=-1&s=%s&l=%s&p=%s";
    // 网站推荐(翻唱) API (5sing)
    private final String WEBSITE_REC_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=1&s=%s&l=%s&p=%s";
    // 候选推荐(翻唱) API (5sing)
    private final String CANDI_REC_FC_MUSIC_FS_API = "https://5sing.kugou.com/fc/list?t=2&s=%s&l=%s&p=%s";
    // 所有伴奏(伴奏) API (5sing)
    private final String ALL_BZ_MUSIC_FS_API = "http://5sing.kugou.com/bz/bzsong/more_%s.shtml";

    /**
     * 最新上传(原唱)
     */
    public CommonResult<NetMusicInfo> getLatestYcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(LATEST_YC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 网站推荐(原唱)
     */
    public CommonResult<NetMusicInfo> getWebsiteRecYcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 候选推荐(原唱)
     */
    public CommonResult<NetMusicInfo> getCandiRecYcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_YC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 最新上传(翻唱)
     */
    public CommonResult<NetMusicInfo> getLatestFcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(LATEST_FC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 网站推荐(翻唱)
     */
    public CommonResult<NetMusicInfo> getWebsiteRecFcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(WEBSITE_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 候选推荐(翻唱)
     */
    public CommonResult<NetMusicInfo> getCandiRecFcSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagType.RECOMMEND_NEW_SONG_FS];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            String musicInfoBody = HttpRequest.get(String.format(CANDI_REC_FC_MUSIC_FS_API, sp[0], sp[1], page))
                    .executeAsStr();
            Document doc = Jsoup.parse(musicInfoBody);
            Elements songs = doc.select(".lists dl dd.l_info");
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
     * 所有伴奏(伴奏)
     */
    public CommonResult<NetMusicInfo> getAllBzSong(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String musicInfoBody = HttpRequest.get(String.format(ALL_BZ_MUSIC_FS_API, page))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select("tr");
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
        return new CommonResult<>(r, t);
    }
}
