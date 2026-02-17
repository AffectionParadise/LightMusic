package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.kg.KgLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;
import java.util.Map;

public class KgMusicInfoReq {
    private static KgMusicInfoReq instance;

    private KgMusicInfoReq() {
    }

    public static KgMusicInfoReq getInstance() {
        if (instance == null) instance = new KgMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (酷狗)
//    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    private final String SINGLE_SONG_DETAIL_KG_API_V2 = "/v2/get_res_privilege/lite";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String hash = musicInfo.getHash();
//            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, id))
//                    .cookie(SdkCommon.KG_COOKIE)
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
//            if (JsonUtil.notEmpty(data)) {
//                // 时长是毫秒，转为秒
//                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("timelength") / 1000);
//                if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(data));
//                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
//                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album_name"));
//                if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(data.getString("album_id"));
//                if (!musicInfo.hasAlbumImage()) {
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage albumImage = SdkUtil.getImageFromUrl(data.getString("img"));
//                        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
//                        ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
//                        musicInfo.callback();
//                    });
//                }
////                if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyrics"));
//            } else {
        // 歌曲信息接口有时返回为空，直接用 V2 版本接口，不过由于部分信息不完整，作为备选
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(SINGLE_SONG_DETAIL_KG_API_V2);
        String dat = String.format("{\"appid\":%s,\"area_code\":1,\"behavior\":\"play\",\"clientver\":%s,\"need_hash_offset\":1,\"relate\":1," +
                        "\"support_verify\":1,\"resource\":[{\"type\":\"audio\",\"page_id\":0,\"hash\":\"%s\",\"album_id\":0}]}",
                KugouReqBuilder.appid, KugouReqBuilder.clientver, hash);
        String songBody = SdkCommon.kgRequest(null, dat, options)
                .header(Header.CONTENT_TYPE, "application/json")
                .header("x-router", "media.store.kugou.com")
                .executeAsStr();
        JSONObject songData = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);
        JSONObject info = songData.getJSONObject("info");
        // 时长是毫秒，转为秒
        if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDouble("duration") / 1000);
        if (!musicInfo.hasArtist()) musicInfo.setArtist(songData.getString("singername"));
//                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(songData.getString("albumname"));
        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(songData.getString("recommend_album_id"));
        if (!musicInfo.hasAlbumImage()) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage albumImage = SdkUtil.getImageFromUrl(info.getString("image").replace("/{size}", ""));
                FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                musicInfo.callback();
            });
        }
//            }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        KgLyricReq.getInstance().fillLyric(musicInfo);
    }
}
