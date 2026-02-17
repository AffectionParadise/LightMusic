package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HfMusicUrlReq {
    private static HfMusicUrlReq instance;

    private HfMusicUrlReq() {
    }

    public static HfMusicUrlReq getInstance() {
        if (instance == null) instance = new HfMusicUrlReq();
        return instance;
    }

    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifiti.com/thread-%s.htm";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                .cookie(SdkCommon.HF_COOKIE)
                .executeAsStr();
        Document doc = Jsoup.parse(songBody);
        String dataStr = RegexUtil.getGroup1("audio:\\[.*?(\\{.*?\\}).*?\\]", doc.html());
        if (StringUtil.notEmpty(dataStr)) {
            // json 字段带引号
            JSONObject data = JSONObject.parseObject(dataStr.replaceAll("(\\w+):'(.*?)'", "'$1':'$2'"));
            String url = UrlUtil.encodeBlank(data.getString("url"));
            if (url.startsWith("http")) return url;
            return SdkUtil.getRedirectUrl("https://www.hifiti.com/" + url);
        }
        return "";
    }
}
