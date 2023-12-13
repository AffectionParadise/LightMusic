package net.doge.sdk.common.opt.kg;

import java.util.HashMap;
import java.util.Map;

public class KugouReqOptsBuilder {
    // 安卓 GET 请求
    public static Map<KugouReqOptEnum, String> androidGet(String url) {
        Map<KugouReqOptEnum, String> opts = new HashMap<>();
        opts.put(KugouReqOptEnum.METHOD, KugouReqOptConstants.GET);
        opts.put(KugouReqOptEnum.CRYPTO, KugouReqOptConstants.ANDROID);
        opts.put(KugouReqOptEnum.URL, url);
        return opts;
    }

    // 安卓 POST 请求
    public static Map<KugouReqOptEnum, String> androidPost(String url) {
        Map<KugouReqOptEnum, String> opts = new HashMap<>();
        opts.put(KugouReqOptEnum.METHOD, KugouReqOptConstants.POST);
        opts.put(KugouReqOptEnum.CRYPTO, KugouReqOptConstants.ANDROID);
        opts.put(KugouReqOptEnum.URL, url);
        return opts;
    }
}
