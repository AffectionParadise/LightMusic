package net.doge.sdk.service.music.rcmd.impl.hotmusic;

import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
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

public class GgHotMusicRecommendReq {
    private static GgHotMusicRecommendReq instance;

    private GgHotMusicRecommendReq() {
    }

    public static GgHotMusicRecommendReq getInstance() {
        if (instance == null) instance = new GgHotMusicRecommendReq();
        return instance;
    }

    // 热歌 API (咕咕咕音乐)
    private final String HOT_MUSIC_GG_API = "http://www.gggmusic.com/%s-%s.htm";

    /**
     * 热门歌曲
     */
    public CommonResult<NetMusicInfo> getHotMusic(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotSongTag.get(tag);

        if (StringUtil.notEmpty(s[6])) {
            String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_GG_API, s[6], page))
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

                String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
                String songName = a.text();

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.GG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
