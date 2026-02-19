package net.doge.sdk.service.playlist.rcmd.impl.recommendplaylist;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetPlaylistInfo;
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

public class FsRecommendPlaylistReq {
    private static FsRecommendPlaylistReq instance;

    private FsRecommendPlaylistReq() {
    }

    public static FsRecommendPlaylistReq getInstance() {
        if (instance == null) instance = new FsRecommendPlaylistReq();
        return instance;
    }

    // 分类歌单(最新) API (5sing)
    private final String NEW_PLAYLIST_FS_API = "http://5sing.kugou.com/gd/gdList?tagName=%s&page=%s&type=1";

    /**
     * 分类歌单(最新)
     */
    public CommonResult<NetPlaylistInfo> getNewPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.recPlaylistTags.get(tag);

        if (StringUtil.notEmpty(s[4])) {
            String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_FS_API, s[4].trim(), page))
                    .executeAsStr();
            Document doc = Jsoup.parse(playlistInfoBody);
            Elements as = doc.select("span.pagecon a");
            if (as.isEmpty()) t = limit;
            else t = Integer.parseInt(as.last().text()) * limit;
            Elements playlistArray = doc.select("li.item dl");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                Element elem = playlistArray.get(i);
                Elements a = elem.select(".jx_name.ellipsis a");
                Elements author = elem.select(".author a");
                Elements img = elem.select(".imgbox img");
                Elements lc = elem.select(".lcount");

                String playlistId = RegexUtil.getGroup1("dj/(.*?)\\.html", a.attr("href"));
                String playlistName = a.text();
                String creator = author.text();
                String creatorId = RegexUtil.getGroup1("/(\\d+)/dj", a.attr("href"));
                Long playCount = Long.parseLong(lc.text());
                String coverImgThumbUrl = img.attr("src");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.FS);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
