package net.doge.util.core.net;

import cn.hutool.core.util.URLUtil;
import net.doge.util.core.log.LogUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Doge
 * @description URL 工具类
 * @date 2020/12/15
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

    /**
     * 获取 url 的 query
     *
     * @param url
     * @return
     */
    public static String getQuery(String url) {
        try {
            URI uri = new URI(url);
            return uri.getQuery();
        } catch (URISyntaxException e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 对 query 按字典序排序
     *
     * @param query
     * @return
     */
    public static String sortQuery(String query) {
        // 将参数按照字典序排序
        String[] sp = query.split("&");
        Arrays.sort(sp);
        // 合并排序后的参数
        StringJoiner sj = new StringJoiner("&");
        for (String s : sp) sj.add(s);
        return sj.toString();
    }

    /**
     * 构造请求 query (默认进行 url 编码)
     *
     * @param params
     * @return
     */
    public static String buildQuery(Map<String, Object> params) {
        return buildQuery(params, true);
    }

    public static String buildQuery(Map<String, Object> params, boolean encode) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> e : params.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            sj.add(k + "=" + (encode ? encodeAll(v instanceof String ? (String) v : v.toString()) : v));
        }
        return sj.toString();
    }
}
