package net.doge.sdk.common.opt;

import net.doge.sdk.common.crypto.NeteaseCrypto;

import java.util.HashMap;
import java.util.Map;

public class NeteaseReqOptsBuilder {
    /**
     * 构造 weapi 请求选项
     *
     * @return
     */
    public static Map<NeteaseReqOptEnum, String> weApi() {
        Map<NeteaseReqOptEnum, String> opts = new HashMap<>();
        opts.put(NeteaseReqOptEnum.CRYPTO, NeteaseCrypto.WE_API);
        return opts;
    }

    /**
     * 构造 eapi 请求选项
     *
     * @param url
     * @return
     */
    public static Map<NeteaseReqOptEnum, String> eApi(String url) {
        Map<NeteaseReqOptEnum, String> opts = new HashMap<>();
        opts.put(NeteaseReqOptEnum.CRYPTO, NeteaseCrypto.E_API);
        opts.put(NeteaseReqOptEnum.URL, url);
        return opts;
    }
}
