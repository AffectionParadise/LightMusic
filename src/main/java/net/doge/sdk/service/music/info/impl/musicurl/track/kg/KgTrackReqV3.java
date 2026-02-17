//package net.doge.sdk.entity.music.info.trackhero.kg;
//
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.sdk.common.SdkCommon;
//import net.doge.sdk.common.builder.KugouReqBuilder;
//import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
//import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
//import net.doge.util.common.CryptoUtil;
//import net.doge.util.common.JsonUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class KgTrackHeroV3 {
//    private static KgTrackHeroV3 instance;
//
//    private KgTrackHeroV3() {
//        initMap();
//    }
//
//    public static KgTrackHeroV3 getInstance() {
//        if (instance == null) instance = new KgTrackHeroV3();
//        return instance;
//    }
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        qualityMap.put("piano", "magic_piano");
//        qualityMap.put("acappella", "magic_acappella");
//        qualityMap.put("subwoofer", "magic_subwoofer");
//        qualityMap.put("ancient", "magic_ancient");
//        qualityMap.put("dj", "magic_dj");
//        qualityMap.put("surnay", "magic_surnay");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "high");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "viper_atmos");
//    }
//
//    /**
//     * 获取酷狗歌曲 url
//     *
//     * @param hash    歌曲 Hash
//     * @param quality 音质(
//     *                piano：手机端魔法音乐 钢琴
//     *                acappella：手机端魔法音乐 人声 伴奏
//     *                subwoofer：手机端魔法音乐 乐器
//     *                ancient：手机端魔法音乐 尤克里里
//     *                dj：手机端魔法音乐 DJ
//     *                surnay：手机端魔法音乐 唢呐
//     *                128k 320k flac hires
//     *                master：蝰蛇全景声
//     *                )
//     * @return
//     */
//    public String getTrackUrl(String hash, String quality) {
//        // 该接口只返回试听
//        Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost("http://tracker.kugou.com/v6/priv_url");
//        String key = CryptoUtil.md5(hash + "185672dd44712f60bb1736df5a377e82" + KugouReqBuilder.appid + CryptoUtil.md5(KugouReqBuilder.dfid) + KugouReqBuilder.userid);
//        String data = String.format("{\"area_code\":\"1\",\"behavior\":\"play\",\"qualities\":[\"128\",\"320\",\"flac\",\"high\",\"multitrack\",\"viper_atmos\",\"viper_tape\",\"viper_clear\"]," +
//                        "\"resource\":{\"album_audio_id\":\"\",\"collect_list_id\":\"3\",\"collect_time\":%s,\"hash\":\"%s\",\"id\":0,\"page_id\":1,\"type\":\"audio\"},\"token\":\"\"," +
//                        "\"tracker_param\":{\"all_m\":1,\"auth\":\"\",\"is_free_part\":0,\"key\":\"%s\",\"module_id\":0,\"need_climax\":1,\"need_xcdn\":1,\"open_time\":\"\"," +
//                        "\"pid\":\"411\",\"pidversion\":\"3001\",\"priv_vip_type\":\"6\",\"viptoken\":\"\"},\"userid\":\"%s\",\"vip\":0}",
//                System.currentTimeMillis(), hash, key, KugouReqBuilder.userid);
//        JSONObject.parseObject(data);
//        String respBody = SdkCommon.kgRequest(null, data, options)
//                .executeAsync()
//                .body();
//        JSONObject urlJson = JSONObject.parseObject(respBody);
//        JSONArray urlArray = urlJson.getJSONArray("data").getJSONObject(0).getJSONObject("info").getJSONArray("tracker_url");
//        if (JsonUtil.notEmpty(urlArray)) return urlArray.getString(0);
//        return "";
//    }
//
/// /    public static void main(String[] args) {
/// /        System.out.println(getInstance().getTrackUrl("CAD5C392FA6E6BFC2F76F5905BC027ED", AudioQuality.KEYS[net.doge.constant.core.media.AudioQuality.STANDARD]));
/// /    }
//}
