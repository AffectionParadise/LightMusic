package net.doge.sdk.service.mv.info.impl.mvurl;

import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FaMvUrlReq {
    private static FaMvUrlReq instance;

    private FaMvUrlReq() {
    }

    public static FaMvUrlReq getInstance() {
        if (instance == null) instance = new FaMvUrlReq();
        return instance;
    }

    // 视频链接获取 API (发姐)
    private final String VIDEO_URL_FA_API = "https://www.chatcyf.com/topics/%s/";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(VIDEO_URL_FA_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(mvBody);
        Elements vs = doc.select("video source");
        if (vs.isEmpty()) vs = doc.select("video");
        if (!vs.isEmpty()) return UrlUtil.encodeBlank(vs.attr("src"));
        return "";
    }
}
