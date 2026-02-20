package net.doge.sdk.service.mv.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.LangUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class FaRecommendMvReq {
    private static FaRecommendMvReq instance;

    private FaRecommendMvReq() {
    }

    public static FaRecommendMvReq getInstance() {
        if (instance == null) instance = new FaRecommendMvReq();
        return instance;
    }

    // 视频 API (发姐)
    private final String VIDEO_FA_API = "https://www.chatcyf.com/video/page/%s/?c2=%s&c3&c4&t";
    // 直播 API (发姐)
    private final String LIVE_FA_API = "https://www.chatcyf.com/teaparty/page/%s/?c2=%s&c3&c4&t";

    /**
     * 视频
     */
    public CommonResult<NetMvInfo> getVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.VIDEO_FA];
        String[] sp = param.split(" ", -1);
        if (StringUtil.notEmpty(sp[0]) || StringUtil.isEmpty(sp[1])) {
            String mvInfoBody = HttpRequest.get(String.format(VIDEO_FA_API, page, sp[0]))
                    .executeAsStr();
            Document doc = Jsoup.parse(mvInfoBody);
            Elements as = doc.select(".pagination ul li");
            if (as.isEmpty()) t = limit;
            else {
                for (int i = as.size() - 1; i >= 0; i--) {
                    String ts = as.get(i).text();
                    if (!StringUtil.isNumber(ts)) continue;
                    t = Integer.parseInt(ts) * limit;
                    break;
                }
            }
            Elements mvArray = doc.select(".post.list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                Element mv = mvArray.get(i);
                Elements a = mv.select(".con h3 a");
                Elements author = mv.select(".author a");
                Elements img = mv.select(".img img");
                Elements views = mv.select(".views");
                Elements time = mv.select(".time");

                String id = RegexUtil.getGroup1("topics/(\\d+)/", a.attr("href"));
                String mvName = a.text();
                String artistName = author.text();
                String coverImgUrl = img.attr("src");
                Long playCount = LangUtil.parseNumber(views.text());
                String pubTime = time.text().trim();

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.FA);
                mvInfo.setId(id);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 直播
     */
    public CommonResult<NetMvInfo> getLive(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.VIDEO_FA];
        String[] sp = param.split(" ", -1);
        if (StringUtil.notEmpty(sp[1]) || StringUtil.isEmpty(sp[0])) {
            String mvInfoBody = HttpRequest.get(String.format(LIVE_FA_API, page, sp[1]))
                    .executeAsStr();
            Document doc = Jsoup.parse(mvInfoBody);
            Elements as = doc.select(".pagination ul li");
            if (as.isEmpty()) t = limit;
            else {
                for (int i = as.size() - 1; i >= 0; i--) {
                    String ts = as.get(i).text();
                    if (!StringUtil.isNumber(ts)) continue;
                    t = Integer.parseInt(ts) * limit;
                    break;
                }
            }
            Elements mvArray = doc.select(".post.list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                Element mv = mvArray.get(i);
                Elements a = mv.select(".con h3 a");
                Elements author = mv.select(".author a");
                Elements img = mv.select(".img img");
                Elements views = mv.select(".views");
                Elements time = mv.select(".time");

                String id = RegexUtil.getGroup1("topics/(\\d+)/", a.attr("href"));
                String mvName = a.text();
                String artistName = author.text();
                String coverImgUrl = img.attr("src");
                Long playCount = LangUtil.parseNumber(views.text());
                String pubTime = time.text().trim();

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetResourceSource.FA);
                mvInfo.setId(id);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}