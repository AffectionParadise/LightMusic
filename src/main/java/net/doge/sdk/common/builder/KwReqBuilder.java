package net.doge.sdk.common.builder;

import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.constant.Header;

public class KwReqBuilder {
    private static KwReqBuilder instance;

    private KwReqBuilder() {
    }

    public static KwReqBuilder getInstance() {
        if (instance == null) instance = new KwReqBuilder();
        return instance;
    }

    private final String cookie = "Hm_Iuvt_cdb524f42f23cer9b268564v7y735ewrq2324=w6nWhWQm4y2cTbFFcXi5Xxa3KtXKnjzS";
    private final String secret = "5470ccb31c2e253cf173fea957bd5e544d0b4f6e54f88190868a0817094e920000224d1a";

    public HttpRequest buildRequest(String url) {
        return HttpRequest.get(url)
                .cookie(cookie)
                .header("Secret", secret)
                .header(Header.HOST, "kuwo.cn")
                .header(Header.REFERER, "https://kuwo.cn/");
    }
}
