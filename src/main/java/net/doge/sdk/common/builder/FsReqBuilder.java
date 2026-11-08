package net.doge.sdk.common.builder;

import net.doge.sdk.common.opt.fs.FsReqOptEnum;
import net.doge.util.core.StringUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.net.UrlUtil;

import java.util.Map;
import java.util.TreeMap;

public class FsReqBuilder {
    private static FsReqBuilder instance;

    private FsReqBuilder() {
    }

    public static FsReqBuilder getInstance() {
        if (instance == null) instance = new FsReqBuilder();
        return instance;
    }

    public static final String appid = "2918";
    public static final String clientver = "1000";
    public static final String dfid = "4XVEFo0D8RW648Fsb13etRBV";
    public static final String mid = "bd36e74b7a2d9de815fe597d46706a0e";
    public static final String uuid = "bd36e74b7a2d9de815fe597d46706a0e";
    // 5sing 的签名算法和 kugou 一致，只不过 key 不一样
    public static final String signKey = "5uytoxQewcvIc1gn1PlNF0T2jbbOzRl5";

    public HttpRequest buildRequest(Map<String, Object> params, String data, Map<FsReqOptEnum, Object> options) {
        String url = (String) options.getOrDefault(FsReqOptEnum.URL, "");
        Method method = (Method) options.get(FsReqOptEnum.METHOD);

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
        url += "?" + UrlUtil.buildQuery(params);
        return HttpRequest.createRequest(method, url)
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

    // 签名
    private String sign(Map<String, Object> params, String data) {
//        Map<String, Object> paramsTreeMap = new TreeMap<>(params);
        String content = buildSignParams(params);
        return CryptoUtil.md5Hex(signKey + content + (StringUtil.notEmpty(data) ? data : "") + signKey);
    }
}
