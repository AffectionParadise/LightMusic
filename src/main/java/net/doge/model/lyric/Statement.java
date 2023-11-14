package net.doge.model.lyric;

import lombok.Data;
import net.doge.util.common.StringUtil;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class Statement {
    // 开始时间
    private double time;
    // 结束时间
    private double endTime;
    // 歌词(可能带逐字时间轴)
    private String lyric;
    // 纯歌词
    private String plainLyric;

    public Statement(String lyric) {
        setLyric(lyric);
    }

    public Statement(double time, String lyric) {
        this.time = time;
        setLyric(lyric);
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
        plainLyric = lyric.replaceAll("<\\d+,\\d+>", "");
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

    @Override
    public String toString() {
        return plainLyric;
    }
}
