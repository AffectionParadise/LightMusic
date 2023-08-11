//package net.doge.sdk.common.builder;
//
//import cn.hutool.http.Header;
//import cn.hutool.http.HttpRequest;
//import net.doge.sdk.common.SdkCommon;
//import net.doge.util.common.CryptoUtil;
//import net.doge.util.common.StringUtil;
//
//public class MiguReqBuilder {
//    public static HttpRequest buildSearchRequest(String type, String keyword, int page, int limit) {
//        String deviceId = "963B7AA0D21511ED807EE5846EC87D20";
//        String signatureMD5 = "6cdc72a439cef99a3418d2a78aa28c73";
//        String time = String.valueOf(System.currentTimeMillis());
//        String sign = CryptoUtil.hashMD5(keyword + signatureMD5 + "yyapp2d16148780a1dcc7408e06336b98cfd50" + deviceId + time);
//
//        String json;
//        switch (type) {
//            case "song":
//            default:
//                json = "{\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":1,\"mvSong\":1,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
//                break;
//            case "lyric":
//                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":1}";
//                break;
//            case "playlist":
//                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":1,\"lyricSong\":0}";
//                break;
//            case "album":
//                json = "{\"song\":0,\"album\":1,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
//                break;
//            case "artist":
//                json = "{\"song\":0,\"album\":0,\"singer\":1,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
//                break;
//        }
//        String url = "https://jadeite.migu.cn/music_search/v3/search/searchAll?isCorrect=1&isCopyright=1&searchSwitch=%s&text=%s&pageNo=%s&pageSize=%s&sort=0";
//        return HttpRequest.get(String.format(url, StringUtil.urlEncodeAll(json), StringUtil.urlEncodeAll(keyword), page, limit))
//                .header("uiVersion", "A_music_3.6.1")
//                .header("deviceId", deviceId)
//                .header("timestamp", time)
//                .header("sign", sign)
//                .header("channel", "0146921")
//                .header(Header.USER_AGENT, SdkCommon.USER_AGENT);
//    }
//}
