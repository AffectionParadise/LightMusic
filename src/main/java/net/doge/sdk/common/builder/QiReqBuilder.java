package net.doge.sdk.common.builder;

import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.util.Map;
import java.util.TreeMap;

public class QiReqBuilder {
    private static QiReqBuilder instance;

    private QiReqBuilder() {
    }

    public static QiReqBuilder getInstance() {
        if (instance == null) instance = new QiReqBuilder();
        return instance;
    }

    public HttpRequest buildRequest(String baseUrl, Map<String, Object> params) {
        // 公共 query
        if (params == null) params = new TreeMap<>();
        params.put("appid", "16073360");
        params.put("timestamp", System.currentTimeMillis());

        String query = UrlUtil.buildQuery(params);

        String secret = "0b50b02fd0d73a9c4c8c3a781c30845f";
        // 签名需要参数有序，使用 TreeMap 保持有序化
        String sign = CryptoUtil.md5Hex(UrlUtil.decode(query) + secret);
        String url = baseUrl + "?" + query + "&sign=" + sign;
        return HttpRequest.get(url);
    }
}
