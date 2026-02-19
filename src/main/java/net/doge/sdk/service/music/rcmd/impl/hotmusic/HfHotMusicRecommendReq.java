package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
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

public class HfHotMusicRecommendReq {
    private static HfHotMusicRecommendReq instance;

    private HfHotMusicRecommendReq() {
    }

    public static HfHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new HfHotMusicRecommendReq();
        return instance;
    }

    // 热歌 API (音乐磁场)
    private final String HOT_MUSIC_HF_API = "https://www.hifiti.com/%s-%s.htm";

    /**
     * 热门歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusic(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTags.get(tag);

        if (StringUtil.notEmpty(s[5])) {
            String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_HF_API, s[5], page))
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
            else t = songs.size();
            for (int i = hasTs ? 0 : (page - 1) * limit, len = hasTs ? songs.size() : Math.min(songs.size(), page * limit); i < len; i++) {
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
        }
        return new CommonResult<>(r, t);
    }
}
