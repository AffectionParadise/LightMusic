package net.doge.sdk.common.builder;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import net.doge.sdk.common.opt.kg.KugouReqOptConstants;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.util.core.CryptoUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.UrlUtil;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class KugouReqBuilder {
    private static KugouReqBuilder instance;

    private KugouReqBuilder() {
    }

    public static KugouReqBuilder getInstance() {
        if (instance == null) instance = new KugouReqBuilder();
        return instance;
    }

    public static final String appid = "1005";
    public static final String apiver = "20";
    public static final String clientver = "12569";
    private final String pidversec = "57ae12eb6890223e355ccfcb74edf70d";
    public static final String userid = "0";
    public static final String dfid = "-";
    public static final String mid = "336d5ebc5436534e61d16e63ddfca327";
    public static final String uuid = "15e772e1213bdd0718d0c1d10d64e06f";
    public static final String androidSignKey = "OIlwieks28dk2k092lksi2UIkp";

    public HttpRequest buildRequest(Map<String, Object> params, String data, Map<KugouReqOptEnum, Object> options) {
        String url = (String) options.getOrDefault(KugouReqOptEnum.URL, "");
        if (!url.startsWith("http")) url = "https://gateway.kugou.com" + url;
        Method method = (Method) options.get(KugouReqOptEnum.METHOD);

        // 初始化默认参数
        if (params == null) params = new TreeMap<>();
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
        if ((Boolean) options.getOrDefault(KugouReqOptEnum.ENCRYPT_KEY, false))
            params.put("key", CryptoUtil.md5(params.get("hash") + pidversec + appid + mid + userid));

        String crypto = (String) options.get(KugouReqOptEnum.CRYPTO);
        switch (crypto) {
            case KugouReqOptConstants.ANDROID:
                params.put("signature", signAndroid(params, data));
                break;
        }
        url += "?" + buildRequestParams(params);
        return HttpUtil.createRequest(method, url)
                .header("dfid", dfid)
                .header("mid", mid)
                .header("clienttime", ct)
                .body(data);
    }

    // 构造请求参数
    private String buildRequestParams(Map<String, Object> params) {
        StringJoiner sj = new StringJoiner("&");
        for (String k : params.keySet()) {
            Object o = params.get(k);
            Object v = o instanceof String ? UrlUtil.encodeAll((String) o) : o;
            sj.add(k + "=" + v);
        }
        return sj.toString();
    }

    // 构造签名参数
    private String buildSignParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String k : params.keySet()) sb.append(k).append("=").append(params.get(k));
        return sb.toString();
    }

    // 安卓签名
    private String signAndroid(Map<String, Object> params, String data) {
//        Map<String, Object> paramsTreeMap = new TreeMap<>(params);
        String content = buildSignParams(params);
        return CryptoUtil.md5(androidSignKey + content + (StringUtil.notEmpty(data) ? data : "") + androidSignKey);
    }

    // 请求参数签名
    public static String signParamsKey(String data) {
        return CryptoUtil.md5(appid + androidSignKey + clientver + data);
    }
}
