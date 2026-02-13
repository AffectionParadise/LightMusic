package net.doge.sdk.common.builder;

import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Header;
import net.doge.util.core.CryptoUtil;
import net.doge.util.core.UrlUtil;

public class MiguReqBuilder {
    private static MiguReqBuilder instance;

    private MiguReqBuilder() {
    }

    public static MiguReqBuilder getInstance() {
        if (instance == null) instance = new MiguReqBuilder();
        return instance;
    }

    public HttpRequest buildSearchRequest(String type, String keyword, int page, int limit) {
        String deviceId = "963B7AA0D21511ED807EE5846EC87D20";
        String signatureMD5 = "6cdc72a439cef99a3418d2a78aa28c73";
        String time = String.valueOf(System.currentTimeMillis());
        String sign = CryptoUtil.md5(keyword + signatureMD5 + "yyapp2d16148780a1dcc7408e06336b98cfd50" + deviceId + time);

        String json;
        switch (type) {
            case "song":
            default:
                json = "{\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":1,\"mvSong\":1,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
            case "lyric":
                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":1}";
                break;
            case "playlist":
                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":1,\"lyricSong\":0}";
                break;
            case "album":
                json = "{\"song\":0,\"album\":1,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
            case "artist":
                json = "{\"song\":0,\"album\":0,\"singer\":1,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
        }
        String url = "https://jadeite.migu.cn/music_search/v3/search/searchAll?isCorrect=0&isCopyright=1&searchSwitch=%s&text=%s&pageNo=%s&pageSize=%s&sort=0&sid=USS";
        return HttpRequest.get(String.format(url, UrlUtil.encodeAll(json), UrlUtil.encodeAll(keyword), page, limit))
                .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                .header("uiVersion", "A_music_3.6.1")
                .header("deviceId", deviceId)
                .header("timestamp", time)
                .header("sign", sign)
                .header("channel", "0146921");
    }
}
