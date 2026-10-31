package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.service.source.NetResourceSource;
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
    private final String SEARCH_MUSIC_QS_API = "https://api.qishui.com/luna/search/track?device_platform=android&os=android&ssmix=a&cdid=46556f98-1720-4248-83da-62b74b60b46a" +
            "&channel=xiaomi_8478_64&aid=386088&app_name=luna&version_code=100198030&version_name=19.8.0&manifest_version_code=100198030&update_version_code=100198030" +
            "&resolution=1080*1920&dpi=480&device_type=ABR-AL80&device_brand=HUAWEI&language=zh&os_api=35&os_version=15&ac=wifi&device_model=ABR-AL80&save_power=0&font_size=1.00" +
            "&luna_first_launch_apk_type=normal_apk&diversion_channel_name=xiaomi_8478_64&is_car_play=0&battery=0.99&network_speed=10156&hybrid_version_code=100198030" +
            "&tz_name=Asia/Shanghai&tz_offset=28800&luna_register_time=1784311292&diversion_category_level_two=Xiaomi商店-自然&package=com.luna.music" +
            "&charge=0&luna_apk_type=normal_apk&output_device_type=Phone&volume=1.00&brightness=0.08&need_personal_recommend=1&is_teen_mode=0&sim_region=cn" +
            "&diversion_category_level_one=厂商商店-自然&android_device_type=default&iid=2204957404569386&device_id=2204957404565290&_rticket=%s&q=%s&cursor=%s&count=%s";

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;

        // 对关键词编码
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        // 每页固定 20 条
        String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_QS_API, System.currentTimeMillis(), encodedKeyword, (page - 1) * limit, limit))
                .executeAsStr();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        JSONArray resultGroups = musicInfoJson.getJSONArray("result_groups");
        if (JsonUtil.notEmpty(resultGroups)) {
            JSONObject result = resultGroups.getJSONObject(0);
            JSONArray songArray = result.getJSONArray("data");
            t = page * limit + (result.getBooleanValue("has_more") ? 1 : 0);
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
                musicInfo.setSource(NetResourceSource.QS);
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
