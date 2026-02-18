package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;

public class GgMusicInfoReq {
    private static GgMusicInfoReq instance;

    private GgMusicInfoReq() {
    }

    public static GgMusicInfoReq getInstance() {
        if (instance == null) instance = new GgMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (咕咕咕音乐)
    private final String SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_GG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(songBody);
        String dataStr = RegexUtil.getGroup1("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html());
        if (StringUtil.notEmpty(dataStr)) {
            dataStr = dataStr.replaceFirst("base64_decode\\(\"(.*?)\"\\)", "\"\"");
            // json 字段带引号
            dataStr = dataStr.replaceAll(" (\\w+):", "'$1':");
        }
        JSONObject data = JSONObject.parseObject(dataStr);

        Elements a = doc.select(".m-3.text-center h5 a");

        if (!musicInfo.hasArtist()) musicInfo.setArtist(a.text());
        if (!musicInfo.hasArtistId())
            musicInfo.setArtistId(RegexUtil.getGroup1("user-(\\d+)\\.htm", a.attr("href")));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                String picUrl = data.getString("cover");
                if (StringUtil.isEmpty(picUrl)) picUrl = data.getString("pic");
                if (StringUtil.notEmpty(picUrl)) {
                    if (picUrl.contains("music.126.net"))
                        picUrl = picUrl.replaceFirst("param=\\d+y\\d+", "param=500y500");
                    else if (picUrl.contains("y.gtimg.cn"))
                        picUrl = picUrl.replaceFirst("300x300", "500x500");
                    if (!picUrl.startsWith("http")) picUrl = "http://www.gggmusic.com/" + picUrl;
                }
                BufferedImage albumImage = SdkUtil.getImageFromUrl(picUrl);
                FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                musicInfo.callback();
            });
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        String id = musicInfo.getId();
        String songBody = HttpRequest.get(String.format(SONG_DETAIL_GG_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(songBody);
        Elements ps = doc.select(".message.break-all p");
        StringBuilder sb = new StringBuilder();
        for (Element p : ps) {
            String lyric = p.text().trim();
            if (StringUtil.isEmpty(lyric)) continue;
            sb.append(lyric);
            sb.append('\n');
        }
        musicInfo.setLyric(sb.toString());
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
