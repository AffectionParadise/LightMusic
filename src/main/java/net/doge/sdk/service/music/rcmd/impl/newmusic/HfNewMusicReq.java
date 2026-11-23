package net.doge.sdk.service.music.rcmd.impl.newmusic;

import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HfNewMusicReq {
    private static HfNewMusicReq instance;

    private HfNewMusicReq() {
    }

    public static HfNewMusicReq getInstance() {
        if (instance == null) instance = new HfNewMusicReq();
        return instance;
    }

    // 推荐新歌 API (音乐磁场)
    private final String RECOMMEND_NEW_MUSIC_HF_API = "https://www.hifiti.net/%s-%s.htm";

    /**
     * 推荐新歌
     */
    public CommonResult<NetMusicInfo> getRecommendNewSong(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.newSongTags.get(tag);

        String param = s[TagModule.RECOMMEND_NEW_SONG_HF];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("orderby", "tid");
            String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_MUSIC_HF_API, param, page))
                    .queryParams(params)
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
                musicInfo.setSource(NetResourceSource.HF);
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
