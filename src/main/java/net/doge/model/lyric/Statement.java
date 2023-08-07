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
    // 歌词
    private String lyric;

    public Statement(String lyric) {
        this.lyric = lyric;
    }

    public Statement(double time, String lyric) {
        this.time = time;
        this.lyric = lyric;
    }

    public boolean hasEndTime() {
        return endTime != 0;
    }

    // 歌词是否为空
    public boolean isEmpty() {
        return StringUtil.isEmpty(lyric);
    }

    @Override
    public String toString() {
        return lyric;
    }
}
