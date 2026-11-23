package net.doge.sdk.common.builder;

import net.doge.sdk.common.opt.kg.KgReqOptConstants;
import net.doge.sdk.common.opt.kg.KgReqOptEnum;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Method;

import java.util.Map;
import java.util.TreeMap;

public class KgReqBuilder {
    private static KgReqBuilder instance;

    private KgReqBuilder() {
    }

    public static KgReqBuilder getInstance() {
        if (instance == null) instance = new KgReqBuilder();
        return instance;
    }

    public static final String appid = "1005";
    public static final String apiver = "20";
    public static final String clientver = "12569";
    private final String pidversec = "57ae12eb6890223e355ccfcb74edf70d";
    public static final String userid = "0";
    public static final String dfid = "-";
    public static final String mid = "16249512204336365674023395779019";
    public static final String uuid = "15e772e1213bdd0718d0c1d10d64e06f";
    // 安卓 key
    public static final String androidSignKey = "OIlwieks28dk2k092lksi2UIkp";
    // Web key
    public static final String webSignKey = "NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt";

    public HttpRequest buildRequest(Map<String, Object> params, String data, Map<KgReqOptEnum, Object> options) {
        String url = (String) options.getOrDefault(KgReqOptEnum.URL, "");
        if (!url.startsWith("http")) url = "https://gateway.kugou.com" + url;
        Method method = (Method) options.get(KgReqOptEnum.METHOD);

        // 初始化默认参数
        if (params == null) params = new TreeMap<>();
        if (!(params instanceof TreeMap)) params = new TreeMap<>(params);
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("dfid", dfid);
        params.put("mid", mid);
        params.put("uuid", uuid);
        params.put("appid", appid);
        params.put("apiver", apiver);
        params.put("clientver", clientver);
        params.put("userid", userid);
        params.put("clienttime", ct);

        // 带加密 key
        if ((Boolean) options.getOrDefault(KgReqOptEnum.WITH_ENCRYPT_KEY, false))
            params.put("key", CryptoUtil.md5Hex(params.get("hash") + pidversec + appid + mid + userid));

        String crypto = (String) options.get(KgReqOptEnum.CRYPTO);
        switch (crypto) {
            case KgReqOptConstants.ANDROID:
                params.put("signature", createSignature(params, data, androidSignKey));
                break;
            case KgReqOptConstants.WEB:
                params.put("signature", createSignature(params, data, webSignKey));
                break;
        }
        return HttpRequest.createRequest(method, url)
                .queryParams(params)
                .header("dfid", dfid)
                .header("mid", mid)
                .header("clienttime", ct)
                .jsonBody(data);
    }

    // 构造签名参数
    private String buildSignParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String k : params.keySet()) sb.append(k).append("=").append(params.get(k));
        return sb.toString();
    }

    // 生成签名，安卓/WEB 使用的 key 不同
    private String createSignature(Map<String, Object> params, String data, String key) {
        String content = buildSignParams(params);
        return CryptoUtil.md5Hex(key + content + (StringUtil.notEmpty(data) ? data : "") + key);
    }

    // 请求参数签名
    public static String signParamsKey(String data) {
        return CryptoUtil.md5Hex(appid + androidSignKey + clientver + data);
    }
}
