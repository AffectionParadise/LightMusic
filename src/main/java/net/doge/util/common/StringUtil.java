package net.doge.util.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.moji4j.MojiConverter;
import net.doge.constant.ui.Fonts;
import net.doge.util.collection.ArrayUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javax.swing.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Doge
 * @Description 字符串工具类
 * @Date 2020/12/15
 */
public class StringUtil {
    private static final Map<Character, String> cMap = new HashMap<>();

    static {
        cMap.put(' ', "&nbsp;");
        cMap.put('<', "&lt;");
        cMap.put('>', "&gt;");
        cMap.put('\n', "<br>");
    }

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
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
     * 判断字符串是否为纯数字
     *
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        return StrUtil.isNumeric(s);
    }

    /**
     * 数字位数
     *
     * @param n
     * @return
     */
    public static int bit(int n) {
        return String.valueOf(n).length();
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
        if (s == null) return s;
        return s.replaceAll(" +", " ");
    }

    /**
     * 比较两个字符串大小
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
        return shortenBlank(sb.toString());
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
     * 解析数字 例如 7.6万 -> 76000
     *
     * @param s
     * @return
     */
    public static long parseNumber(String s) {
        if (s.contains("万")) return (long) (Double.parseDouble(s.replace("万", "").trim()) * 10000);
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

    /**
     * 从元素提取文本，替换 <br> 为 \n
     *
     * @param parentElement
     * @return
     */
    public static String getPrettyText(Element parentElement) {
        if (parentElement == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Node child : parentElement.childNodes()) {
            if (child instanceof TextNode) {
                sb.append(((TextNode) child).text());
            }
            if (child instanceof Element) {
                Element childElement = (Element) child;
                sb.append(getPrettyText(childElement));
                String s = childElement.tag().getName().toLowerCase();
                if ("br".equals(s) || "li".equals(s) || "p".equals(s) || "dd".equals(s)) sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtmlWithSpace(String text) {
        return textToHtml(text, true);
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text) {
        return textToHtml(text, false);
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示，不替换空格
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text, boolean withSpace) {
        if (text == null || text.startsWith("<html>") || text.trim().isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<html><div style=\"font-family:%s\">", Fonts.NORMAL.getFontName(Locale.ENGLISH)));
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);
            if ((withSpace || chars[0] != ' ') && cMap.containsKey(chars[0])) {
                sb.append(cMap.get(chars[0]));
                continue;
            }
            for (int j = 0, l = Fonts.TYPES.size(); j < l; j++) {
                if (Fonts.TYPES.get(j).canDisplay(codePoint)) {
                    // 中文
                    if (j == 0) sb.append(chars[0]);
                    else
                        sb.append(String.format("<span style=\"font-family:%s\">%s</span>", Fonts.TYPES.get(j).getFontName(), str));
                    i += chars.length - 1;
                    break;
                }
            }
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    /**
     * 去掉字符串中所有 HTML 标签，并将转义后的符号还原
     *
     * @param s
     * @return
     */
    public static String removeHTMLLabel(String s) {
        s = s.replaceAll("<br ?/?>", "\n");
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&apos;", "'");
    }

    /**
     * 字符串宽度 thresholdWidth 加 <br/> 换行
     *
     * @param text
     * @param thresholdWidth
     * @return
     */
    public static String wrapLineByWidth(String text, int thresholdWidth) {
        if (thresholdWidth < 0) return text;
        StringBuilder sb = new StringBuilder();
        int sw = 0;
        JLabel label = new JLabel();
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);

            for (int j = 0, l = Fonts.TYPES.size(); j < l; j++) {
                if (Fonts.TYPES.get(j).canDisplay(codePoint)) {
                    if (chars[0] != '\n') {
                        int tw = label.getFontMetrics(Fonts.TYPES.get(j)).stringWidth(str);
                        sw += tw;
                        if (sw >= thresholdWidth) {
                            sb.append('\n');
                            sw = tw;
                        }
                    } else sw = 0;
                    sb.append(str);
                    i += chars.length - 1;
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * url 编码（会处理所有冲突的字符）
     *
     * @param s
     * @return
     */
    public static String urlEncodeAll(String s) {
        return URLUtil.encodeAll(s);
    }

    /**
     * url 编码（处理空白字符）
     *
     * @param s
     * @return
     */
    public static String urlEncodeBlank(String s) {
        return URLUtil.encodeBlank(s);
    }

    /**
     * url 解码
     *
     * @param s
     * @return
     */
    public static String urlDecode(String s) {
        return URLUtil.decode(s);
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
     * 用 padStr 填充字符串 str 到指定长度
     *
     * @param str
     * @param len
     * @param padStr
     * @return
     */
    public static String padPre(String str, int len, String padStr) {
        return StrUtil.padPre(str, len, padStr);
    }
}
