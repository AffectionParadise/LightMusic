package net.doge.sdk.common.opt.nc;

import java.util.HashMap;
import java.util.Map;

public class NcReqOptsBuilder {
    // 构造 weapi 请求选项
    public static Map<NcReqOptEnum, String> weapi() {
        Map<NcReqOptEnum, String> opts = new HashMap<>();
        opts.put(NcReqOptEnum.CRYPTO, NcReqOptConstants.WEAPI);
        return opts;
    }

    // 构造 weapi 请求选项，用 mobile 的 UA
    public static Map<NcReqOptEnum, String> weapiMobile() {
        Map<NcReqOptEnum, String> opts = weapi();
        opts.put(NcReqOptEnum.UA, NcReqOptConstants.MOBILE);
        return opts;
    }

    // 构造 weapi 请求选项，用 PC 的 UA
    public static Map<NcReqOptEnum, String> weapiPC() {
        Map<NcReqOptEnum, String> opts = weapi();
        opts.put(NcReqOptEnum.UA, NcReqOptConstants.PC);
        return opts;
    }

    // 构造 eapi 请求选项
    public static Map<NcReqOptEnum, String> eapi(String path) {
        Map<NcReqOptEnum, String> opts = new HashMap<>();
        opts.put(NcReqOptEnum.CRYPTO, NcReqOptConstants.EAPI);
        opts.put(NcReqOptEnum.PATH, path);
        return opts;
    }
}
