package net.doge.sdk.common.builder;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import net.doge.sdk.common.opt.fs.FiveSingReqOptEnum;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.UrlUtil;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class FiveSingReqBuilder {
    private static FiveSingReqBuilder instance;

    private FiveSingReqBuilder() {
    }

    public static FiveSingReqBuilder getInstance() {
        if (instance == null) instance = new FiveSingReqBuilder();
        return instance;
    }

    public static final String appid = "2918";
    public static final String clientver = "1000";
    public static final String dfid = "4XVEFo0D8RW648Fsb13etRBV";
    public static final String mid = "bd36e74b7a2d9de815fe597d46706a0e";
    public static final String uuid = "bd36e74b7a2d9de815fe597d46706a0e";
    // 5sing 的签名算法和 kugou 一致，只不过 key 不一样
    public static final String signKey = "5uytoxQewcvIc1gn1PlNF0T2jbbOzRl5";

    public HttpRequest buildRequest(Map<String, Object> params, String data, Map<FiveSingReqOptEnum, Object> options) {
        String url = (String) options.getOrDefault(FiveSingReqOptEnum.URL, "");
        Method method = (Method) options.get(FiveSingReqOptEnum.METHOD);

        // 初始化默认参数
        if (params == null) params = new TreeMap<>();
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("dfid", dfid);
        params.put("mid", mid);
        params.put("uuid", uuid);
        params.put("appid", appid);
        params.put("clientver", clientver);
        params.put("clienttime", ct);
        // 签名
        params.put("signature", sign(params, data));
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

    // 签名
    private String sign(Map<String, Object> params, String data) {
//        Map<String, Object> paramsTreeMap = new TreeMap<>(params);
        String content = buildSignParams(params);
        return CryptoUtil.md5(signKey + content + (StringUtil.notEmpty(data) ? data : "") + signKey);
    }

    // 请求参数签名
    public static String signParamsKey(String data) {
        return CryptoUtil.md5(appid + signKey + clientver + data);
    }
}
