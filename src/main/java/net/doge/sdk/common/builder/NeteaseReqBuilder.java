package net.doge.sdk.common.builder;

import net.doge.sdk.common.crypto.NeteaseCrypto;
import net.doge.sdk.common.opt.nc.NeteaseReqOptConstants;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.core.CryptoUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.UrlUtil;

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

    private final String anonymousToken = "009CFD1212520F50F02A14F6325846255C6B1BAFBEF0AC6CE8BA28CE7E932BAE7627D880D9332BAC466743838802F096DF38CB3146477042FC9B3D86519804C6305AADCB87E56370A3936A187E8623BC6BB249D324F40E2A6924B7B3D9D6413CB5B5CBA1525416AF914614073E988B1188B2AD6DF990E15DBDE22A7713064B29F1BC65DCBAB2C6A2B5C525E400BEF202EB1C358BF62D2F6934A7534A5B1D9570FAE60EDCFDA1E29BCD1C945D152D6C30329601F24D6662A16FF93577484E94018D707E315D6921B0114295439C1CEEE792B6EEA7B07001424422887589AC31F9D4FEBA9E3C9ED4DCABC20E7039A1B7E5913F747214DEE977DDDB2B34F51BCA6AAC42791F4AC0C44E503DD11DE4D635B8EE834BECEE52799D89B98A610FFD18D158EB32760D84D8900E3EE511159E9EBAC5461782F4EA1B34369540486CB2C794B8EDEED684905A76616AE9E9497279C1E1C1B8A591E20957D877CA21E0DCC88BB32D3E73845FD23E41DA111F5B601BE55262A9FE0C1370C0695243C5D4BF82C614615F338FF4D4B2889244274DF8D823E2452BF316D4FEFB72D1836B6373A3C83012BD872DBB79A85D3F35BC850A015E1B1AC12744B3EE9EC4B97F63912AB05182C45FE6764C85ADB872238231C06DABBF3F67B95E22ADB09AE801912AEA6407D32F4B1D4DDDF91133987A2DD3B6AEEC294885A3BD9F2FAAF0181E72ECB017D676EFA45E609ACA5516C3617849022D615A";
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
        String osver = "Microsoft-Windows-10-Professional-build-19045-64bit";
        String deviceId = "";
        String os = "pc";
        String channel = "netease";
        String appver = "3.1.17.204416";
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
        return HttpRequest.createRequest(url, method)
                .headers(headers)
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
            sb.append(entry.getKey()).append("=").append(UrlUtil.encodeAll(entry.getValue())).append("; ");
        }
        return sb.toString();
    }
}
