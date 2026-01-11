package net.doge.sdk.common.builder;

import cn.hutool.http.HttpRequest;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

import java.util.Arrays;
import java.util.StringJoiner;

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
        // 提取参数并解码
        String params = StringUtil.urlDecode(url.substring(url.indexOf('?') + 1));
        // 将参数按照字典序排序
        String[] sp = params.split("&");
        Arrays.sort(sp);
        // 合并排序后的参数
        StringJoiner sj = new StringJoiner("&");
        for (String s : sp) sj.add(s);
        String sortedParams = sj.toString();
        String sign = CryptoUtil.md5(sortedParams + secret);
        return HttpRequest.get(url + "&sign=" + sign);
    }
}
