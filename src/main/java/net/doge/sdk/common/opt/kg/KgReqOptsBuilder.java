package net.doge.sdk.common.opt.kg;

import net.doge.util.core.http.constant.Method;

import java.util.HashMap;
import java.util.Map;

public class KgReqOptsBuilder {
    // 安卓 GET 请求
    public static Map<KgReqOptEnum, Object> androidGet(String url) {
        Map<KgReqOptEnum, Object> opts = new HashMap<>();
        opts.put(KgReqOptEnum.METHOD, Method.GET);
        opts.put(KgReqOptEnum.CRYPTO, KgReqOptConstants.ANDROID);
        opts.put(KgReqOptEnum.URL, url);
        return opts;
    }

    // 安卓 GET 请求，带加密 key
    public static Map<KgReqOptEnum, Object> androidGetWithKey(String url) {
        Map<KgReqOptEnum, Object> opts = androidGet(url);
        opts.put(KgReqOptEnum.ENCRYPT_KEY, true);
        return opts;
    }

    // 安卓 POST 请求
    public static Map<KgReqOptEnum, Object> androidPost(String url) {
        Map<KgReqOptEnum, Object> opts = new HashMap<>();
        opts.put(KgReqOptEnum.METHOD, Method.POST);
        opts.put(KgReqOptEnum.CRYPTO, KgReqOptConstants.ANDROID);
        opts.put(KgReqOptEnum.URL, url);
        return opts;
    }
}
