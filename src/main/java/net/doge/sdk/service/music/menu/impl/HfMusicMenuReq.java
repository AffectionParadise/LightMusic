package net.doge.sdk.service.music.menu.impl;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class HfMusicMenuReq {
    private static HfMusicMenuReq instance;

    private HfMusicMenuReq() {
    }

    public static HfMusicMenuReq getInstance() {
        if (instance == null) instance = new HfMusicMenuReq();
        return instance;
    }

    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifiti.com/thread-%s.htm";

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        List<NetMusicInfo> res = new LinkedList<>();
        int t;

        String id = netMusicInfo.getId();
        String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                .cookie(SdkCommon.HF_COOKIE)
                .executeAsStr();
        Document doc = Jsoup.parse(musicInfoBody);
        Elements songs = doc.select(".relate_post a");
        t = songs.size();
        for (int i = 0, len = songs.size(); i < len; i++) {
            Element song = songs.get(i);

            String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", song.attr("href"));
            String songName = song.text();

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.HF);
            musicInfo.setId(songId);
            musicInfo.setName(songName);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, t);
    }
}
