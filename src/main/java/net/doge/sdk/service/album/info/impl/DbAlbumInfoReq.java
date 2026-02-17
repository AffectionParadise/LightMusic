package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class DbAlbumInfoReq {
    private static DbAlbumInfoReq instance;

    private DbAlbumInfoReq() {
    }

    public static DbAlbumInfoReq getInstance() {
        if (instance == null) instance = new DbAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (豆瓣)
    private final String ALBUM_DETAIL_DB_API = "https://music.douban.com/subject/%s/";
    // 获取专辑照片 API (堆糖)
    private final String GET_ALBUMS_IMG_DT_API = "https://www.duitang.com/napi/vienna/blog/by_album/?album_id=%s&after_id=%s&limit=%s&_=%s";

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DB_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        String info = HtmlUtil.getPrettyText(doc.select("#info").first()) + "\n";
        Element re = doc.select("#link-report").first();
        Elements span = re.select("span");
        String desc = HtmlUtil.getPrettyText(span.isEmpty() ? re : span.last()) + "\n";
        String tracks = HtmlUtil.getPrettyText(doc.select(".track-list div div").first());
        String coverImgUrl = doc.select("#mainpic img").attr("src");

        albumInfo.setDescription(info + desc + "\n曲目：\n" + tracks);
        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
    }

    /**
     * 获取专辑照片链接
     */
    public CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        String id = albumInfo.getId();
        List<String> imgUrls = new LinkedList<>();
        cursor = UrlUtil.encodeAll(cursor);
        int total;

        String imgInfoBody = HttpRequest.get(String.format(GET_ALBUMS_IMG_DT_API, id, cursor, limit, System.currentTimeMillis()))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(imgInfoBody).getJSONObject("data");
        JSONArray imgs = data.getJSONArray("object_list");
        cursor = data.getString("after");
        total = page * limit;
        if (data.getIntValue("more") == 1) total++;
        else total = (page - 1) * limit + imgs.size();
        for (int i = 0, len = imgs.size(); i < len; i++) {
            JSONObject img = imgs.getJSONObject(i);
            imgUrls.add(img.getJSONObject("photo").getString("path"));
        }

        return new CommonResult<>(imgUrls, total, cursor);
    }
}
