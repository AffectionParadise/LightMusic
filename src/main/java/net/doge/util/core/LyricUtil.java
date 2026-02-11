package net.doge.util.core;

/**
 * @Author Doge
 * @Description 歌词工具类
 * @Date 2020/12/15
 */
public class LyricUtil {
    /**
     * 去除歌词字符串中无用的字符
     *
     * @param lyricStr
     * @return
     */
    public static String cleanLyricStr(String lyricStr) {
        return StringUtil.trimStringWith(lyricStr.replaceAll("[\t\r\n]", ""), ' ', ' ', '　');
    }
}
