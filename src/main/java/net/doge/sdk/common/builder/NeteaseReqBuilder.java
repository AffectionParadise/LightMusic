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
    private static final String[] MOBILE_USER_AGENTS = {
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
    private static final String[] PC_USER_AGENTS = {
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

    public static HttpRequest buildRequest(Method method, String url, String data, Map<NeteaseReqOptEnum, String> options) {
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
        if (url.contains("music.163.com")) headers.put("Referer", "https://music.163.com");
        String anonymousToken = "1f5fa7b6a6a9f81a11886e5186fde7fbf61e685ebf8a0ea2caec48f1a15f2a764b7f5273fc3921d1865fd2a3083cb3937e7b74823f13808adf8ea1560b5215ac07fb84071dfb15a13324751bcc9aaf44c3061cd18d77b7a0";
        String body = "";
        switch (options.get(NeteaseReqOptEnum.CRYPTO)) {
            case NeteaseReqOptConstants.WEAPI:
                headers.put("Cookie", String.format(
                        "os=iOS; __remember_me=true; _ntes_nuid=%s; NMTID=%s; MUSIC_A=%s; appver=8.10.90;",
                        CryptoUtil.bytesToHex(ArrayUtil.randomBytes(16)),
                        CryptoUtil.bytesToHex(ArrayUtil.randomBytes(16)),
                        anonymousToken
                ));
                body = NeteaseCrypto.getInstance().weapi(data);
                url = url.replaceFirst("\\w*api", "weapi");
                break;
            case NeteaseReqOptConstants.EAPI:
                String requestId = System.currentTimeMillis() / 1000 + "_" + StringUtil.padPre(String.valueOf(Math.floor(Math.random() * 1000)), 4, "0");
                headers.put("Cookie", String.format(
                        "osver=; deviceId=; appver=8.9.70; versioncode=140; mobilename=; buildver=1690071476; resolution=1920x1080; " +
                                "__csrf=; os=android; channel=; requestId=%s; MUSIC_A=%s",
                        requestId,
                        anonymousToken
                ));
                body = NeteaseCrypto.getInstance().eapi(options.get(NeteaseReqOptEnum.PATH), data);
                url = url.replaceFirst("\\w*api", "eapi");
                break;
        }
        return HttpUtil.createRequest(method, url)
                .headerMap(headers, true)
                .body(body);
    }
}
