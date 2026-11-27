//package net.doge.sdk.service.music.info.impl.musicurl.track.qq;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.RegexUtil;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.json.JsonUtil;
//import net.doge.util.core.log.LogUtil;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class GrassQqTrackReq {
//    private static GrassQqTrackReq instance;
//
//    private GrassQqTrackReq() {
//        initMap();
//    }
//
//    public static GrassQqTrackReq getInstance() {
//        if (instance == null) instance = new GrassQqTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (QQ)
//    private final String SONG_URL_QQ_API = "http://97.64.37.235/grass/v1/url/kw/%s/%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//    // url 黑名单
//    private Set<String> urlBlacklist = new HashSet<>();
//
//    private void initMap() {
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "128k");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "320k");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "320k");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "flac");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "flac24bit");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "flac24bit");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "flac24bit");
//    }
//
//    private static final Pattern TAG_PART = Pattern.compile("(?:[0-9][A-Za-z0-9_])+");
//    private static final char[] HEX = "0123456789abcdef".toCharArray();
//
//    public static String buildTag(String path) {
//        Matcher matcher = TAG_PART.matcher(path);
//        StringBuilder json = new StringBuilder();
//
//        // 复现 JSON.stringify(parts, null, 1) 的换行和缩进
//        while (matcher.find()) {
//            json.append(json.length() == 0 ? "[\n" : ",\n");
//            json.append(" \"").append(matcher.group()).append('"');
//        }
//
//        // JavaScript 的 match() 无匹配时返回 null
//        json.append(json.length() == 0 ? "null" : "\n]");
//
//        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
//        StringBuilder tag = new StringBuilder(bytes.length * 2);
//        for (byte value : bytes) {
//            int unsigned = value & 0xff;
//            tag.append(HEX[unsigned >>> 4]).append(HEX[unsigned & 0x0f]);
//        }
//        return tag.toString();
//    }
//
//    /**
//     * 获取 QQ 音乐歌曲链接
//     *
//     * @param mid     歌曲 mid
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        try {
//            String url = String.format(SONG_URL_QQ_API, mid, qualityMap.get(quality));
//            String songBody = HttpRequest.get(url)
//                    .userAgent("lx-music/desktop")
//                    .header("ver", "2.0.0")
//                    .header("source-ver", "1")
//                    .header("tag", buildTag(RegexUtil.getGroup0("/url.*", url)))
//                    .executeAsStr();
//            JSONObject songJson = JSONObject.parseObject(songBody);
//            if (songJson.getIntValue("code") != 0) return "";
//            JSONObject data = songJson.getJSONObject("data");
//            if (JsonUtil.isEmpty(data)) return "";
//            String trackUrl = data.getString("url");
//            if (StringUtil.isEmpty(trackUrl) || urlBlacklist.contains(trackUrl)) return "";
//            return trackUrl;
//        } catch (Exception e) {
//            LogUtil.error(e);
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        GrassQqTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("001CnSwn2xF1ee", AudioQuality.KEYS[AudioQuality.SUPER]));
//        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//        System.out.println(trackReq.getTrackUrl("0039MnYb0qxYhV", AudioQuality.KEYS[AudioQuality.HI_RES]));
//    }
//}
