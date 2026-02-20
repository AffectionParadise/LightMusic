package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
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

public class FsHighQualityPlaylistReq {
    private static FsHighQualityPlaylistReq instance;

    private FsHighQualityPlaylistReq() {
    }

    public static FsHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new FsHighQualityPlaylistReq();
        return instance;
    }

    // 分类歌单(最热) API (5sing)
    private final String HOT_PLAYLIST_FS_API = "http://5sing.kugou.com/gd/gdList?tagName=%s&page=%s&type=0";

    /**
     * 分类歌单(最热)
     */
    public CommonResult<NetPlaylistInfo> getHotPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        String param = s[TagType.HOT_PLAYLIST_FS];
        if (StringUtil.notEmpty(param)) {
            String playlistInfoBody = HttpRequest.get(String.format(HOT_PLAYLIST_FS_API, param.trim(), page))
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
                playlistInfo.setSource(NetResourceSource.FS);
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
