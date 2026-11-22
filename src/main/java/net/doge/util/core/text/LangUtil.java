package net.doge.util.core.text;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.text.comparator.NameComparator;
import net.doge.util.core.text.helper.JapaneseRomanizerHelper;

/**
 * @author Doge
 * @description 语言工具类
 * @date 2020/12/15
 */
public class LangUtil {
    /**
     * 按照 UCA/CLDR 标准比较两个名称字符串大小，通常用于按名称排序
     *
     * @param s1
     * @param s2
     * @return
     */
    public static int compareName(String s1, String s2) {
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return NameComparator.getInstance().compare(s1, s2);
    }

    /**
     * 简体转为繁体
     *
     * @param s
     * @return
     */
    public static String toTraditionalChinese(String s) {
        return ZhConverterUtil.toTraditional(s);
    }

    /**
     * 日语转为罗马音
     *
     * @param s
     * @return
     */
    public static String toRomaji(String s) {
        return JapaneseRomanizerHelper.toRomaji(s);
    }

    /**
     * 格式化数字使其带中文单位(万、亿等)
     *
     * @param n
     * @return
     */
    public static String formatNumber(long n) {
        if (n < 10000) return String.valueOf(n);
        if (n < 100000000) return String.format("%.1f 万", (double) n / 10000).replace(".0", "");
        return String.format("%.1f 亿", (double) n / 100000000).replace(".0", "");
    }

    /**
     * 解析数字（关键词：k、w、千、万、亿）例如 7.6万 -> 76000，1.25k -> 1250
     *
     * @param s
     * @return
     */
    public static long parseNumber(String s) {
        if (StringUtil.isEmpty(s)) return 0;
        if (s.contains("k") || s.contains("千"))
            return (long) (Double.parseDouble(s.replaceAll("[k千]", "").trim()) * 1000);
        else if (s.contains("w") || s.contains("万"))
            return (long) (Double.parseDouble(s.replaceAll("[w万]", "").trim()) * 10000);
        else if (s.contains("亿")) return (long) (Double.parseDouble(s.replace("亿", "").trim()) * 100000000);
        return Long.parseLong(s.trim());
    }

    /**
     * 生成评分星星字符串
     *
     * @param n
     * @return
     */
    public static String genStar(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < n) sb.append('★');
            else sb.append('☆');
        }
        return sb.toString();
    }
}
