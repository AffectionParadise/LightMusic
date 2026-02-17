package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class KwMusicSearchReq {
    private static KwMusicSearchReq instance;

    private KwMusicSearchReq() {
    }

    public static KwMusicSearchReq getInstance() {
        if (instance == null) instance = new KwMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (酷我)
//    private final String SEARCH_MUSIC_KW_API = "https://kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=%s&rn=%s&reqId=a52ed540-2fb5-11ee-bba2-0d6f963952a7&plat=web_www&from=&httpsStatus=1";
    private final String SEARCH_MUSIC_KW_API = "https://search.kuwo.cn/r.s?client=kt&all=%s&pn=%s&rn=%s&uid=794762570" +
            "&ver=kwplayer_ar_9.2.2.1&vipver=1&show_copyright_off=1&newver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1&issubtitle=1";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
//            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_MUSIC_KW_API, encodedKeyword, page, limit)).executeAsync();
//            // 有时候请求会崩，先判断是否请求成功
//            if (resp.isSuccessful()) {
//                String musicInfoBody = resp.body();
//                JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
//                JSONObject data = musicInfoJson.getJSONObject("data");
//                if (JsonUtil.notEmpty(data)) {
//                    t = data.getIntValue("total");
//                    JSONArray songArray = data.getJSONArray("list");
//                    if (JsonUtil.notEmpty(songArray)) {
//                        for (int i = 0, len = songArray.size(); i < len; i++) {
//                            JSONObject songJson = songArray.getJSONObject(i);
//
//                            String songId = songJson.getString("rid");
//                            String songName = StringUtil.removeHTMLLabel(songJson.getString("name"));
//                            String artist = StringUtil.removeHTMLLabel(songJson.getString("artist")).replace("&", "、");
//                            String artistId = songJson.getString("artistid");
//                            String albumName = StringUtil.removeHTMLLabel(songJson.getString("album"));
//                            String albumId = songJson.getString("albumid");
//                            Double duration = songJson.getDouble("duration");
//                            String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;
//
//                            NetMusicInfo musicInfo = new NetMusicInfo();
//                            musicInfo.setSource(NetMusicSource.KW);
//                            musicInfo.setId(songId);
//                            musicInfo.setName(songName);
//                            musicInfo.setArtist(artist);
//                            musicInfo.setArtistId(artistId);
//                            musicInfo.setAlbumName(albumName);
//                            musicInfo.setAlbumId(albumId);
//                            musicInfo.setDuration(duration);
//                            musicInfo.setMvId(mvId);
//
//                            r.add(musicInfo);
//                        }
//                    }
//                }
//            }
        String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_KW_API, encodedKeyword, page - 1, limit))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        if (JsonUtil.notEmpty(musicInfoJson)) {
            t = musicInfoJson.getIntValue("TOTAL");
            JSONArray songArray = musicInfoJson.getJSONArray("abslist");
            if (JsonUtil.notEmpty(songArray)) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("DC_TARGETID");
                    String songName = songJson.getString("SONGNAME");
                    String artist = songJson.getString("ARTIST").replace("&", "、");
                    String artistId = songJson.getString("ARTISTID");
                    String albumName = songJson.getString("ALBUM");
                    String albumId = songJson.getString("ALBUMID");
                    Double duration = songJson.getDouble("DURATION");
                    String mvId = songJson.getIntValue("MVFLAG") == 0 ? "" : songId;
                    String mInfo = songJson.getString("N_MINFO");
                    int qualityType = AudioQuality.UNKNOWN;
                    if (mInfo.contains("bitrate:4000")) qualityType = AudioQuality.HR;
                    else if (mInfo.contains("bitrate:2000")) qualityType = AudioQuality.SQ;
                    else if (mInfo.contains("bitrate:320")) qualityType = AudioQuality.HQ;
                    else if (mInfo.contains("bitrate:128")) qualityType = AudioQuality.LQ;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KW);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);
                    musicInfo.setQualityType(qualityType);

                    r.add(musicInfo);
                }
            }
        }

        return new CommonResult<>(r, t);
    }
}
