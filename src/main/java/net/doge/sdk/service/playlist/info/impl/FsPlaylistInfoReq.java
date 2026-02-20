package net.doge.sdk.service.playlist.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class FsPlaylistInfoReq {
    private static FsPlaylistInfoReq instance;

    private FsPlaylistInfoReq() {
    }

    public static FsPlaylistInfoReq getInstance() {
        if (instance == null) instance = new FsPlaylistInfoReq();
        return instance;
    }

    // 歌单信息 API (5sing)
    private final String PLAYLIST_DETAIL_FS_API = "http://5sing.kugou.com/%s/dj/%s.html";

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        String id = playlistInfo.getId();
        String creatorId = playlistInfo.getCreatorId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_FS_API, creatorId, id))
                .executeAsStr();
        Document doc = Jsoup.parse(playlistInfoBody);

        String coverImgUrl = doc.select(".lt.w_30 img").attr("src");
        String description = doc.select("#normalIntro").first().ownText();
        StringJoiner sj = new StringJoiner("、");
        Elements elems = doc.select(".c_wap.tag_box label");
        elems.forEach(elem -> sj.add(elem.text()));
        String tag = sj.toString();

        if (!playlistInfo.hasCoverImgUrl()) playlistInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> playlistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        playlistInfo.setDescription(description);
        if (!playlistInfo.hasTag()) playlistInfo.setTag(tag);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = playlistInfo.getId();
        String creatorId = playlistInfo.getCreatorId();
        String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_FS_API, creatorId, id))
                .executeAsStr();
        Document doc = Jsoup.parse(playlistInfoBody);
        total = Integer.parseInt(RegexUtil.getGroup1("（(\\d+)）", doc.select("span.number").text()));
        Elements songArray = doc.select("li.p_rel");
        for (int i = (page - 1) * limit, len = Math.min(page * limit, songArray.size()); i < len; i++) {
            Element elem = songArray.get(i);
            Elements na = elem.select(".s_title.lt a");
            Elements aa = elem.select(".s_soner.lt a");

            String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?).html", na.attr("href")).replaceFirst("/", "_");
            String name = na.text();
            String artist = aa.text();
            String artistId = RegexUtil.getGroup1("http://5sing.kugou.com/(\\d+)", aa.attr("href"));

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.FS);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
