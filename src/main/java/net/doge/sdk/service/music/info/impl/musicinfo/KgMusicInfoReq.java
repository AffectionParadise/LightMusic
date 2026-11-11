package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.builder.KgReqBuilder;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.sdk.common.opt.kg.KgReqOptsBuilder;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.kg.KgLyricReq;
import net.doge.sdk.util.SdkUtil;
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
//    private final String SONG_DETAIL_KG_API = "/v3/album_audio/audio";
    private final String SONG_DETAIL_KG_API_V2 = "/v2/get_res_privilege/lite";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String hash = musicInfo.getHash();
//        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(SONG_DETAIL_KG_API);
//        String dat = String.format("{\"area_code\":\"1\",\"show_privilege\":1,\"show_album_info\":\"1\",\"is_publish\":\"\",\"appid\":%s,\"clientver\":%s," +
//                        "\"mid\":\"%s\",\"dfid\":\"%s\",\"clienttime\":%s,\"key\":\"%s\",\"fields\":\"album_info,author_name,audio_info,ori_audio_name,base,songname,classification\"," +
//                        "\"data\":[\"%s\"]}",
//                KugouReqBuilder.appid, KugouReqBuilder.clientver, KugouReqBuilder.mid, KugouReqBuilder.dfid, System.currentTimeMillis() / 1000,
//                KugouReqBuilder.androidSignKey, hash);
//        String songBody = SdkCommon.kgRequest(null, dat, options)
//                .userAgent("Android712-AndroidPhone-11451-376-0-FeeCacheUpdate-wifi")
//                .header("KG-THash", "13a3164")
//                .header("KG-RC", "1")
//                .header("KG-Fake", "0")
//                .header("KG-RF", "00869891")
//                .header("x-router", "kmr.service.kugou.com")
//                .executeAsStr();
//        JSONObject songData = JSONObject.parseObject(songBody).getJSONArray("data").getJSONObject(0);
//        JSONObject info = songData.getJSONObject("info");
//        // 时长是毫秒，转为秒
//        if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDouble("duration") / 1000);
//        if (!musicInfo.hasArtist()) musicInfo.setArtist(songData.getString("singername"));
////                if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(data));
//        if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(songData.getString("albumname"));
//        if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(songData.getString("recommend_album_id"));
//        if (!musicInfo.hasAlbumImage()) {
//            GlobalExecutors.imageExecutor.execute(() -> {
//                BufferedImage albumImage = SdkUtil.getImageFromUrl(info.getString("image").replace("/{size}", ""));
//                FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
//                ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
//                musicInfo.callback();
//            });
//        }
        // 歌曲信息接口有时返回为空，直接用 V2 版本接口，不过由于部分信息不完整，作为备选
        Map<KgReqOptEnum, Object> options = KgReqOptsBuilder.androidPost(SONG_DETAIL_KG_API_V2);
        String dat = String.format("{\"appid\":%s,\"area_code\":1,\"behavior\":\"play\",\"clientver\":%s,\"need_hash_offset\":1,\"relate\":1," +
                        "\"support_verify\":1,\"resource\":[{\"type\":\"audio\",\"page_id\":0,\"hash\":\"%s\",\"album_id\":0}]}",
                KgReqBuilder.appid, KgReqBuilder.clientver, hash);
        String songBody = SdkCommon.kgRequest(null, dat, options)
                .contentType("application/json")
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
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        KgLyricReq.getInstance().fillLyric(musicInfo);
    }
}
