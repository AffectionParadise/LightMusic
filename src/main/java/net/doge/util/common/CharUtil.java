package net.doge.util.common;

/**
 * @Author Doge
 * @Description 字符工具类
 * @Date 2020/12/15
 */
public class CharUtil {
    /**
     * 判断是否为中文字符
     *
     * @param c
     * @return
     */
    public static boolean isChineseCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION;
    }
}
