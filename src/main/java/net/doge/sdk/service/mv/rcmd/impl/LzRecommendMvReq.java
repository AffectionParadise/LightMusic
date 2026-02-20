package net.doge.sdk.service.mv.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetMvInfo;
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

public class LzRecommendMvReq {
    private static LzRecommendMvReq instance;

    private LzRecommendMvReq() {
    }

    public static LzRecommendMvReq getInstance() {
        if (instance == null) instance = new LzRecommendMvReq();
        return instance;
    }

    // 视频 API (李志)
    private final String VIDEO_LZ_API = "https://www.lizhinb.com/live-category/%s/";

    /**
     * 视频
     */
    public CommonResult<NetMvInfo> getVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagType.VIDEO_LZ];
        if (StringUtil.notEmpty(param)) {
            String mvInfoBody = HttpRequest.get(String.format(VIDEO_LZ_API, param))
                    .executeAsStr();
            Document doc = Jsoup.parse(mvInfoBody);
            Elements mvArray = doc.select(".tile-content");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                Element mv = mvArray.get(i);
                Elements a = mv.select(".tile-link.ajax-link");
                Elements hl = mv.select(".tile-headline");
                Elements img = mv.select("img");
                Elements time = mv.select(".tile-date");

                String id = RegexUtil.getGroup1("live/(.*?)/", a.attr("href"));
                String mvName = hl.text();
                String artistName = "李志";
                String coverImgUrl = img.attr("srcset").split(" ")[0];
                if (StringUtil.isEmpty(coverImgUrl)) coverImgUrl = img.attr("data-src");
                String pubTime = time.text();

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.LZ);
                mvInfo.setId(id);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPubTime(pubTime);
                String finalCoverImgUrl = coverImgUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(finalCoverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(mvInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
