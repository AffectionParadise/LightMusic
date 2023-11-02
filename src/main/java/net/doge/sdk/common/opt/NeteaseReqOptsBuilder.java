package net.doge.sdk.common.opt;

import java.util.HashMap;
import java.util.Map;

public class NeteaseReqOptsBuilder {
    // 构造 weapi 请求选项
    public static Map<NeteaseReqOptEnum, String> weapi() {
        Map<NeteaseReqOptEnum, String> opts = new HashMap<>();
        opts.put(NeteaseReqOptEnum.CRYPTO, NeteaseReqOptConstants.WEAPI);
        return opts;
    }

    // 构造 weapi 请求选项，用 mobile 的 UA
    public static Map<NeteaseReqOptEnum, String> weapiMobile() {
        Map<NeteaseReqOptEnum, String> opts = weapi();
        opts.put(NeteaseReqOptEnum.UA, NeteaseReqOptConstants.MOBILE);
        return opts;
    }

    // 构造 weapi 请求选项，用 PC 的 UA
    public static Map<NeteaseReqOptEnum, String> weapiPC() {
        Map<NeteaseReqOptEnum, String> opts = weapi();
        opts.put(NeteaseReqOptEnum.UA, NeteaseReqOptConstants.PC);
        return opts;
    }

    // 构造 eapi 请求选项
    public static Map<NeteaseReqOptEnum, String> eapi(String path) {
        Map<NeteaseReqOptEnum, String> opts = new HashMap<>();
        opts.put(NeteaseReqOptEnum.CRYPTO, NeteaseReqOptConstants.EAPI);
        opts.put(NeteaseReqOptEnum.PATH, path);
        return opts;
    }
}
