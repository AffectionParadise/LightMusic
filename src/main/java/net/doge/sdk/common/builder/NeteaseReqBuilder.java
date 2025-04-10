package net.doge.sdk.common.builder;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import net.doge.sdk.common.crypto.NeteaseCrypto;
import net.doge.sdk.common.opt.nc.NeteaseReqOptConstants;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class NeteaseReqBuilder {
    private static NeteaseReqBuilder instance;

    private NeteaseReqBuilder() {
    }

    public static NeteaseReqBuilder getInstance() {
        if (instance == null) instance = new NeteaseReqBuilder();
        return instance;
    }

    private final String anonymousToken = "001E3EBFF2DB872F6150D523309EDF06ED115D4E2A80B5F5F11135C5F4D39CE67EA22F40C4D1F2FA5FC5FB94A3E6FA32FC365F4C2BF8BBB78F3B97981343B7F3658CEFC0C9CC823985D6272CAE8F0FF721BECF48401C0C07278EAA3C6F873CA9110D808859E780D1F139C3FD4326B2A80D89D5FA5497D30010F3C66F545854E1F3868B9EEC95BC0788EA236243FC5DDF8A99DDD0C5AA9907222EF4DE9896BA7D249C130F4AFCE873BF071F9C01D7DC6C45D234465D02FC2D63305CD1BEE9960A8E0AB575BDD8C0E5391E1AEF58B1780324792039C543B7DDD2A104CC3B083BFADDA0354462347DD5EC33522772234D7E9EB692856076B86AD0CE72822C5085B0A0C0ABBD9C9CFC9C1ED1FD6274C426BB127468F39808A0A9852EEA5B39DE4CD1D6E13E306671BCF7B7F1AF3D95DDC1D836532F878C7E1DAF4CFF74BB67CB8B7FFE2DCF9833145B518F413D17940CD346D240FF82F0388E8E1E34DEE13BC67954AD30D88A2E0B17A36783B89AA039833C83AE4F9CEC3C67E2ABBBD53611911E43D3E3648D490BE2CE79772C0AD0319883E4AF9E8C57F8425C098CDF3F9FDD2DBD10E6B2F6F70F9B7B70D1E373DE8A592768";
    private final String[] MOBILE_USER_AGENTS = {
            // iOS 13.5.1 14.0 beta with safari
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.",
            // iOS with qq micromsg
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/602.1.50 (KHTML like Gecko) Mobile/14A456 QQ/6.5.7.408 V1_IPH_SQ_6.5.7_1_APP_A Pixel/750 Core/UIWebView NetType/4G Mem/103",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f27) NetType/WIFI Language/zh",
            // Android -> Huawei Xiaomi
            "Mozilla/5.0 (Linux; Android 9; PCT-AL10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.64 HuaweiBrowser/10.0.3.311 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 9; zh-cn; Redmi Note 8 Build/PKQ1.190616.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.141 Mobile Safari/537.36 XiaoMi/MiuiBrowser/12.5.22",
            // Android + qq micromsg
            "Mozilla/5.0 (Linux; Android 10; YAL-AL00 Build/HUAWEIYAL-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.62 XWEB/2581 MMWEBSDK/200801 Mobile Safari/537.36 MMWEBID/3027 MicroMessenger/7.0.18.1740(0x27001235) Process/toolsmp WeChat/arm64 NetType/WIFI Language/zh_CN ABI/arm64",
            "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BKK-AL10 Build/HONORBKK-AL10) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/10.6 Mobile Safari/537.36"
    };
    private final String[] PC_USER_AGENTS = {
            // macOS 10.15.6  Firefox / Chrome / Safari
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:80.0) Gecko/20100101 Firefox/80.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.30 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.2 Safari/605.1.15",
            // Windows 10 Firefox / Chrome / Edge
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:80.0) Gecko/20100101 Firefox/80.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.30 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586"
            // Linux 就算了
    };

    public HttpRequest buildRequest(Method method, String url, String data, Map<NeteaseReqOptEnum, String> options) {
        Map<String, String> headers = new HashMap<>();
        String device = options.getOrDefault(NeteaseReqOptEnum.UA, NeteaseReqOptConstants.BOTH);
        switch (device) {
            case NeteaseReqOptConstants.MOBILE:
                headers.put("User-Agent", ArrayUtil.randomChoose(MOBILE_USER_AGENTS));
                break;
            case NeteaseReqOptConstants.PC:
                headers.put("User-Agent", ArrayUtil.randomChoose(PC_USER_AGENTS));
                break;
            case NeteaseReqOptConstants.BOTH:
                headers.put("User-Agent", ArrayUtil.randomChoose(ArrayUtil.concat(MOBILE_USER_AGENTS, PC_USER_AGENTS)));
                break;
        }
        if (method == Method.POST) headers.put("Content-Type", "application/x-www-form-urlencoded");

        String _ntes_nuid = CryptoUtil.bytesToHex(ArrayUtil.randomBytes(32));
        String _ntes_nnid = String.format("%s,%s", _ntes_nuid, System.currentTimeMillis());
        String WNMCID = getWNMCID();
        String WEVNSM = "1.0.0";
        String NMTID = CryptoUtil.bytesToHex(ArrayUtil.randomBytes(16));
        String osver = "16.2";
        String deviceId = "";
        String os = "iPhone OS";
        String channel = "distribution";
        String appver = "9.0.90";
        String csrfToken = "";

        String body = "";
        switch (options.get(NeteaseReqOptEnum.CRYPTO)) {
            case NeteaseReqOptConstants.WEAPI:
                headers.put("Referer", "https://music.163.com");
                String cookie = String.format("__remember_me=true; ntes_kaola_ad=1; _ntes_nuid=%s; _ntes_nnid=%s; WNMCID=%s; WEVNSM=%s; NMTID=%s; osver=%s; deviceId=%s; " +
                                "os=%s; channel=%s; appver=%s; MUSIC_A=%s;",
                        _ntes_nuid, _ntes_nnid, WNMCID, WEVNSM, NMTID, osver, deviceId, os, channel, appver, anonymousToken);
                headers.put("Cookie", cookie);
                body = NeteaseCrypto.getInstance().weapi(data);
                url = url.replaceFirst("\\w*api", "weapi");
                break;
            case NeteaseReqOptConstants.EAPI:
                String requestId = System.currentTimeMillis() + "_" + StringUtil.padPre(String.valueOf(Math.floor(Math.random() * 1000)), 4, "0");
                headers.put("osver", osver);
                headers.put("deviceId", deviceId);
                headers.put("os", os);
                headers.put("appver", appver);
                headers.put("versioncode", "140");
                headers.put("mobilename", "");
                headers.put("buildver", String.valueOf(System.currentTimeMillis()).substring(0, 10));
                headers.put("resolution", "1920x1080");
                headers.put("__csrf", csrfToken);
                headers.put("channel", channel);
                headers.put("requestId", requestId);
                headers.put("MUSIC_A", anonymousToken);
                headers.put("Cookie", getCookie(headers));
                body = NeteaseCrypto.getInstance().eapi(options.get(NeteaseReqOptEnum.PATH), data);
                url = url.replaceFirst("\\w*api", "eapi");
                break;
        }
        return HttpUtil.createRequest(method, url)
                .headerMap(headers, true)
                .body(body);
    }

    private String getWNMCID() {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = characters.length(); i < 6; i++)
            sb.append(characters.charAt((int) Math.floor(Math.random() * len)));
        return String.format("%s.%s.01.0", sb, System.currentTimeMillis());
    }

    private String getCookie(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append("=").append(StringUtil.urlEncodeAll(entry.getValue())).append("; ");
        }
        return sb.toString();
    }
}
