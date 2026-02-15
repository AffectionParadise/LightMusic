package net.doge.sdk.common.opt.kg;

import net.doge.util.core.http.constant.Method;

import java.util.HashMap;
import java.util.Map;

public class KugouReqOptsBuilder {
    // 安卓 GET 请求
    public static Map<KugouReqOptEnum, Object> androidGet(String url) {
        Map<KugouReqOptEnum, Object> opts = new HashMap<>();
        opts.put(KugouReqOptEnum.METHOD, Method.GET);
        opts.put(KugouReqOptEnum.CRYPTO, KugouReqOptConstants.ANDROID);
        opts.put(KugouReqOptEnum.URL, url);
        return opts;
    }

    // 安卓 GET 请求，带加密 key
    public static Map<KugouReqOptEnum, Object> androidGetWithKey(String url) {
        Map<KugouReqOptEnum, Object> opts = androidGet(url);
        opts.put(KugouReqOptEnum.ENCRYPT_KEY, true);
        return opts;
    }

    // 安卓 POST 请求
    public static Map<KugouReqOptEnum, Object> androidPost(String url) {
        Map<KugouReqOptEnum, Object> opts = new HashMap<>();
        opts.put(KugouReqOptEnum.METHOD, Method.POST);
        opts.put(KugouReqOptEnum.CRYPTO, KugouReqOptConstants.ANDROID);
        opts.put(KugouReqOptEnum.URL, url);
        return opts;
    }
}
