package net.doge.util.common;

/**
 * @Author Doge
 * @Description 歌词工具类
 * @Date 2020/12/15
 */
public class LrcUtil {
    /**
     * 去除歌词字符串中无用的字符
     *
     * @param lrcStr
     * @return
     */
    public static String cleanLrcStr(String lrcStr) {
        return StringUtil.trimStringWith(lrcStr.replaceAll("[\t\r\n]", ""), ' ', ' ', '　');
    }
}
