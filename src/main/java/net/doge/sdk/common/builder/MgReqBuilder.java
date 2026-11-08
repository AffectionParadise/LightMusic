package net.doge.sdk.common.builder;

import net.doge.sdk.common.constant.mg.MgSearchType;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

public class MgReqBuilder {
    private static MgReqBuilder instance;

    private MgReqBuilder() {
    }

    public static MgReqBuilder getInstance() {
        if (instance == null) instance = new MgReqBuilder();
        return instance;
    }

    // 搜索 API (咪咕)
    private final String SEARCH_MG_API = "https://jadeite.migu.cn/music_search/v3/search/searchAll?isCorrect=0&isCopyright=1&searchSwitch=%s&text=%s&pageNo=%s&pageSize=%s&sort=0&sid=USS";

    public HttpRequest buildSearchRequest(MgSearchType type, String keyword, int page, int limit) {
        String deviceId = "963B7AA0D21511ED807EE5846EC87D20";
        String signatureMD5 = "6cdc72a439cef99a3418d2a78aa28c73";
        String time = String.valueOf(System.currentTimeMillis());
        String sign = CryptoUtil.md5Hex(keyword + signatureMD5 + "yyapp2d16148780a1dcc7408e06336b98cfd50" + deviceId + time);

        String json;
        switch (type) {
            case SONG:
            default:
                json = "{\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":1,\"mvSong\":1,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
            case LYRIC:
                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":1}";
                break;
            case PLAYLIST:
                json = "{\"song\":0,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":1,\"lyricSong\":0}";
                break;
            case ALBUM:
                json = "{\"song\":0,\"album\":1,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
            case ARTIST:
                json = "{\"song\":0,\"album\":0,\"singer\":1,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}";
                break;
        }
        return HttpRequest.get(String.format(SEARCH_MG_API, UrlUtil.encodeAll(json), UrlUtil.encodeAll(keyword), page, limit))
                .header("uiVersion", "A_music_3.6.1")
                .header("deviceId", deviceId)
                .header("timestamp", time)
                .header("sign", sign)
                .header("channel", "0146921");
    }
}
