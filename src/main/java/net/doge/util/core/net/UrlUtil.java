package net.doge.util.core.net;

import cn.hutool.core.util.URLUtil;

/**
 * @Author Doge
 * @Description URL 工具类
 * @Date 2020/12/15
 */
public class UrlUtil {
    /**
     * url 编码（会处理所有冲突的字符）
     *
     * @param s
     * @return
     */
    public static String encodeAll(String s) {
        return URLUtil.encodeAll(s);
    }

    /**
     * url 编码（处理空白字符）
     *
     * @param s
     * @return
     */
    public static String encodeBlank(String s) {
        return URLUtil.encodeBlank(s);
    }

    /**
     * url 解码
     *
     * @param s
     * @return
     */
    public static String decode(String s) {
        return URLUtil.decode(s);
    }
}
