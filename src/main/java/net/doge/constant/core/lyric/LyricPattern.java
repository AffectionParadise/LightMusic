package net.doge.constant.core.lyric;

/**
 * @author Doge
 * @description 逐字歌词匹配模式
 * @date 2020/12/7
 */
public class LyricPattern {
    // 逐字时间轴
    // 用于匹配，特殊情况前面可能带负号
    public static final String PAIR_FIND = "<-?\\d+,\\d+>";
    public static final String PAIR_FMT = "<%s,%s>";
    public static final String PAIR_REP = "<$1,$2>";
    // 起始时间
    public static final String START = "<(\\d+),\\d+>";
    // 持续时间
    public static final String DURATION = "<\\d+,(\\d+)>";
}
