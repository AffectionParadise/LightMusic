package net.doge.util.core;

import cn.hutool.core.util.StrUtil;
import net.doge.util.core.array.ArrayUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author Doge
 * @description 字符串工具类
 * @date 2020/12/15
 */
public class StringUtil {
    /**
     * 判断字符串是否为 null 或 ""
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * 判断字符串是否不为 null 和 ""
     *
     * @param s
     * @return
     */
    public static boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    /**
     * 从某位置插入
     *
     * @param s
     * @return
     */
    public static String insert(String s, int index, String content) {
        StringBuilder sb = new StringBuilder(s);
        sb.insert(index, content);
        return sb.toString();
    }

    /**
     * 缩短字符串
     *
     * @param s
     * @param maxLen
     * @return
     */
    public static String shorten(String s, int maxLen) {
        if (maxLen <= 3 || s == null || s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    /**
     * 缩短字符串中所有连续空格
     *
     * @param s
     * @return
     */
    public static String shortenBlank(String s) {
        if (s == null) return null;
        return s.replaceAll(" +", " ");
    }

    /**
     * 判断字符串是否为纯数字
     *
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        return StrUtil.isNumeric(s);
    }

    /**
     * 转为数字
     *
     * @param s
     * @return
     */
    public static int toNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 返回 s1 与 s2 相似度
     *
     * @param s1
     * @param s2
     * @return
     */
    public static double similar(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        return StrUtil.similar(s1, s2);
    }

    /**
     * 去除字符串前后指定字符
     *
     * @param str
     * @param cs
     * @return
     */
    public static String trimStringWith(String str, char... cs) {
        if (str == null) return null;
        char[] chars = str.toCharArray();
        int len = chars.length;
        int st = 0;
        while (st < len && ArrayUtil.in(cs, chars[st])) st++;
        while (st < len && ArrayUtil.in(cs, chars[len - 1])) len--;
        return st > 0 || len < chars.length ? str.substring(st, len) : str;
    }

    /**
     * 用 padStr 左填充字符串 str 到指定长度
     *
     * @param str
     * @param len
     * @param padStr
     * @return
     */
    public static String padPre(String str, int len, String padStr) {
        return StrUtil.padPre(str, len, padStr);
    }

    /**
     * 用 padStr 右填充字符 str 到指定长度
     *
     * @param str
     * @param len
     * @param padChar
     * @return
     */
    public static String padAfter(String str, int len, char padChar) {
        StringBuilder sb = new StringBuilder(str);
        // hutool 自带的 padAfter 算法有问题
        while (sb.length() < len) sb.append(padChar);
        return sb.toString();
    }

    /**
     * 修复字符串正确编码后的结果
     *
     * @param s
     * @return
     */
    public static String fixEncoding(String s) {
        if (isEmpty(s) || isReadableText(s)) return s;
        // 常见的编码尝试
        String[] encodings = {"UTF-8", "GBK", "GB2312", "ISO-8859-1", "Windows-1252", "Big5", "Shift_JIS"};
        for (String encoding : encodings) {
            try {
                // 尝试用不同编码重新解析
                byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
                String decoded = new String(bytes, encoding);
                // 检查解码后的字符串是否包含可读字符
                if (isReadableText(decoded)) return decoded;
            } catch (Exception e) {
                // 继续尝试下一个编码
            }
        }
        return s;
    }

    // 判断是否为可读文本
    private static boolean isReadableText(String text) {
        if (isEmpty(text)) return false;
        // 检查是否包含大量可读字符
        int readableCount = 0;
        for (int i = 0, l = text.length(); i < l; i++) {
            char ch = text.charAt(i);
            if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch) || CharUtil.isChineseCharacter(ch))
                readableCount++;
        }
        // 如果超过 70% 的字符可读，认为是有效文本
        return (double) readableCount / text.length() > 0.7;
    }
}
