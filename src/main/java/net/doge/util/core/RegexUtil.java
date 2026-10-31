package net.doge.util.core;

import cn.hutool.core.util.ReUtil;
import org.intellij.lang.annotations.Language;

import java.util.List;

/**
 * @author Doge
 * @description 正则表达式工具类
 * @date 2020/12/15
 */
public class RegexUtil {
    /**
     * 正则匹配，返回组 0
     *
     * @param regex
     * @param content
     * @return
     */
    public static String getGroup0(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.getGroup0(regex, content);
    }

    /**
     * 正则匹配，返回组 1
     *
     * @param regex
     * @param content
     * @return
     */
    public static String getGroup1(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.getGroup1(regex, content);
    }

    /**
     * 匹配全部，返回组 0 字符串列表
     *
     * @param regex
     * @param content
     * @return
     */
    public static List<String> findAllGroup0(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.findAllGroup0(regex, content);
    }

    /**
     * 匹配全部，返回组 1 字符串列表
     *
     * @param regex
     * @param content
     * @return
     */
    public static List<String> findAllGroup1(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.findAllGroup1(regex, content);
    }

    /**
     * 测试字符串是否完全匹配模式
     *
     * @param regex
     * @param content
     * @return
     */
    public static boolean test(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.isMatch(regex, content);
    }

    /**
     * 测试字符串是否部分匹配模式
     *
     * @param regex
     * @param content
     * @return
     */
    public static boolean contains(@Language("RegExp") String regex, CharSequence content) {
        return ReUtil.contains(regex, content);
    }
}
