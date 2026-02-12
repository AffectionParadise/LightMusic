//package net.doge.sdk.service.music.info.trackhero.kg;
//
//import cn.hutool.http.HttpRequest;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.util.core.JsonUtil;
//import net.doge.util.core.StringUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class CggKgTrackHero {
//    private static CggKgTrackHero instance;
//
//    private CggKgTrackHero() {
//        initMap();
//    }
//
//    public static CggKgTrackHero getInstance() {
//        if (instance == null) instance = new CggKgTrackHero();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API
//    private final String SONG_URL_API = "https://music-api2.cenguigui.cn/?kg=&id=%s&type=song&format=json&level=%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        qualityMap.put("ogg", "ogg");
//        qualityMap.put("standard", "standard");
//        qualityMap.put("hq", "exhigh");
//        qualityMap.put("lossless", "lossless");
//        qualityMap.put("hires", "hires");
//    }
//
//    /**
//     * 获取网易云音乐歌曲链接
//     *
//     * @param id      歌曲 id
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String id, String quality) {
//        String songBody = HttpRequest.get(String.format(SONG_URL_API, id, qualityMap.get(quality)))
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
//                // 标准浏览器接受类型
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//                // 自动解压gzip
//                .header("Accept-Encoding", "gzip, deflate, br, zstd")
//                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
//                .header("Cache-Control", "max-age=0")
//                .header("Priority", "u=0, i")
//                // 模拟浏览器安全头
//                .header("Sec-Ch-Ua", "\"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Microsoft Edge\";v=\"144\"")
//                .header("Sec-Ch-Ua-Mobile", "?0")
//                .header("Sec-Ch-Ua-Platform", "\"Windows\"")
//                .header("Sec-Fetch-Dest", "document")
//                .header("Sec-Fetch-Mode", "navigate")
//                .header("Sec-Fetch-Site", "none")
//                .header("Sec-Fetch-User", "?1")
//                .header("Upgrade-Insecure-Requests", "1")
//                // 启用Cookie自动管理
//                .enableDefaultCookie()
//                // 设置超时（毫秒）
//                .timeout(10000)
//                // 允许重定向
//                .setFollowRedirects(true)
//                .executeAsync()
//                .body();
//        JSONArray data = JSONObject.parseObject(songBody).getJSONArray("data");
//        if (JsonUtil.isEmpty(data)) return "";
//        JSONObject urlJson = data.getJSONObject(0);
//        // 排除试听部分，直接换源
//        if (JsonUtil.isEmpty(urlJson.getJSONObject("freeTrialInfo"))) {
//            String url = urlJson.getString("url");
//            if (StringUtil.notEmpty(url)) return url;
//        }
//        return "";
//    }
//
//    public static void main(String[] args) {
//        CggKgTrackHero trackHero = getInstance();
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", "standard"));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", "hq"));
//        System.out.println(trackHero.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", "lossless"));
//    }
//}
