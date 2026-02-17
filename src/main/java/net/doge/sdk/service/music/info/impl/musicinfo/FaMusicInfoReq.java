package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.net.UrlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;

public class FaMusicInfoReq {
    private static FaMusicInfoReq instance;

    private FaMusicInfoReq() {
    }

    public static FaMusicInfoReq getInstance() {
        if (instance == null) instance = new FaMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (发姐)
    private final String SINGLE_SONG_DETAIL_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";
    // 歌词 API (发姐)
    private final String LYRIC_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&scope=remote_lyric&id=%s";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        // 获取发姐请求参数
        String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                .executeAsStr();
        Document doc = Jsoup.parse(body);
        Elements ap = doc.select("#aplayer1");
        String musicSet = UrlUtil.encodeAll(ap.attr("data-songs"));
        String _nonce = ap.attr("data-_nonce");

        String songInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_FA_API, musicSet, _nonce))
                .executeAsStr();
        JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
        JSONObject data = songInfoJson.getJSONObject("msg");
        JSONArray songArray = data.getJSONArray("songs");
        for (int i = 0, s = songArray.size(); i < s; i++) {
            JSONObject songJson = songArray.getJSONObject(i);
            if (!id.equals(songJson.getString("id"))) continue;
            if (!musicInfo.hasArtist()) musicInfo.setArtist(songJson.getString("author"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("pic"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            break;
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        String id = musicInfo.getId();
        String lyricBody = HttpRequest.get(String.format(LYRIC_FA_API, id))
                .executeAsStr();
        musicInfo.setLyric(lyricBody);
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
