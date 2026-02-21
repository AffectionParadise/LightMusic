package net.doge.util.core.text;

import net.doge.constant.core.lyric.LyricPattern;
import net.doge.util.core.StringUtil;

/**
 * @author Doge
 * @description 歌词工具类
 * @date 2020/12/15
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

    /**
     * 根据是否使用逐字返回合适的歌词
     *
     * @param lyricStr
     * @param verbatimTimeline
     * @return
     */
    public static String getAppropriateLyricStr(String lyricStr, boolean verbatimTimeline) {
        if (lyricStr == null) return null;
        if (verbatimTimeline) return lyricStr;
        return lyricStr.replaceAll(LyricPattern.PAIR, "");
    }
}
