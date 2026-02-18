package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.media.DurationUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.List;

public class MeMusicInfoReq {
    private static MeMusicInfoReq instance;

    private MeMusicInfoReq() {
    }

    public static MeMusicInfoReq getInstance() {
        if (instance == null) instance = new MeMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (猫耳)
    private final String SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";
    // 歌曲专辑信息 API (猫耳)
    private final String SONG_ALBUM_DETAIL_ME_API = "https://www.missevan.com/dramaapi/getdramabysound?sound_id=%s";
    // 弹幕 API (猫耳)
    private final String DM_ME_API = "https://www.missevan.com/sound/getdm?soundid=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        // 歌曲信息
        Runnable fillMusicInfo = () -> {
            String songBody = HttpRequest.get(String.format(SONG_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("username"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(data.getString("user_id"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("front_cover"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        };
        // 专辑信息
        Runnable fillAlbumInfo = () -> {
            String albumBody = HttpRequest.get(String.format(SONG_ALBUM_DETAIL_ME_API, id))
                    .executeAsStr();
            String infoStr = JSONObject.parseObject(albumBody).getString("info");
            // 可能是字符串也可能是 json 对象，先判断
            if (!JsonUtil.isValidObject(infoStr)) return;
            JSONObject info = JSONObject.parseObject(infoStr);
            JSONObject albumData = info.getJSONObject("drama");
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumData.getString("name"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumData.getString("id"));
        };

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(fillMusicInfo);
        executor.submit(fillAlbumInfo);
        executor.await();
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        String id = musicInfo.getId();
        String dmBody = HttpRequest.get(String.format(DM_ME_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(dmBody);
        Elements elements = doc.select("d");
        // 限制弹幕数量，避免引发性能问题
        final int dmLimit = 300;
        List<Element> ds = elements.subList(0, Math.min(elements.size(), dmLimit));
        StringBuilder sb = new StringBuilder();
        for (Element d : ds) {
            double time = Double.parseDouble(d.attr("p").split(",", 2)[0]);
            sb.append(DurationUtil.formatToLyricTime(time));
            sb.append(d.text());
            sb.append("\n");
        }
        musicInfo.setLyric(sb.toString());
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
