package net.doge.util.core.text;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.moji4j.MojiConverter;
import net.doge.util.core.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @Author Doge
 * @Description 语言工具类
 * @Date 2020/12/15
 */
public class LangUtil {
    static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    private static MojiConverter mojiConverter = new MojiConverter();

    static {
        // 拼音小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不带声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // u 用 v 代替
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 比较两个字符串大小（中文使用拼音比较）
     *
     * @param s1
     * @param s2
     * @return
     */
    public static int compare(String s1, String s2) throws BadHanyuPinyinOutputFormatCombination {
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        for (int i = 0, len = Math.min(s1.length(), s2.length()); i < len; i++) {
            char c1 = s1.charAt(i), c2 = s2.charAt(i);
            if (c1 == c2) continue;
            if ((String.valueOf(c1)).matches("[\\u4E00-\\u9FA5]+") && (String.valueOf(c2)).matches("[\\u4E00-\\u9FA5]+"))
                return PinyinHelper.toHanyuPinyinStringArray(c1, format)[0].compareTo(PinyinHelper.toHanyuPinyinStringArray(c2, format)[0]);
            return c1 - c2;
        }
        return s1.length() - s2.length();
    }

    /**
     * 转为繁体中文
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            String s1 = mojiConverter.convertKanaToRomaji(String.valueOf(ch));
            // 遇到片假、平假加空格隔开
            if (s1.indexOf(ch) < 0) {
                if (i != 0) sb.append(' ');
                sb.append(s1);
                if (i != len - 1) sb.append(' ');
            } else sb.append(s1);
        }
        // 将连续空格缩成一个
        return StringUtil.shortenBlank(sb.toString());
    }

    /**
     * 格式化数字使其带中文单位(万、亿等)
     *
     * @param n
     * @return
     */
    public static String formatNumber(long n) {
        return formatNumberWithoutSuffix(n) + " 播放";
    }

    /**
     * 格式化数字使其带中文单位(万、亿等)
     *
     * @param n
     * @return
     */
    public static String formatNumberWithoutSuffix(long n) {
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
