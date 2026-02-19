package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.net.UrlUtil;

import java.util.LinkedList;
import java.util.List;

public class QsMusicSearchReq {
    private static QsMusicSearchReq instance;

    private QsMusicSearchReq() {
    }

    public static QsMusicSearchReq getInstance() {
        if (instance == null) instance = new QsMusicSearchReq();
        return instance;
    }

    // 关键词搜索歌曲 API (汽水)
    private final String SEARCH_MUSIC_QS_API = "https://api.qishui.com/luna/pc/search/track?aid=386088&app_name=luna_pc&region=cn&geo_region=cn&os_region=cn&sim_region=" +
            "&device_id=1088932190113307&cdid=&iid=2332504177791808&version_name=3.0.0&version_code=30000000&channel=official&build_mode=master&network_carrier=&ac=wifi" +
            "&tz_name=Asia/Shanghai&resolution=&device_platform=windows&device_type=Windows&os_version=Windows+11+Home+China&fp=1088932190113307&q=%s&cursor=%s" +
            "&search_id=4ee2bc52-db9b-42c3-85cf-cdac2fe02efe&search_method=input&debug_params=&from_search_id=1aa21093-d49e-4d29-b6c7-548b170d12a0&search_scene=";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        // 对关键词编码
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        // 每页固定 20 条
        String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_QS_API, encodedKeyword, (page - 1) * 20))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONObject result = musicInfoJson.getJSONArray("result_groups").getJSONObject(0);
        JSONArray songArray = result.getJSONArray("data");
        t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
        if (JsonUtil.notEmpty(songArray)) {
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("entity").getJSONObject("track");
                JSONObject albumJson = songJson.getJSONObject("album");

                String songId = songJson.getString("id");
                String songName = songJson.getString("name");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("duration") / 1000;
                String mvId = songJson.getString("vid");
                int qualityType = AudioQuality.UNKNOWN;
                JSONArray bitRates = songJson.getJSONArray("bit_rates");
                if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "hi_res")))
                    qualityType = AudioQuality.HR;
                else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "lossless")))
                    qualityType = AudioQuality.SQ;
                else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "highest")))
                    qualityType = AudioQuality.HQ;
                else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "higher")))
                    qualityType = AudioQuality.MQ;
                else if (JsonUtil.notEmpty(SdkUtil.findFeatureObj(bitRates, "quality", "medium")))
                    qualityType = AudioQuality.LQ;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QS);
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
        return new CommonResult<>(r, t);
    }
}
