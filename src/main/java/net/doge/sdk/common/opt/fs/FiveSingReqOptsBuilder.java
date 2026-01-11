package net.doge.sdk.common.opt.fs;

import cn.hutool.http.Method;

import java.util.HashMap;
import java.util.Map;

public class FiveSingReqOptsBuilder {
    // GET 请求
    public static Map<FiveSingReqOptEnum, Object> get(String url) {
        Map<FiveSingReqOptEnum, Object> opts = new HashMap<>();
        opts.put(FiveSingReqOptEnum.METHOD, Method.GET);
        opts.put(FiveSingReqOptEnum.URL, url);
        return opts;
    }
}
