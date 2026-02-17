package net.doge.sdk.service.music.info.impl.musicinfo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.service.music.info.impl.musicinfo.lyric.nc.NcLyricReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.json.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.Map;

public class NcMusicInfoReq {
    private static NcMusicInfoReq instance;

    private NcMusicInfoReq() {
    }

    public static NcMusicInfoReq getInstance() {
        if (instance == null) instance = new NcMusicInfoReq();
        return instance;
    }

    // 歌曲信息 API (网易云)
    private final String SINGLE_SONG_DETAIL_NC_API = "https://music.163.com/api/v3/song/detail";
    // 节目信息 API (网易云)
    private final String SINGLE_PROGRAM_DETAIL_NC_API = "https://music.163.com/api/dj/program/detail";

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        boolean isProgram = musicInfo.isProgram();
        if (isProgram) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_PROGRAM_DETAIL_NC_API, String.format("{\"id\":\"%s\"}", musicInfo.getProgramId()), options)
                    .executeAsStr();
            JSONObject songJson = JSONObject.parseObject(songBody).getJSONObject("program");
            JSONObject dj = songJson.getJSONObject("dj");

            if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("duration") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(dj.getString("nickname"));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(dj.getString("userId"));
            if (!musicInfo.hasAlbumName())
                musicInfo.setAlbumName(songJson.getJSONObject("radio").getString("name"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(songJson.getJSONObject("radio").getString("id"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(songJson.getString("coverUrl"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        } else {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String songBody = SdkCommon.ncRequest(Method.POST, SINGLE_SONG_DETAIL_NC_API, String.format("{\"c\":\"[{'id':'%s'}]\"}", id), options)
                    .executeAsStr();
            JSONArray array = JSONObject.parseObject(songBody).getJSONArray("songs");
            if (JsonUtil.isEmpty(array)) return;
            JSONObject songJson = array.getJSONObject(0);
            JSONObject albumJson = songJson.getJSONObject("al");

            if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("dt") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(SdkUtil.parseArtist(songJson));
            if (!musicInfo.hasArtistId()) musicInfo.setArtistId(SdkUtil.parseArtistId(songJson));
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(albumJson.getString("name"));
            if (!musicInfo.hasAlbumId()) musicInfo.setAlbumId(albumJson.getString("id"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage albumImage = SdkUtil.getImageFromUrl(albumJson.getString("picUrl"));
                    FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
                    ImageUtil.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        if (musicInfo.isLyricIntegrated()) return;
        NcLyricReq.getInstance().fillLyric(musicInfo);
    }
}
