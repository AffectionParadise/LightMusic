package net.doge.sdk.service.music.menu.impl;

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

public class GgMusicMenuReq {
    private static GgMusicMenuReq instance;

    private GgMusicMenuReq() {
    }

    public static GgMusicMenuReq getInstance() {
        if (instance == null) instance = new GgMusicMenuReq();
        return instance;
    }

    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        List<NetMusicInfo> res = new LinkedList<>();
        int t;

        String id = netMusicInfo.getId();
        String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select("ul.text-middle.break-all li a");
        t = songs.size();
        for (int i = 0, len = songs.size(); i < len; i++) {
            Element song = songs.get(i);

            String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", song.attr("href"));
            String songName = song.text();

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.GG);
            musicInfo.setId(songId);
            musicInfo.setName(songName);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, t);
    }
}
