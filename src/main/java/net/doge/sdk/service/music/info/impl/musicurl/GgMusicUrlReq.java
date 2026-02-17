package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GgMusicUrlReq {
    private static GgMusicUrlReq instance;

    private GgMusicUrlReq() {
    }

    public static GgMusicUrlReq getInstance() {
        if (instance == null) instance = new GgMusicUrlReq();
        return instance;
    }

    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(songBody);
        String dataStr = RegexUtil.getGroup1("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html());
        if (StringUtil.notEmpty(dataStr)) {
            String base64Pattern = "base64_decode\\(\"(.*?)\"\\)";
            String base64Str = RegexUtil.getGroup1(base64Pattern, dataStr);
            if (StringUtil.notEmpty(base64Str))
                dataStr = dataStr.replaceFirst(base64Pattern, String.format("\"%s\"", CryptoUtil.base64Decode(base64Str)));

            // json 字段带引号
            JSONObject data = JSONObject.parseObject(dataStr.replaceAll(" (\\w+):", "'$1':"));
            String url = UrlUtil.encodeBlank(data.getString("url"));
            if (url.startsWith("http")) return url;
            else {
                // 获取重定向之后的 url
                String startUrl = "http://www.gggmusic.com" + url;
                String newUrl = SdkUtil.getRedirectUrl(startUrl);
                return StringUtil.isEmpty(newUrl) ? startUrl : newUrl;
            }
        }
        return "";
    }
}
