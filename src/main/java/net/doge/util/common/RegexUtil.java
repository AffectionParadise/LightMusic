package net.doge.util.common;

import cn.hutool.core.util.ReUtil;

import java.util.List;

/**
 * @Author yzx
 * @Description 正则表达式工具类
 * @Date 2020/12/15
 */
public class RegexUtil {
    /**
     * 正则匹配，返回组 1
     *
     * @param regex
     * @param content
     * @return
     */
    public static String getGroup1(String regex, CharSequence content) {
        return ReUtil.getGroup1(regex, content);
    }

    /**
     * 匹配全部，返回字符串列表
     *
     * @param regex
     * @param content
     * @return
     */
    public static List<String> findAllGroup1(String regex, CharSequence content) {
        return ReUtil.findAllGroup1(regex, content);
    }
}
