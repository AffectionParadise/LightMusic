package net.doge.sdk.service.music.info.trackhero.kg;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.util.core.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class KgTrackHeroV2 {
    private static KgTrackHeroV2 instance;

    private KgTrackHeroV2() {
        initMap();
    }

    public static KgTrackHeroV2 getInstance() {
        if (instance == null) instance = new KgTrackHeroV2();
        return instance;
    }

    // 歌曲 URL 获取 API (酷狗)
    private final String SONG_URL_KG_API = "/v5/url";

    private Map<String, String> qualityMap = new HashMap<>();

    private void initMap() {
        qualityMap.put("piano", "magic_piano");
        qualityMap.put("acappella", "magic_acappella");
        qualityMap.put("subwoofer", "magic_subwoofer");
        qualityMap.put("ancient", "magic_ancient");
        qualityMap.put("dj", "magic_dj");
        qualityMap.put("surnay", "magic_surnay");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "high");
        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "viper_atmos");
    }

    /**
     * 获取酷狗歌曲 url
     *
     * @param hash    歌曲 Hash
     * @param quality 音质(
     *                piano：手机端魔法音乐 钢琴
     *                acappella：手机端魔法音乐 人声 伴奏
     *                subwoofer：手机端魔法音乐 乐器
     *                ancient：手机端魔法音乐 尤克里里
     *                dj：手机端魔法音乐 DJ
     *                surnay：手机端魔法音乐 唢呐
     *                standard hq lossless hires
     *                atmosphere：蝰蛇全景声
     *                )
     * @return
     */
    public String getTrackUrl(String hash, String quality) {
        Map<String, Object> params = new TreeMap<>();
        params.put("album_audio_id", 0);
        params.put("area_code", 1);
        params.put("hash", hash);
        params.put("vipType", 0);
        params.put("vipToken", 0);
        params.put("behavior", "play");
        params.put("pid", 2);
        params.put("cmd", 26);
        params.put("version", 11709);
        params.put("pidversion", 3001);
        params.put("IsFreePart", 0);
        params.put("album_id", 0);
        params.put("ssa_flag", "is_fromtrack");
        params.put("page_id", 151369488);
        params.put("quality", qualityMap.get(quality));
        params.put("ppage_id", "463467626,350369493,788954147");
        params.put("cdnBackup", 1);
        params.put("kcard", 0);
        params.put("module", "collection");
        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGetWithKey(SONG_URL_KG_API);
        String respBody = SdkCommon.kgRequest(params, null, options)
                .header("x-router", "trackercdn.kugou.com")
                .executeAsStr();
        JSONObject urlJson = JSONObject.parseObject(respBody);
        JSONArray urlArray = urlJson.getJSONArray("url");
        if (JsonUtil.notEmpty(urlArray)) return urlArray.getString(0);
        return "";
    }

//    public static void main(String[] args) {
//        System.out.println(getInstance().getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//    }
}
