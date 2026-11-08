package net.doge.sdk.common.opt.fs;

import net.doge.util.core.http.constant.Method;

import java.util.HashMap;
import java.util.Map;

public class FsReqOptsBuilder {
    // GET 请求
    public static Map<FsReqOptEnum, Object> get(String url) {
        Map<FsReqOptEnum, Object> opts = new HashMap<>();
        opts.put(FsReqOptEnum.METHOD, Method.GET);
        opts.put(FsReqOptEnum.URL, url);
        return opts;
    }
}
