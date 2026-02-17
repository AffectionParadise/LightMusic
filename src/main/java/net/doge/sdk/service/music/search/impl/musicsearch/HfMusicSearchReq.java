package net.doge.sdk.service.music.search.impl.musicsearch;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class HfMusicSearchReq {
    private static HfMusicSearchReq instance;

    private HfMusicSearchReq() {
    }

    public static HfMusicSearchReq getInstance() {
        if (instance == null) instance = new HfMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (音乐磁场)
    private final String SEARCH_MUSIC_HF_API = "https://www.hifiti.com/search-%s-1-0-%s.htm";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_HF_API, encodedKeyword.replace("%", "_"), page))
                .cookie(SdkCommon.HF_COOKIE)
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".media.thread.tap");
        Elements ap = doc.select("a.page-link");
        String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
        if (StringUtil.isEmpty(ts))
            ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
        boolean hasTs = StringUtil.notEmpty(ts);
        if (hasTs) t = Integer.parseInt(ts) * limit;
        else t = limit;
        for (int i = 0, len = songs.size(); i < len; i++) {
            Element song = songs.get(i);

            Elements a = song.select(".subject.break-all a");
            Element span = song.select(".haya-post-info-username .username").first();

            String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
            String songName = a.text();
            String artist = span.text();
            String artistId = span.attr("uid");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.HF);
            musicInfo.setId(songId);
            musicInfo.setName(songName);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }
}
