package net.doge.entity.core.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.util.core.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Doge
 * @description
 * @date 2020/12/7
 */
@Data
public class Statement implements Comparable<Statement> {
    // 开始时间
    private double time;
    // 结束时间
    private double endTime;
    // 歌词(可能带逐字时间轴)
    private String lyric;
    // 纯歌词
    private String plainLyric;
    // 子歌词(翻译/罗马音)
    private String subLyric;

    public Statement(String lyric) {
        this(0, lyric);
    }

    public Statement(double time, String lyric) {
        this(time, lyric, null);
    }

    public Statement(double time, String lyric, String subLyric) {
        this.time = time;
        setLyric(lyric);
        this.subLyric = subLyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
        plainLyric = lyric.replaceAll(LyricPattern.PAIR_FIND, "");
    }

    public boolean hasEndTime() {
        return endTime != 0;
    }

    public boolean notEmpty() {
        return StringUtil.notEmpty(plainLyric);
    }

    public boolean isEmpty() {
        return StringUtil.isEmpty(plainLyric);
    }

    public boolean hasSubLyric() {
        return StringUtil.notEmpty(subLyric);
    }

    @Override
    public String toString() {
        return "Time: " + time + "\nLyric:" + plainLyric
                + (hasSubLyric() ? "\nSubLyric:" + subLyric : "");
    }

    @Override
    public int compareTo(@NotNull Statement o) {
        return Double.compare(time, o.time);
    }
}
