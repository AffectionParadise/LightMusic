package net.doge.sdk.common.builder;

import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.log.LogUtil;
import net.doge.util.core.net.UrlUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class QianReqBuilder {
    private static QianReqBuilder instance;

    private QianReqBuilder() {
    }

    public static QianReqBuilder getInstance() {
        if (instance == null) instance = new QianReqBuilder();
        return instance;
    }

    private final String secret = "0b50b02fd0d73a9c4c8c3a781c30845f";

    public HttpRequest buildRequest(String url) {
        try {
            // 提取参数并解码
            URI uri = new URI(url);
            String query = uri.getQuery();
            String sortedQuery = UrlUtil.sortQuery(query);
            String sign = CryptoUtil.md5Hex(sortedQuery + secret);
            return HttpRequest.get(url + "&sign=" + sign);
        } catch (URISyntaxException e) {
            LogUtil.error(e);
            return null;
        }
    }
}
