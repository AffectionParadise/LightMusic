package net.doge.sdk.service.mv.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
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
    private final String VIDEO_LZ_API = "https://www.lizhinb.com/ssp/";

    /**
     * 视频
     */
    public CommonResult<NetMvInfo> getVideo(String tag, int page, int limit) {
        List<NetMvInfo> r = new LinkedList<>();
        int t;
        String[] s = Tags.mvTags.get(tag);

        String param = s[TagModule.VIDEO_LZ];
        boolean isAll = StringUtil.isEmpty(param);
        String mvInfoBody = HttpRequest.get(VIDEO_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(mvInfoBody);
        Elements mvArray = doc.select(".nbvp-wall-card" + (isAll ? "" : String.format("[data-cats=\"%s\"]", param)));
        t = mvArray.size();
        for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
            Element mv = mvArray.get(i);
            Elements title = mv.select(".nbvp-wall-title");

            String id = RegexUtil.getGroup1("lz-video/(.*?)/", mv.attr("data-seo-url"));
            String mvName = title.text();
            String artistName = "李志";
            String coverImgUrl = mv.attr("data-poster");

            NetMvInfo mvInfo = new NetMvInfo();
            mvInfo.setSource(NetResourceSource.LZ);
            mvInfo.setId(id);
            mvInfo.setName(mvName);
            mvInfo.setArtist(artistName);
            mvInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                mvInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(mvInfo);
        }
        return new CommonResult<>(r, t);
    }
}
