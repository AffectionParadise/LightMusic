package net.doge.sdk.common.builder;

import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.constant.qq.QqSearchDevice;
import net.doge.sdk.common.constant.qq.QqSearchType;
import net.doge.sdk.common.crypto.QqCrypto;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class QqReqBuilder {
    private static QqReqBuilder instance;

    private QqReqBuilder() {
    }

    public static QqReqBuilder getInstance() {
        if (instance == null) instance = new QqReqBuilder();
        return instance;
    }

    // 需要签名的 QQ API
    public static final String SIGNED_MAIN_QQ_API = "https://u.y.qq.com/cgi-bin/musics.fcg";

    public HttpRequest buildRequest(String data) {
        String sign = QqCrypto.getInstance().zzcSign(data);
        Map<String, Object> params = new HashMap<>();
        params.put("sign", sign);
        return HttpRequest.post(SIGNED_MAIN_QQ_API)
                .queryParams(params)
                .jsonBody(data);
    }

    public HttpRequest buildPlaylistRequest(String data) {
        Map<String, Object> params = new HashMap<>();
        params.put("loginUin", 0);
        params.put("hostUin", 0);
        params.put("format", "json");
        params.put("inCharset", "utf-8");
        params.put("outCharset", "utf-8");
        params.put("notice", 0);
        params.put("platform", "wk_v15.json");
        params.put("needNewCode", 0);
        params.put("data", data);
        return HttpRequest.get(SdkCommon.MAIN_QQ_API)
                .queryParams(params);
    }

    public HttpRequest buildSearchRequest(QqSearchDevice device, QqSearchType type, String keyword, int page, int limit) {
        String json = "{\"comm\":{\"_channelid\":\"0\",\"_os_version\":\"6.2.9200-2\",\"ct\":\"19\",\"cv\":\"2151\",\"guid\":\"1F70E520B2EAA7D25E11760783C53CA9\"," +
                "\"patch\":\"118\",\"psrf_access_token_expiresAt\":0,\"psrf_qqaccess_token\":\"\",\"psrf_qqopenid\":\"\",\"psrf_qqunionid\":\"\",\"tmeAppID\":\"qqmusic\"," +
                "\"tmeLoginType\":0,\"uin\":\"0\",\"wid\":\"7223299733393904640\"},\"req\":{\"module\":\"music.search.SearchCgiService\"," +
                "\"method\":\"%s\",\"param\":{\"searchid\":\"%s\",\"search_type\":%s,\"query\":\"%s\",\"page_num\":%s,\"num_per_page\":%s," +
                "\"highlight\":0,\"grp\":1,\"remoteplace\":\"txt.newclient.top\"}}}";
        int t;
        switch (type) {
            case SONG:
            default:
                t = 0;
                break;
            case LYRIC:
                t = 7;
                break;
            case PLAYLIST:
                t = 3;
                break;
            case ALBUM:
                t = 2;
                break;
            case ARTIST:
                t = 1;
                break;
            case MV:
                t = 4;
                break;
            case USER:
                t = 8;
                break;
        }
        String method;
        switch (device) {
            case MOBILE:
            default:
                // 不推荐使用手机端，依然存在返回空数据问题
                method = "DoSearchForQQMusicMobile";
                break;
            case PC:
                method = "DoSearchForQQMusicDesktop";
                break;
        }
        String searchId = getSearchId();
        String data = String.format(json, method, searchId, t, keyword, page, limit);
        return buildRequest(data)
                .userAgent("QQMusic 14090508(android 12)");
    }

    /**
     * PC 客户端版 searchid：32 位大写十六进制 GUID + 5 位补零随机数 = 37 字符
     * 对应 QQ 音乐 PC 端 searchid 形状（服务端只需要唯一的会话 ID，形状一致即可）
     */
    private String getSearchId() {
        String guid = RandomUtil.randomGuid();
        String randomPart = RandomUtil.randomNumbers(5);
        return guid + randomPart;
    }
}
