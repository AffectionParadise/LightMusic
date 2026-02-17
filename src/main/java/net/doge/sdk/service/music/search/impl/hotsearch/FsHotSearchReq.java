package net.doge.sdk.service.music.search.impl.hotsearch;

import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class FsHotSearchReq {
    private static FsHotSearchReq instance;

    private FsHotSearchReq() {
    }

    public static FsHotSearchReq getInstance() {
        if (instance == null) instance = new FsHotSearchReq();
        return instance;
    }

    // 热搜 API (5sing)
    private final String HOT_SEARCH_FS_API = "http://search.5sing.kugou.com";

    /**
     * 获取热搜
     *
     * @return
     */
    public List<String> getHotSearch() {
        List<String> r = new LinkedList<>();

        String body = HttpRequest.get(HOT_SEARCH_FS_API)
                .executeAsStr();
        Elements hotkeys = Jsoup.parse(body).select(".hot_search a");
        for (int i = 0, len = hotkeys.size(); i < len; i++) {
            r.add(hotkeys.get(i).text());
        }
        return r;
    }
}
